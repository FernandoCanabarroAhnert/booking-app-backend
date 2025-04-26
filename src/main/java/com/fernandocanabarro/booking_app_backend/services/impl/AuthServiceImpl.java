package com.fernandocanabarro.booking_app_backend.services.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.UserMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.NewPasswordRequestoDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.PasswordRecoverRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.PasswordRecover;
import com.fernandocanabarro.booking_app_backend.models.entities.Role;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.PasswordRecoverRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoleRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.EmailService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ExpiredCodeException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.InvalidCurrentPasswordException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.UnauthorizedException;
import com.fernandocanabarro.booking_app_backend.utils.UserUtils;
import com.sendgrid.helpers.mail.Mail;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder;
    private final PasswordRecoverRepository passwordRecoverRepository;
    private final EmailService emailService;

    private final long SECONDS_IN_A_DAY = 86400L;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword());
        Authentication authenticated = authenticationManager.authenticate(authentication);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("booking-app-backend-auth-service")
                .subject(authenticated.getName())
                .claim("authorities", authenticated.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(SECONDS_IN_A_DAY))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new LoginResponseDTO(token, SECONDS_IN_A_DAY);
    }

    @Override
    @Transactional
    public void register(RegistrationRequestDTO request) {
        Optional<User> UserByEmail = this.userRepository.findByEmail(request.getEmail());
        if (UserByEmail.isPresent()) {
            throw new AlreadyExistingPropertyException("E-mail");
        }
        Optional<User> UserByCpf = this.userRepository.findByCpf(request.getCpf());
        if (UserByCpf.isPresent()) {
            throw new AlreadyExistingPropertyException("CPF");
        }
        User user = UserMapper.convertRequestToEntity(request, passwordEncoder);
        Role role = roleRepository.findByAuthority("ROLE_GUEST");
        user.addRole(role);
        userRepository.save(user);
    }

    @Override
    public User getConnectedUser() {
        String email = UserUtils.getConnectedUserEmail();
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User is not logged in"));
        return user;
    }

    @Override
    public void verifyToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Token not provided");
        }
        try {
            jwtDecoder.decode(token);
        }
        catch (JwtException ex) {
            throw new ForbiddenException("Invalid token");
        }
    }

    @Override
    public void userSelfUpdateInfos(UserSelfUpdateInfosRequestDTO request) {
        User user = this.getConnectedUser();
        Optional<User> UserByEmail = this.userRepository.findByEmail(request.getEmail());
        if (UserByEmail.isPresent()) {
            if (!UserByEmail.get().getId().equals(user.getId())) {
                throw new AlreadyExistingPropertyException("E-mail");
            }
        }
        Optional<User> UserByCpf = this.userRepository.findByCpf(request.getCpf());
        if (UserByCpf.isPresent()) {
            if (!UserByCpf.get().getId().equals(user.getId())) {
                throw new AlreadyExistingPropertyException("CPF");
            }
        }
        UserMapper.updateUser(user, request);
        userRepository.save(user);
    }

    @Override
    public void userSelfUpdatePassword(UserSelfUpdatePasswordRequestDTO request) {
        User user = this.getConnectedUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void sendPasswordRecoverEmail(PasswordRecoverRequestDTO request) {
        User user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with " + request.getEmail() + "email not found"));
        String code = this.generateCode();
        PasswordRecover passwordRecover = PasswordRecover.builder()
            .code(code)
            .user(user)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(30L))
            .used(false)
            .usedAt(null)
            .build();
        passwordRecoverRepository.save(passwordRecover);
        Mail mail = this.emailService.createEmail(user.getFullName(), user.getEmail(), code);
        this.emailService.sendEmail(mail);
    }

    private String generateCode() {
        String chars = "0123456789";
        int length = chars.length();
        StringBuilder builder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(length);
            builder.append(chars.charAt(randomIndex));
        }
        return builder.toString();
    }

    @Override
    public void setNewPasswordFromPasswordRecover(NewPasswordRequestoDTO request) {
        Optional<PasswordRecover> passwordRecover = this.passwordRecoverRepository.findByCode(request.getCode());
        if (passwordRecover.isEmpty()) {
            throw new ResourceNotFoundException("Password recover  with code " + request.getCode() + " not found");
        }
        if (!passwordRecover.get().isValid()) {
            throw new ExpiredCodeException();
        }
        passwordRecover.get().setUsed(true);
        passwordRecover.get().setUsedAt(LocalDateTime.now());
        passwordRecoverRepository.save(passwordRecover.get());
        User user = passwordRecover.get().getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserWithPropertyAlreadyExistsDTO verifyIfUserExistsByEmail(String email) {
        Optional<User> UserByEmail = this.userRepository.findByEmail(email);
        return new UserWithPropertyAlreadyExistsDTO(UserByEmail.isPresent());
    }

    @Override
    public UserWithPropertyAlreadyExistsDTO verifyIfUserExistsByCpf(String cpf) {
        Optional<User> UserByCpf = this.userRepository.findByCpf(cpf);
        return new UserWithPropertyAlreadyExistsDTO(UserByCpf.isPresent());
    }

    @Override
    public void verifyIfConnectedUserHasAdminPermission(Long id) {
        User connectedUser = this.getConnectedUser();
        if (!connectedUser.getId().equals(id)) {
            boolean hasAdminPermission = connectedUser.hasRole("ROLE_OPERATOR") || connectedUser.hasRole("ROLE_ADMIN");
            if (hasAdminPermission) {
                return;
            }
            throw new ForbiddenException("User does not have permission to perform this action");
        }
    }

    
    

}
