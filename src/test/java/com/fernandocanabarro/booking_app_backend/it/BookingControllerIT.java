package com.fernandocanabarro.booking_app_backend.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
import com.fernandocanabarro.booking_app_backend.factories.PaymentFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.utils.AccessTokenUtils;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@Sql(scripts = {"classpath:create_tables.sql", "classpath:insert_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("it")
public class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail, adminPassword;
    private String guestEmail, guestPassword;
    private String adminBearerToken, guestBearerToken;
    private Long existingId, nonExistingId;
    private AdminBookingRequestDTO adminBookingRequest;
    private BookingRequestDTO bookingRequest;
    
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

        this.adminBookingRequest = new AdminBookingRequestDTO();
        adminBookingRequest.setRoomId(1L);
        adminBookingRequest.setCheckIn(LocalDate.of(2025, 7, 1));
        adminBookingRequest.setCheckOut(LocalDate.of(2025, 7, 7));
        adminBookingRequest.setGuestsQuantity(1);
        adminBookingRequest.setPayment(PaymentFactory.createDinheiroPaymentRequest());
        adminBookingRequest.setUserId(2L);

        this.bookingRequest = new BookingRequestDTO();
        bookingRequest.setRoomId(1L);
        bookingRequest.setCheckIn(LocalDate.of(2025, 7, 1));
        bookingRequest.setCheckOut(LocalDate.of(2025, 7, 7));
        bookingRequest.setGuestsQuantity(1);
        bookingRequest.setPayment(PaymentFactory.createDinheiroPaymentRequest());
    }

    @Test
    public void findAllShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllShouldReturnStatus403WhenUserIsGuest() throws Exception {
        mockMvc.perform(get("/api/v1/bookings")
            .header("Authorization", guestBearerToken))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findAllShouldReturnStatus200WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/bookings")
            .header("Authorization", adminBearerToken))
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
    public void findByIdShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", existingId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdShouldReturnStatus403WhenBookingDoesNotBelongToCurrentUser() throws Exception {
        String otherUserToken = "Bearer " + AccessTokenUtils.obtainAccessToken("pereira@gmail.com", "12345Az@", mockMvc, objectMapper);
        mockMvc.perform(get("/api/v1/bookings/{id}", existingId)
            .header("Authorization", otherUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnStatus200WhenUserIsAdminOrTheOwnerOfTheResource() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", existingId)
            .header("Authorization", adminBearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.checkIn").value("2025-05-02"))
            .andExpect(jsonPath("$.checkOut").value("2025-05-07"))
            .andExpect(jsonPath("$.finished").value(true))
            .andExpect(jsonPath("$.guestsQuantity").value(1))
            .andExpect(jsonPath("$.totalPrice").value(750.00))
            .andExpect(jsonPath("$.user.id").value(2))
            .andExpect(jsonPath("$.user.fullName").value("Anita"))
            .andExpect(jsonPath("$.user.email").value("anita@gmail.com"))
            .andExpect(jsonPath("$.room.id").value(1))
            .andExpect(jsonPath("$.room.pricePerNight").value(150.0))
            .andExpect(jsonPath("$.payment.paymentType").value(1));
        mockMvc.perform(get("/api/v1/bookings/{id}", existingId)
            .header("Authorization", guestBearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.checkIn").value("2025-05-02"))
            .andExpect(jsonPath("$.checkOut").value("2025-05-07"))
            .andExpect(jsonPath("$.finished").value(true))
            .andExpect(jsonPath("$.guestsQuantity").value(1))
            .andExpect(jsonPath("$.totalPrice").value(750.00))
            .andExpect(jsonPath("$.user.id").value(2))
            .andExpect(jsonPath("$.user.fullName").value("Anita"))
            .andExpect(jsonPath("$.user.email").value("anita@gmail.com"))
            .andExpect(jsonPath("$.room.id").value(1))
            .andExpect(jsonPath("$.room.pricePerNight").value(150.0))
            .andExpect(jsonPath("$.payment.paymentType").value(1));
    }

    @Test
    public void findByIdShouldReturnStatus404WhenBookingDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", nonExistingId)
            .header("Authorization", adminBearerToken))
            .andExpect(status().isNotFound());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus403WhenUserIsGuest() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus201WhenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus400WhenPaymentTypeIsDinheiroButOnlinePaymentIsSetToTrue() throws Exception {
        adminBookingRequest.getPayment().setIsOnlinePayment(true);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus400WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrueButCreditCardIdIsNull() throws Exception {
        adminBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        adminBookingRequest.getPayment().setCreditCardId(null);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus400WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrueButInstallmentQuantityIsNull() throws Exception {
        adminBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        adminBookingRequest.getPayment().setInstallmentQuantity(null);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isBadRequest());
    }

}
