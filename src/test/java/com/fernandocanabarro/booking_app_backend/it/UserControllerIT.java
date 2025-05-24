package com.fernandocanabarro.booking_app_backend.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
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
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.utils.AccessTokenUtils;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@Sql(scripts = {"classpath:create_tables.sql", "classpath:insert_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("it")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail, adminPassword;
    private String guestEmail, guestPassword;
    private String adminBearerToken, guestBearerToken;
    private Long existingId, nonExistingId, createdUserId;
    private AdminCreateUserRequestDTO adminCreateUserRequest;
    private AdminUpdateUserRequestDTO adminUpdateUserRequest;
    
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
        this.guestBearerToken = "Bearer " + AccessTokenUtils.obtainAccessToken(guestEmail, guestPassword, mockMvc, objectMapper);
        this.existingId = 1L;
        this.nonExistingId = 1000L;
        this.createdUserId = 5L;

        this.adminCreateUserRequest = new AdminCreateUserRequestDTO();
        adminCreateUserRequest.setFullName("name");
        adminCreateUserRequest.setEmail("email@gmail.com");
        adminCreateUserRequest.setCpf("241.989.790-06");
        adminCreateUserRequest.setPhone("(11) 99999-9999");
        adminCreateUserRequest.setBirthDate(LocalDate.of(2005,10, 28));
        adminCreateUserRequest.setPassword("12345Az@");
        adminCreateUserRequest.setActivated(true);
        adminCreateUserRequest.setRolesIds(new ArrayList<>(Arrays.asList(1L)));

        this.adminUpdateUserRequest = new AdminUpdateUserRequestDTO();
        adminUpdateUserRequest.setFullName("name");
        adminUpdateUserRequest.setEmail("email@gmail.com");
        adminUpdateUserRequest.setCpf("241.989.790-06");
        adminUpdateUserRequest.setPhone("(11) 99999-9999");
        adminUpdateUserRequest.setBirthDate(LocalDate.of(2005,10, 28));
        adminUpdateUserRequest.setActivated(true);
        adminUpdateUserRequest.setRolesIds(new ArrayList<>(Arrays.asList(1L)));
    }

    @Test
    public void findAllShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(get("/api/v1/users")
            .header("Authorization", guestBearerToken))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findAllShouldReturnStatus200AndPageOfUserResponseDTOWhenAdminOrOperatorIsLogged() throws Exception {
        mockMvc.perform(get("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].fullName").value("Fernando"))
            .andExpect(jsonPath("$.content[0].email").value("fernando@gmail.com"))
            .andExpect(jsonPath("$.content[0].phone").value("(51) 1234-12345"))
            .andExpect(jsonPath("$.content[0].cpf").value("329.949.250-01"))
            .andExpect(jsonPath("$.content[0].birthDate").value("2005-10-28"));
    }

    @Test
    public void findAllByCpfShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/search?cpf={cpf}", "329.949.250-01")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllByCpfShouldReturnStatus403WhenUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(get("/api/v1/users/search?cpf={cpf}", "329.949.250-01")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findAllByCpfShouldReturnStatus200AndListOfUserSearchResponseDTOWhenAdminOrOperatorIsLogged() throws Exception {
        mockMvc.perform(get("/api/v1/users/search?cpf={cpf}", "329.949.250-01")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].cpf").value("329.949.250-01"));
    }

    @Test
    public void findByIdShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", existingId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", existingId)
            .header("Authorization", guestBearerToken))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnStatus200AndPageOfUserResponseDTOWhenAdminOrOperatorIsLogged() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.fullName").value("Fernando"))
            .andExpect(jsonPath("$.email").value("fernando@gmail.com"))
            .andExpect(jsonPath("$.phone").value("(51) 1234-12345"))
            .andExpect(jsonPath("$.cpf").value("329.949.250-01"))
            .andExpect(jsonPath("$.birthDate").value("2005-10-28"));
    }

    @Test
    public void findByIdShouldReturnStatu404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", nonExistingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createUserShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createUserShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    public void createUserShouldReturnStatus201WhenDataIsValid() throws Exception {
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    @Order(7)
    public void createUserShouldReturnStatus201WhenUserRequestHasOperatorOrAdminRoleAndWorkingHotelIdIsPresentAndHotelExists() throws Exception {
        adminCreateUserRequest.setEmail("email2@gmail.com");
        adminCreateUserRequest.setCpf("326.477.300-75");
        adminCreateUserRequest.getRolesIds().add(2L);
        adminCreateUserRequest.getRolesIds().add(3L);
        adminCreateUserRequest.setWorkingHotelId(1L);
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    @Order(1)
    public void createUserShouldReturnStatus400WhenUserRequestHasOperatorOrAdminRoleButWorkingHotelIdIsNull() throws Exception {
        adminCreateUserRequest.getRolesIds().add(2L);
        adminCreateUserRequest.getRolesIds().add(3L);
        adminCreateUserRequest.setWorkingHotelId(null);
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    public void createUserShouldReturnStatus404WhenUserRequestHasOperatorOrAdminRoleAndWorkingHotelIdIsPresentButHotelDoesNotExist() throws Exception {
        adminCreateUserRequest.getRolesIds().add(2L);
        adminCreateUserRequest.getRolesIds().add(3L);
        adminCreateUserRequest.setWorkingHotelId(nonExistingId);
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void createUserShouldReturnStatus404WhenRoleDoesNotExist() throws Exception {
        adminCreateUserRequest.getRolesIds().add(nonExistingId);
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    public void createUserShouldReturnStatus409WhenEmailIsAlreadyInUse() throws Exception {
        adminCreateUserRequest.setEmail(guestEmail);
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    public void createUserShouldReturnStatus409WhenCpfIsAlreadyInUse() throws Exception {
        adminCreateUserRequest.setCpf("329.949.250-01");
        mockMvc.perform(post("/api/v1/users")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminCreateUserRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    public void updateUserShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUserShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    public void updateUserShouldReturnStatus200WhenDataIsValid() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @Order(14)
    public void updateUserShouldReturnStatus201WhenUserRequestHasOperatorOrAdminRoleAndWorkingHotelIdIsPresentAndHotelChangedAndItExists() throws Exception {
        adminUpdateUserRequest.getRolesIds().add(2L);
        adminUpdateUserRequest.getRolesIds().add(3L);
        adminUpdateUserRequest.setWorkingHotelId(2L);
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isOk());
    }

    @Test
    public void updateUserShouldReturnStatus404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", nonExistingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    public void updateUserShouldReturnStatus400WhenUserRequestHasOperatorOrAdminRoleButWorkingHotelIdIsNull() throws Exception {
        adminUpdateUserRequest.getRolesIds().add(2L);
        adminUpdateUserRequest.getRolesIds().add(3L);
        adminUpdateUserRequest.setWorkingHotelId(null);
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    public void updateUserShouldReturnStatus404WhenUserRequestHasOperatorOrAdminRoleAndWorkingHotelIdIsPresentButHotelDoesNotExist() throws Exception {
        adminUpdateUserRequest.getRolesIds().add(2L);
        adminUpdateUserRequest.getRolesIds().add(3L);
        adminUpdateUserRequest.setWorkingHotelId(nonExistingId);
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    public void updateUserShouldReturnStatus404WhenRoleDoesNotExist() throws Exception {
        adminUpdateUserRequest.getRolesIds().add(nonExistingId);
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    public void updateUserShouldReturnStatus409WhenEmailIsAlreadyInUse() throws Exception {
        adminUpdateUserRequest.setEmail(guestEmail);
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(11)
    public void updateUserShouldReturnStatus409WhenCpfIsAlreadyInUse() throws Exception {
        adminUpdateUserRequest.setCpf("329.949.250-01");
        mockMvc.perform(put("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateUserRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    public void findUserBookingsShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/bookings", existingId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findUserBookingsShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/bookings", existingId)
            .header("Authorization", guestBearerToken))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findUserBookingsShouldReturnStatus200AndPageOfBookingResponseDTOWhenAdminOrOperatorIsLogged() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/bookings", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].checkIn").value("2025-05-02"))
            .andExpect(jsonPath("$.content[0].checkOut").value("2025-05-07"))
            .andExpect(jsonPath("$.content[0].finished").value(true))
            .andExpect(jsonPath("$.content[0].guestsQuantity").value(1))
            .andExpect(jsonPath("$.content[0].totalPrice").value(750.00))
            .andExpect(jsonPath("$.content[0].userId").value(2))
            .andExpect(jsonPath("$.content[0].roomId").value(1));
    }

    @Test
    public void deleteUserBookingsShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", existingId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUserBookingsShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", existingId)
            .header("Authorization", guestBearerToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(15)
    public void deleteUserBookingsShouldReturnStatus204WhenUserExistsAndAdminOrOperatorIsLogged() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", createdUserId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserBookingsShouldReturnStatus400WhenUserToBeDeletedHasBookingsAssociatedWith() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

}
