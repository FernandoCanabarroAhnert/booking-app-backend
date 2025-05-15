package com.fernandocanabarro.booking_app_backend.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
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
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardRequestDTO;
import com.fernandocanabarro.booking_app_backend.utils.AccessTokenUtils;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@Sql(scripts = {"classpath:create_tables.sql", "classpath:insert_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("it")
public class CreditCardControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail, adminPassword;
    private String guestEmail, guestPassword;
    private String adminBearerToken, guestBearerToken;
    private Long existingId, nonExistingId;
    private CreditCardRequestDTO creditCardRequest;

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
        this.creditCardRequest = new CreditCardRequestDTO("name", "1234567812345678", "321", "2025-12", 2);
    }
    
    @Test
    public void getConnectedUserCreditCardsShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/credit-cards/my-credit-cards"))
            .andExpect(status().isUnauthorized());
    }

     @Test
    public void getConnectedUserCreditCardsShouldReturnStatus200AndPageOfCreditCardResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/credit-cards/my-credit-cards")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].holderName").value("Fernando"))
            .andExpect(jsonPath("$.content[0].lastFourDigits").value("**** **** **** 5678"))
            .andExpect(jsonPath("$.content[0].brand").value(1))
            .andExpect(jsonPath("$.content[0].expirationDate").value("8/2026"));
    }

    @Test
    public void addCreditCreditCardsShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/credit-cards")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(creditCardRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void addCreditCardAndDeleteShouldAllReturnSuccessStatusWhenDataIsValid() throws Exception {
        mockMvc.perform(post("/api/v1/credit-cards")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(creditCardRequest)))
            .andExpect(status().isCreated());
        Long createdCreditCardId = 2L;
        mockMvc.perform(delete("/api/v1/credit-cards/{id}", createdCreditCardId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCreditCreditCardsShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/credit-cards/{id}", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteCreditCreditCardsShouldReturnStatus403WhenConnectedUserIsNotTheOwnerNeitherAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/credit-cards/{id}", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCreditCreditCardsShouldReturnStatus404WhenCreditCardDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/credit-cards/{id}", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}
