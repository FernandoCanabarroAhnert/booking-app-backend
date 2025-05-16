package com.fernandocanabarro.booking_app_backend.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
import com.fernandocanabarro.booking_app_backend.factories.PaymentFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminUpdateBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentRequestDTO;
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
    private BookingRequestDTO selfBookingRequest;
    private AdminUpdateBookingRequestDTO adminUpdateBookingRequest;
    private BaseBookingRequestDTO selfUpdateBookingRequest;
    private BookingPaymentRequestDTO bookingPaymentRequest;
    
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

        this.selfBookingRequest = new BookingRequestDTO();
        selfBookingRequest.setRoomId(1L);
        selfBookingRequest.setCheckIn(LocalDate.of(2025, 7, 8));
        selfBookingRequest.setCheckOut(LocalDate.of(2025, 7, 15));
        selfBookingRequest.setGuestsQuantity(1);
        selfBookingRequest.setPayment(PaymentFactory.createDinheiroPaymentRequest());

        this.adminUpdateBookingRequest = new AdminUpdateBookingRequestDTO();
        adminUpdateBookingRequest.setRoomId(1L);
        adminUpdateBookingRequest.setCheckIn(LocalDate.of(2025, 7, 16));
        adminUpdateBookingRequest.setCheckOut(LocalDate.of(2025, 7, 20));
        adminUpdateBookingRequest.setGuestsQuantity(1);
        adminUpdateBookingRequest.setUserId(1L);
        adminUpdateBookingRequest.setIsFinished(true);

        this.selfUpdateBookingRequest = new BaseBookingRequestDTO();
        selfUpdateBookingRequest.setRoomId(1L);
        selfUpdateBookingRequest.setCheckIn(LocalDate.of(2025, 7, 21));
        selfUpdateBookingRequest.setCheckOut(LocalDate.of(2025, 7, 25));
        selfUpdateBookingRequest.setGuestsQuantity(1);

        this.bookingPaymentRequest = new BookingPaymentRequestDTO();
        bookingPaymentRequest.setPaymentType(4);
        bookingPaymentRequest.setIsOnlinePayment(true);
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
    @Order(1)
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
    public void findMyBookingsShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/my-bookings"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    public void findMyBookingsShouldReturnStatus200WhenUserIsConnected() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/my-bookings")
            .header("Authorization", guestBearerToken))
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
    @Order(3)
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
    @Order(6)
    public void adminCreateBookingShouldReturnStatus201WhenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isCreated());
    }
 
    @Test
    @Order(7)
    public void adminCreateBookingShouldReturnStatus409WhenCheckInOrCheckOutDatesAreInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isConflict());
        adminBookingRequest.setCheckIn(LocalDate.of(2025, 7, 7));
        adminBookingRequest.setCheckOut(LocalDate.of(2025, 7, 1));
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isConflict());
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

    @Test
    public void adminCreateBookingShouldReturnStatus400WhenGuestsQuantityIsGreaterThanRoomCapacity() throws Exception {
        adminBookingRequest.setGuestsQuantity(1000);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminCreateBookingShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        adminBookingRequest.setRoomId(nonExistingId);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void adminCreateBookingShouldReturnStatus404WhenUserDoesNotExist() throws Exception {
        adminBookingRequest.setUserId(nonExistingId);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void adminCreateBookingShouldReturnStatus400WhenPaymentTypeIsCreditCardAndOnlinePaymenteIsSetToTrue() throws Exception {
        adminBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        adminBookingRequest.getPayment().setCreditCardId(nonExistingId);
        mockMvc.perform(post("/api/v1/bookings")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestCreateBookingShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/self")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    public void guestCreateBookingShouldReturnStatus201WhenUserIsLogged() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isCreated());
    }
 
    @Test
    @Order(10)
    public void guestCreateBookingShouldReturnStatus409WhenCheckInOrCheckOutDatesAreInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isConflict());
        selfBookingRequest.setCheckIn(LocalDate.of(2025, 7, 7));
        selfBookingRequest.setCheckOut(LocalDate.of(2025, 7, 1));
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    public void guestCreateBookingShouldReturnStatus400WhenPaymentTypeIsDinheiroButOnlinePaymentIsSetToTrue() throws Exception {
        selfBookingRequest.getPayment().setIsOnlinePayment(true);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestCreateBookingShouldReturnStatus400WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrueButCreditCardIdIsNull() throws Exception {
        selfBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        selfBookingRequest.getPayment().setCreditCardId(null);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestCreateBookingShouldReturnStatus400WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrueButInstallmentQuantityIsNull() throws Exception {
        selfBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        selfBookingRequest.getPayment().setInstallmentQuantity(null);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestCreateBookingShouldReturnStatus400WhenGuestsQuantityIsGreaterThanRoomCapacity() throws Exception {
        selfBookingRequest.setGuestsQuantity(1000);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestCreateBookingShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        selfBookingRequest.setRoomId(nonExistingId);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    public void guestCreateBookingShouldReturnStatus404WhenPaymentTypeIsCreditCardAndOnlinePaymenteIsSetToTrueButCreditCardDoesNotExist() throws Exception {
        selfBookingRequest.setCheckIn(LocalDate.of(2025, 7, 28));
        selfBookingRequest.setCheckOut(LocalDate.of(2025, 7, 29));
        selfBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        selfBookingRequest.getPayment().setCreditCardId(nonExistingId);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    public void guestCreateBookingShouldReturnStatus403WhenPaymentTypeIsCreditCardAndOnlinePaymenteIsSetToTrueButCreditCardDoesNotBelongToConnectedUser() throws Exception {
        selfBookingRequest.setCheckIn(LocalDate.of(2025, 7, 28));
        selfBookingRequest.setCheckOut(LocalDate.of(2025, 7, 29));
        selfBookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        selfBookingRequest.getPayment().setCreditCardId(1L);
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminUpdateBookingShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminUpdateBookingShouldReturnStatus403WhenUserIsGuest() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    public void adminUpdateBookingShouldReturnStatus200WhenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isOk());
    }
 
    @Test
    @Order(14)
    public void adminUpdateBookingShouldReturnStatus409WhenCheckInOrCheckOutDatesAreInvalid() throws Exception {
        selfBookingRequest.setCheckIn(LocalDate.of(2025, 8, 1));
        selfBookingRequest.setCheckOut(LocalDate.of(2025, 8, 7));
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isCreated());
        adminUpdateBookingRequest.setCheckIn(LocalDate.of(2025, 8, 1));
        adminUpdateBookingRequest.setCheckOut(LocalDate.of(2025, 8, 7));
        mockMvc.perform(put("/api/v1/bookings/{id}", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isConflict());
        adminUpdateBookingRequest.setCheckIn(LocalDate.of(2025, 7, 7));
        adminUpdateBookingRequest.setCheckOut(LocalDate.of(2025, 7, 1));
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(15)
    public void adminUpdateBookingShouldReturnStatus400WhenGuestsQuantityIsGreaterThanRoomCapacity() throws Exception {
        adminUpdateBookingRequest.setGuestsQuantity(1000);
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminUpdateBookingShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        adminUpdateBookingRequest.setRoomId(nonExistingId);
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    public void adminUpdateBookingShouldReturnStatus404WhenUserDoesNotExist() throws Exception {
        adminUpdateBookingRequest.setUserId(nonExistingId);
        mockMvc.perform(put("/api/v1/bookings/{id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void adminUpdateBookingShouldReturnStatus404WhenBookingDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}", nonExistingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(adminUpdateBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void guestUpdateBookingShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/self", 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    public void guestUpdateBookingShouldReturnStatus409WhenCheckInOrCheckOutDatesAreInvalid() throws Exception {
        selfBookingRequest.setCheckIn(LocalDate.of(2025, 8, 8));
        selfBookingRequest.setCheckOut(LocalDate.of(2025, 8, 15));
        mockMvc.perform(post("/api/v1/bookings/self")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfBookingRequest)))
            .andExpect(status().isCreated());
        long guestUserLastCreatedBookingId = 5L;
        selfUpdateBookingRequest.setCheckIn(LocalDate.of(2025, 8, 1));
        selfUpdateBookingRequest.setCheckOut(LocalDate.of(2025, 8, 7));
        mockMvc.perform(put("/api/v1/bookings/{id}/self", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isConflict());
        selfUpdateBookingRequest.setCheckIn(LocalDate.of(2025, 7, 7));
        selfUpdateBookingRequest.setCheckOut(LocalDate.of(2025, 7, 1));
        mockMvc.perform(put("/api/v1/bookings/{id}/self", guestUserLastCreatedBookingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @Order(17)
    public void guestUpdateBookingShouldReturnStatus200WhenUserIsLogged() throws Exception {
        long guestUserLastCreatedBookingId = 5L;
        mockMvc.perform(put("/api/v1/bookings/{id}/self", guestUserLastCreatedBookingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @Order(18)
    public void guestUpdateBookingShouldReturnStatus400WhenGuestsQuantityIsGreaterThanRoomCapacity() throws Exception {
        long guestUserLastCreatedBookingId = 5L;
        selfUpdateBookingRequest.setGuestsQuantity(1000);
        mockMvc.perform(put("/api/v1/bookings/{id}/self", guestUserLastCreatedBookingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestUpdateBookingShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        selfUpdateBookingRequest.setRoomId(nonExistingId);
        mockMvc.perform(put("/api/v1/bookings/{id}/self", 2L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void guestUpdateBookingShouldReturnStatus404WhenBookingDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/self", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selfUpdateBookingRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void adminUpdateBookingPaymentShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminUpdateBookingPaymentShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(19)
    public void adminUpdateBookingPaymentShouldReturnStatus200WhenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isOk());
    }

    @Test
    public void adminUpdateBookingPaymentShouldReturnStatus400WhenPaymentTypeIsDinheiroButOnlinePaymentIsSetToTrue() throws Exception {
        bookingPaymentRequest.setPaymentType(1);
        bookingPaymentRequest.setIsOnlinePayment(true);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminUpdateBookingPaymentShouldReturnStatus400WhenPaymentTypeIsCartaoButInstallmentQuantityIsNull() throws Exception {
        bookingPaymentRequest.setPaymentType(2);
        bookingPaymentRequest.setIsOnlinePayment(false);
        bookingPaymentRequest.setInstallmentQuantity(null);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminUpdateBookingPaymentShouldReturnStatus400WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrue() throws Exception {
        bookingPaymentRequest.setPaymentType(2);
        bookingPaymentRequest.setIsOnlinePayment(true);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminUpdateBookingPaymentShouldReturnStatus404WhenBookingDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment", nonExistingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void guestUpdateBookingPaymentShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(23)
    public void guestUpdateBookingPaymentShouldReturnStatus403WhenBookingDoesNotBelongToConnectedUser() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", 2L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(20)
    public void guestUpdateBookingPaymentShouldReturnStatus200WhenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @Order(21)
    public void guestUpdateBookingPaymentShouldReturnStatus400WhenPaymentTypeIsDinheiroButOnlinePaymentIsSetToTrue() throws Exception {
        bookingPaymentRequest.setPaymentType(1);
        bookingPaymentRequest.setIsOnlinePayment(true);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(22)
    public void guestUpdateBookingPaymentShouldReturnStatus400WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrueButCreditCardIdIsNull() throws Exception {
        bookingPaymentRequest.setPaymentType(2);
        bookingPaymentRequest.setIsOnlinePayment(true);
        bookingPaymentRequest.setCreditCardId(null);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestUpdateBookingPaymentShouldReturnStatus400WhenPaymentTypeIsCartaoButInstallmentQuantityIsNull() throws Exception {
        bookingPaymentRequest.setPaymentType(2);
        bookingPaymentRequest.setIsOnlinePayment(false);
        bookingPaymentRequest.setInstallmentQuantity(null);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void guestUpdateBookingPaymentShouldReturnStatus404WhenPaymentTypeIsCartaoAndOnlinePaymentIsSetToTrueButCreditCardDoesNotExist() throws Exception {
        bookingPaymentRequest.setPaymentType(2);
        bookingPaymentRequest.setIsOnlinePayment(true);
        bookingPaymentRequest.setInstallmentQuantity(2);
        bookingPaymentRequest.setCreditCardId(nonExistingId);
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", 2L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void guestUpdateBookingPaymentShouldReturnStatus404WhenBookingDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/bookings/{id}/payment/self", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingPaymentRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteBookingShouldReturnStatus401AuthTokenIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/{id}", existingId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteBookingShouldReturnStatus403WhenUserIsGuest() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/{id}", existingId)
            .header("Authorization", guestBearerToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(24)
    public void deleteBookingShouldReturnStatus204WhenUserIsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/{id}", 2L)
            .header("Authorization", adminBearerToken))
            .andExpect(status().isNoContent());
    }



}
