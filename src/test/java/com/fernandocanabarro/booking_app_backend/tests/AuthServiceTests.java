package com.fernandocanabarro.booking_app_backend.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;

import com.fernandocanabarro.booking_app_backend.factories.RoleFactory;
import com.fernandocanabarro.booking_app_backend.factories.UserFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.NewPasswordRequestoDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.PasswordRecoverRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.ActivateAccountRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AlreadyExistsResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.ActivationCode;
import com.fernandocanabarro.booking_app_backend.models.entities.PasswordRecover;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.ActivationCodeRepository;
import com.fernandocanabarro.booking_app_backend.repositories.PasswordRecoverRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoleRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.services.EmailService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ExpiredCodeException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.InvalidCurrentPasswordException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.UnauthorizedException;
import com.fernandocanabarro.booking_app_backend.services.impl.AuthServiceImpl;
import com.fernandocanabarro.booking_app_backend.utils.UserUtils;
import com.sendgrid.helpers.mail.Mail;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("it")
public class AuthServiceTests {

    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtDecoder jwtDecoder;
    @Mock
    private PasswordRecoverRepository passwordRecoverRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private UserUtils userUtils;
    @Mock
    private ActivationCodeRepository activationCodeRepository;

    private User user;
    private RegistrationRequestDTO registrationRequest;
    private LoginRequestDTO loginRequest;
    private Jwt jwt;
    private Authentication authentication;
    private UserSelfUpdateInfosRequestDTO userSelfUpdateInfosRequest;
    private UserSelfUpdatePasswordRequestDTO userSelfUpdatePasswordRequest;
    private PasswordRecover passwordRecover;
    private NewPasswordRequestoDTO newPasswordRequest;
    private ActivationCode activationCode;

