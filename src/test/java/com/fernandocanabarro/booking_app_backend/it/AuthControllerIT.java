package com.fernandocanabarro.booking_app_backend.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.ActivateAccountRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.NewPasswordRequestoDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.PasswordRecoverRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.utils.AccessTokenUtils;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@Sql(scripts = {"classpath:create_tables.sql", "classpath:insert_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("it")
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail, adminPassword;
    private String guestEmail, guestPassword;
    private String adminBearerToken;
    private RegistrationRequestDTO registrationRequest;
    private UserSelfUpdateInfosRequestDTO userSelfUpdateInfosRequest;
    private UserSelfUpdatePasswordRequestDTO userSelfUpdatePasswordRequest;
    
    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine");
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    public void setup() throws Exception {
        this.adminEmail = "fernando@gmail.com";
        this.adminPassword = "12345Az@";
        this.guestEmail = "anita@gmail.com";
        this.guestPassword = "12345Az@";
        this.adminBearerToken = "Bearer " + AccessTokenUtils.obtainAccessToken(adminEmail, adminPassword, mockMvc, objectMapper);

        this.registrationRequest = new RegistrationRequestDTO();
        registrationRequest.setFullName("name");
        registrationRequest.setEmail("email@gmail.com");
        registrationRequest.setCpf("241.989.790-06");
        registrationRequest.setPhone("(11) 99999-9999");
        registrationRequest.setBirthDate(LocalDate.of(2005,10, 28));
        registrationRequest.setPassword("12345Az@");

        this.userSelfUpdateInfosRequest = new UserSelfUpdateInfosRequestDTO();
        userSelfUpdateInfosRequest.setFullName("Lucas Pereira");
        userSelfUpdateInfosRequest.setEmail("lucaspereira@gmail.com");
        userSelfUpdateInfosRequest.setCpf("786.857.060-17");
        userSelfUpdateInfosRequest.setPhone("(11) 99999-9999");
        userSelfUpdateInfosRequest.setBirthDate(LocalDate.of(2006,8, 24));

        this.userSelfUpdatePasswordRequest = new UserSelfUpdatePasswordRequestDTO();
        userSelfUpdatePasswordRequest.setCurrentPassword("12345Az@");
        userSelfUpdatePasswordRequest.setPassword("12345678Az@");
    }

    @Test
    public void registerShouldReturnStatus409WhenEmailIsAlreadyInUse() throws Exception {
        registrationRequest.setEmail(adminEmail);
        mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registrationRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    public void registerShouldReturnStatus409WhenCpfIsAlreadyInUse() throws Exception {
        registrationRequest.setCpf("329.949.250-01");
        mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registrationRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    public void registerShouldReturnStatus201WhenDataIsValid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registrationRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    public void loginShouldReturnStatus401WhenCredentialsAreInvalidInBothGuestAndAdminLogin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO("email", "password"))))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/auth/login/admin")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO("email", "password"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminLoginShouldReturnStatus403WhenGuestUserTriesToLoginAsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login/admin")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO(guestEmail, guestPassword))))
            .andExpect(status().isForbidden());
    }

    @Test
    public void loginShouldReturnStatus200WhenCredentialsAreValidInBothGuestAndAdminLogin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO(guestEmail, guestPassword))))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/auth/login/admin")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO(adminEmail, adminPassword))))
            .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    public void loginShouldReturnStatus403WhenUserAccountIsNotActivated() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO("william@gmail.com", "12345Az@"))))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    public void activateAccountShouldReturnStatus200WhenCodeExistsAndIsValid() throws Exception {
        mockMvc.perform(put("/api/v1/auth/activate-account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ActivateAccountRequestDTO("123456"))))
            .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void activateAccountShouldReturnStatus400WhenCodeExistsButIsAlreadyExpired() throws Exception {
        mockMvc.perform(put("/api/v1/auth/activate-account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ActivateAccountRequestDTO("654321"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void activateAccountShouldReturnStatus404WhenCodeDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/auth/activate-account")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ActivateAccountRequestDTO("000000"))))
            .andExpect(status().isNotFound());
    }

    @Test
    public void validateJWTTokenShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/auth/token/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void validateJWTTokenShouldReturnStatus403WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/auth/token/validate")
            .header("Authorization", "Bearer invalid")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void validateJWTTokenShouldReturnStatus200WhenTokenIsValid() throws Exception {
        mockMvc.perform(get("/api/v1/auth/token/validate")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void userSelfUpdateInfosShouldReturnStatus401WheAuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/auth/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdateInfosRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    public void userSelfUpdateInfosShouldReturnStatus409WhenEmailIsAlreadyInUseByAnotherUser() throws Exception {
        userSelfUpdateInfosRequest.setEmail(adminEmail);
        String userSelfUpdateToken = "Bearer " + AccessTokenUtils.obtainAccessToken("pereira@gmail.com", "12345Az@", mockMvc, objectMapper);
        mockMvc.perform(put("/api/v1/auth/profile")
            .header("Authorization", userSelfUpdateToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdateInfosRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(5)
    public void userSelfUpdateInfosShouldReturnStatus409WhenCpfIsAlreadyInUseByAnotherUser() throws Exception {
        userSelfUpdateInfosRequest.setCpf("329.949.250-01");
        String userSelfUpdateToken = "Bearer " + AccessTokenUtils.obtainAccessToken("pereira@gmail.com", "12345Az@", mockMvc, objectMapper);
        mockMvc.perform(put("/api/v1/auth/profile")
            .header("Authorization", userSelfUpdateToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdateInfosRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(6)
    public void userSelfUpdateInfosShouldReturnStatus200WhenDataIsValid() throws Exception {
        String userSelfUpdateToken = "Bearer " + AccessTokenUtils.obtainAccessToken("pereira@gmail.com", "12345Az@", mockMvc, objectMapper);
        mockMvc.perform(put("/api/v1/auth/profile")
            .header("Authorization", userSelfUpdateToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdateInfosRequest)))
            .andExpect(status().isOk());
    }

    @Test
    public void userSelfUpdatePasswordShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/auth/profile/password")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdatePasswordRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    public void userSelfUpdatePasswordShouldReturnStatus409WhenCurrentPasswordIsInvalid() throws Exception {
        String userSelfUpdatePasswordToken = "Bearer " + AccessTokenUtils.obtainAccessToken("lucaspereira@gmail.com", "12345Az@", mockMvc, objectMapper);
        userSelfUpdatePasswordRequest.setCurrentPassword("invalid");
        mockMvc.perform(put("/api/v1/auth/profile/password")
            .header("Authorization", userSelfUpdatePasswordToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdatePasswordRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(8)
    public void userSelfUpdatePasswordShouldReturnStatus200WhenDataIsValid() throws Exception {
        String userSelfUpdatePasswordToken = "Bearer " + AccessTokenUtils.obtainAccessToken("lucaspereira@gmail.com", "12345Az@", mockMvc, objectMapper);
        mockMvc.perform(put("/api/v1/auth/profile/password")
            .header("Authorization", userSelfUpdatePasswordToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSelfUpdatePasswordRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void sendPasswordRecoverRequestEmailShouldReturnStatus200WhenEmailExists() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PasswordRecoverRequestDTO("william@gmail.com"))))
            .andExpect(status().isOk());
    }

    @Test
    public void sendPasswordRecoverRequestEmailShouldReturnStatus404WhenEmailDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PasswordRecoverRequestDTO("email"))))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    public void resetPasswordShouldReturnStatus200WhenCodeExistsAndIsValid() throws Exception {
        NewPasswordRequestoDTO newPasswordRequest = new NewPasswordRequestoDTO();
        newPasswordRequest.setCode("123456");
        newPasswordRequest.setPassword("12345678Az@");
        mockMvc.perform(put("/api/v1/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newPasswordRequest)))
            .andExpect(status().isOk());
    }

    @Test
    public void resetPasswordShouldReturnStatus400WhenCodeIsExpired() throws Exception {
        NewPasswordRequestoDTO newPasswordRequest = new NewPasswordRequestoDTO();
        newPasswordRequest.setCode("654321");
        newPasswordRequest.setPassword("12345678Az@");
        mockMvc.perform(put("/api/v1/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newPasswordRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void resetPasswordShouldReturnStatus404WhenCodeDoesNotExist() throws Exception {
        NewPasswordRequestoDTO newPasswordRequest = new NewPasswordRequestoDTO();
        newPasswordRequest.setCode("0000000000");
        newPasswordRequest.setPassword("12345678Az@");
        mockMvc.perform(put("/api/v1/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newPasswordRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void verifyEmailShouldReturnStatus200AndTrueResponseWhenEmailExists() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verify-email")
            .param("email", guestEmail)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alreadyExists").value(true));
    }

    @Test
    public void verifyEmailShouldReturnStatus200AndFalseResponseWhenEmailDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verify-email")
            .param("email", "email")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alreadyExists").value(false));
    }

    @Test
    public void verifyCpfShouldReturnStatus200AndTrueResponseWhenCpfExists() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verify-cpf")
            .param("cpf", "329.949.250-01")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alreadyExists").value(true));
    }

    @Test
    public void verifyCpfShouldReturnStatus200AndFalseResponseWhenCpfDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verify-cpf")
            .param("cpf", "cpf")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alreadyExists").value(false));
    }

}
