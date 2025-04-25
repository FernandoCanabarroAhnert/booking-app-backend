package com.fernandocanabarro.booking_app_backend.services.impl;

import java.time.Instant;
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
import com.fernandocanabarro.booking_app_backend.models.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Role;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.RoleRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.UnauthorizedException;

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
    public void verifyToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split("")[1];
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

    

}