    @BeforeEach
    public void setup() {
        this.user = UserFactory.createUser();
        this.registrationRequest = UserFactory.createRegistrationRequestDTO();
        this.loginRequest = new LoginRequestDTO(user.getEmail(), user.getPassword());
        this.jwt = Jwt.withTokenValue("token")
            .headers(headers -> {
                headers.put("alg", "HS256");
                headers.put("typ", "JWT");
            })
            .claim("username", user.getEmail())
            .build();
        this.authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        this.userSelfUpdateInfosRequest = UserFactory.createUserSelfUpdateInfosRequestDTO();
        this.userSelfUpdatePasswordRequest = UserFactory.createUserSelfUpdatePasswordRequestDTO();
        this.passwordRecover = new PasswordRecover(1L,"code", user, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30L), false, null);
        this.newPasswordRequest = new NewPasswordRequestoDTO();
        this.newPasswordRequest.setCode(passwordRecover.getCode());
        this.newPasswordRequest.setPassword("newPassword");
        this.activationCode = new ActivationCode(1L, "code", "email", LocalDateTime.now(), 
            LocalDateTime.now().plusMinutes(30L), false, null);
    }

    @Test
    public void guestLoginShouldReturnLoginResponseDTOWhenLoginIsValid() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        LoginResponseDTO response = authService.login(loginRequest, false);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(jwt.getTokenValue());
        assertThat(response.getExpiresIn()).isEqualTo(86400L);
    }

     @Test
    public void adminLoginShouldReturnLoginResponseDTOWhenLoginIsValid() {
        user.addRole(RoleFactory.createOperatorRole());
        user.addRole(RoleFactory.createAdminRole());
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        LoginResponseDTO response = authService.login(loginRequest, true);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(jwt.getTokenValue());
        assertThat(response.getExpiresIn()).isEqualTo(86400L);
    }

    @Test
    public void loginShouldThrowForbiddenExceptionWhenLoginIsAdminLoginButUserDoesNotHaveOperatorOrAdminRole() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        assertThatThrownBy(() -> authService.login(loginRequest, true)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void loginShouldThrowForbiddenExceptionWhenUserAccountIsNotActivated() {
        user.setActivated(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        assertThatThrownBy(() -> authService.login(loginRequest, false)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void registerShouldThrowNoExceptionWhenDataIsValid() {
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByCpf(registrationRequest.getCpf())).thenReturn(Optional.empty());
        when(roleRepository.findByAuthority("ROLE_GUEST")).thenReturn(RoleFactory.createGuestRole());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(activationCodeRepository.save(any(ActivationCode.class))).thenReturn(activationCode);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> authService.register(registrationRequest)).doesNotThrowAnyException();
    }

    @Test
    public void registerShouldThrowAlreadyExistingPropertyExceptionWhenEmailIsAlreadyInUse() {
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(registrationRequest)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void registerShouldThrowAlreadyExistingPropertyExceptionWhenCpfIsAlreadyInUse() {
        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByCpf(registrationRequest.getCpf())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(registrationRequest)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void activateAccountShouldThrowNoExceptionWhenCodeIsValid() {
        when(activationCodeRepository.findByCode(activationCode.getCode())).thenReturn(Optional.of(activationCode));
        when(userRepository.findByEmail(activationCode.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(activationCodeRepository.save(any(ActivationCode.class))).thenReturn(activationCode);

        assertThatCode(() -> authService.activateAccount(new ActivateAccountRequestDTO("code"))).doesNotThrowAnyException();
    }

    @Test
    public void activateAccountShouldThrowExpiredCodeExceptionWhenCodeIsExpired() {
        activationCode.setExpiresAt(LocalDateTime.now().minusMinutes(5L));
        when(activationCodeRepository.findByCode(activationCode.getCode())).thenReturn(Optional.of(activationCode));
        when(userRepository.findByEmail(activationCode.getEmail())).thenReturn(Optional.of(user));
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatThrownBy(() -> authService.activateAccount(new ActivateAccountRequestDTO("code"))).isInstanceOf(ExpiredCodeException.class);
    }

    @Test
    public void activateAccountShouldThrowResourceNotFoundExceptionWhenCodeDoesNotExist() {
        when(activationCodeRepository.findByCode(activationCode.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.activateAccount(new ActivateAccountRequestDTO("code"))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void activateAccountShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(activationCodeRepository.findByCode(activationCode.getCode())).thenReturn(Optional.of(activationCode));
        when(userRepository.findByEmail(activationCode.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.activateAccount(new ActivateAccountRequestDTO("code"))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void getConnectedUserShouldReturnUser() {
        when(userUtils.getConnectedUserEmail()).thenReturn("email");
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        User response = authService.getConnectedUser();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("email");
        assertThat(response.getCpf()).isEqualTo("cpf");
    }

    @Test
    public void getConnectedUserShouldThrowUnauthorizedExceptionWhenUserDoesNotExist() {
        when(userUtils.getConnectedUserEmail()).thenReturn("email");
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getConnectedUser()).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    public void getMeShouldReturnUserResponseDTO() {
        when(userUtils.getConnectedUserEmail()).thenReturn("email");
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        UserResponseDTO response = authService.getMe();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("email");
        assertThat(response.getCpf()).isEqualTo("cpf");
    }

    @Test
    public void validateJWTTokenShouldThrowNoExceptionWhenTokenIsValid() {
        when(jwtDecoder.decode(any(String.class))).thenReturn(jwt);

        assertThatCode(() -> authService.validateJWTToken("token")).doesNotThrowAnyException();
    }

    @Test
    public void validateJWTTOkenShouldThrowUnauthorizedExceptionWhenTokenIsNotProvidedOrIsNull() {
        assertThatThrownBy(() -> authService.validateJWTToken(null)).isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> authService.validateJWTToken("")).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    public void validateJWTTokenShouldThrowForbiddenExceptionWhenTokenIsInvalid() {
        when(jwtDecoder.decode(any(String.class))).thenThrow(new JwtException("Invalid token"));

        assertThatThrownBy(() -> authService.validateJWTToken("token")).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void userSelfUpdateInfosShouldThrowNoExceptionWhenDataIsValid() {
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);
        when(userRepository.findByEmail(userSelfUpdateInfosRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> spy.userSelfUpdateInfos(userSelfUpdateInfosRequest)).doesNotThrowAnyException();
    }

    @Test
    public void userSelfUpdateInfosShouldThrowAlreadyExistingPropertyExceptionWhenEmailIsAlreadyInUse() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("email");
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);
        when(userRepository.findByEmail(userSelfUpdateInfosRequest.getEmail())).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> spy.userSelfUpdateInfos(userSelfUpdateInfosRequest)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void userSelfUpdatePasswordShouldThrowNoExceptionWhenCurrentPasswordIsValid() {
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);
        when(passwordEncoder.matches(userSelfUpdatePasswordRequest.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(userSelfUpdatePasswordRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> spy.userSelfUpdatePassword(userSelfUpdatePasswordRequest)).doesNotThrowAnyException();
    }

    @Test
    public void userSelfUpdatePasswordShouldThrowInvalidCurrentPasswordExceptionWhenCurrentPasswordIsInvalid() {
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);
        when(passwordEncoder.matches(userSelfUpdatePasswordRequest.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> spy.userSelfUpdatePassword(userSelfUpdatePasswordRequest)).isInstanceOf(InvalidCurrentPasswordException.class);
    }

    @Test
    public void forgotPasswordShouldThrowNoExceptionWhenEmailExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordRecoverRepository.save(any())).thenReturn(passwordRecover);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> authService.forgotPassword(new PasswordRecoverRequestDTO(user.getEmail()))).doesNotThrowAnyException();
    }

    @Test
    public void forgotPasswordShouldThrowResourceNotFoundExceptionWhenEmailDoesNotExist() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.forgotPassword(new PasswordRecoverRequestDTO(user.getEmail())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void resetPasswordShouldThrowNoExceptionWhenCodeIsValid() {
        when(passwordRecoverRepository.findByCode(passwordRecover.getCode())).thenReturn(Optional.of(passwordRecover));
        when(passwordRecoverRepository.save(any(PasswordRecover.class))).thenReturn(passwordRecover);
        when(passwordEncoder.encode(newPasswordRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> authService.resetPassword(newPasswordRequest)).doesNotThrowAnyException();
    }

    @Test
    public void resetPasswordShouldThrowResourceNotFoundExceptionWhenCodeDoesNotExist() {
        when(passwordRecoverRepository.findByCode(passwordRecover.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword(newPasswordRequest)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void resetPasswordShouldThrowExpiredCodeExceptionWhenCodeIsExpired() {
        passwordRecover.setExpiresAt(LocalDateTime.now().minusMinutes(5L));
        when(passwordRecoverRepository.findByCode(passwordRecover.getCode())).thenReturn(Optional.of(passwordRecover));

        assertThatThrownBy(() -> authService.resetPassword(newPasswordRequest)).isInstanceOf(ExpiredCodeException.class);
    }

    @Test
    public void verifyIfUserExistsByEmailShouldReturnAlreadyExistsResponseDTOTrueWhenUserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        AlreadyExistsResponseDTO response = authService.verifyIfUserExistsByEmail(user.getEmail());

        assertThat(response).isNotNull();
        assertThat(response.isAlreadyExists()).isTrue();
    }

    @Test
    public void verifyIfUserExistsByEmailShouldReturnAlreadyExistsResponseDTOFalseWhenUserDoesNotExist() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        AlreadyExistsResponseDTO response = authService.verifyIfUserExistsByEmail(user.getEmail());

        assertThat(response).isNotNull();
        assertThat(response.isAlreadyExists()).isFalse();
    }

    @Test
    public void verifyIfUserExistsByCpfShouldReturnAlreadyExistsResponseDTOTrueWhenUserExists() {
        when(userRepository.findByCpf(user.getCpf())).thenReturn(Optional.of(user));

        AlreadyExistsResponseDTO response = authService.verifyIfUserExistsByCpf(user.getCpf());

        assertThat(response).isNotNull();
        assertThat(response.isAlreadyExists()).isTrue();
    }

    @Test
    public void verifyIfUserExistsByCpfShouldReturnAlreadyExistsResponseDTOFalseWhenUserDoesNotExist() {
        when(userRepository.findByCpf(user.getCpf())).thenReturn(Optional.empty());

        AlreadyExistsResponseDTO response = authService.verifyIfUserExistsByCpf(user.getCpf());

        assertThat(response).isNotNull();
        assertThat(response.isAlreadyExists()).isFalse();
    }

    @Test
    public void verifyIfConnectedUserHasAdminPermissionShouldThrowNoExceptionWhenUserIsOperatorOrAdmin() {
        user.addRole(RoleFactory.createOperatorRole());
        user.addRole(RoleFactory.createAdminRole());
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);

        assertThatCode(() -> spy.verifyIfConnectedUserHasAdminPermission(99L)).doesNotThrowAnyException();
    }

    @Test
    public void verifyIfConnectedUserHasAdminPermissionShouldThrowNoExceptionWhenConnectedUserIsTheOwnerOfTheResource() {
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);

        assertThatCode(() -> spy.verifyIfConnectedUserHasAdminPermission(1L)).doesNotThrowAnyException();
    }

    @Test
    public void verifyIfConnectedUserHasAdminPermissionShouldThrowForbiddenExceptionWhenConnectedUserIsNotTheOwnerOfTheResource() {
        AuthServiceImpl spy = spy(authService);
        when(userUtils.getConnectedUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(spy.getConnectedUser()).thenReturn(user);

        assertThatThrownBy(() -> spy.verifyIfConnectedUserHasAdminPermission(99L)).isInstanceOf(ForbiddenException.class);
    }


}
