package com.fernandocanabarro.booking_app_backend.it;

import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.utils.AccessTokenUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@Sql(scripts = {"classpath:create_tables.sql", "classpath:insert_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("it")
public class RoomControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail, adminPassword;
    private String guestEmail, guestPassword;
    private String adminBearerToken, guestBearerToken;
    private Long existingId, nonExistingId;
    private RoomRequestDTO roomRequest;
    private MockMultipartFile roomRequestPart;
    private MockMultipartFile imagePart;
    private RoomRatingRequestDTO roomRatingRequest;

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
        this.roomRequest = new RoomRequestDTO("102", 2, 2, BigDecimal.valueOf(100.0), "Description", 2, 1L);
        this.roomRequestPart = new MockMultipartFile(
            "request",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(this.roomRequest));
        this.imagePart = new MockMultipartFile(
            "images",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "image".getBytes());
        this.roomRatingRequest = new RoomRatingRequestDTO(BigDecimal.valueOf(4.5), "description");
    }

    @Test
    @Order(1)
    public void findAllWithQueryShouldReturnStatus200AndPageOfRoomResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/query?checkIn=2025-08-01&checkOut=2025-08-07")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].number").value("101"))
            .andExpect(jsonPath("$.content[0].floor").value(1))
            .andExpect(jsonPath("$.content[0].type").value(1))
            .andExpect(jsonPath("$.content[0].pricePerNight").value(150.00))
            .andExpect(jsonPath("$.content[0].description").value("Quarto standard com cama de casal e ar-condicionado."))
            .andExpect(jsonPath("$.content[0].hotelId").value(1))
            .andExpect(jsonPath("$.content[0].hotelName").value("Hotel Mar Azul"));
    }

    @Test
    @Order(2)
    public void findAllShouldReturnStatus200AndPageOfRoomResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/rooms")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].number").value("101"))
            .andExpect(jsonPath("$.content[0].floor").value(1))
            .andExpect(jsonPath("$.content[0].type").value(1))
            .andExpect(jsonPath("$.content[0].pricePerNight").value(150.00))
            .andExpect(jsonPath("$.content[0].description").value("Quarto standard com cama de casal e ar-condicionado."))
            .andExpect(jsonPath("$.content[0].hotelId").value(1))
            .andExpect(jsonPath("$.content[0].hotelName").value("Hotel Mar Azul"));
    }

    @Test
    @Order(3)
    public void findByIdShouldReturnStatus200WhenRoomExists() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/{id}", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.number").value("101"))
            .andExpect(jsonPath("$.floor").value(1))
            .andExpect(jsonPath("$.type").value(1))
            .andExpect(jsonPath("$.pricePerNight").value(150.00))
            .andExpect(jsonPath("$.description").value("Quarto standard com cama de casal e ar-condicionado."))
            .andExpect(jsonPath("$.hotel.id").value(1))
            .andExpect(jsonPath("$.hotel.name").value("Hotel Mar Azul"))
            .andExpect(jsonPath("$.hotel.description").value("Hotel com vista para o mar, ideal para férias e descanso."));
    }

    @Test
    public void findBookingsByRoomIdShouldReturnStatus200AndPageOfBookingResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/{id}/bookings", existingId)
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
    public void findByIdShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/{id}", nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms")
            .file(roomRequestPart)
            .file(imagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms")
            .file(roomRequestPart)
            .file(imagePart)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createShouldReturnStatus201WhenDataIsValid() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms")
            .file(roomRequestPart)
            .file(imagePart)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void updateShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms/{id}", existingId)
            .file(roomRequestPart)
            .file(imagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .with(request -> {
                request.setMethod("PUT");
                return request;
            }))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms/{id}", existingId)
            .file(roomRequestPart)
            .file(imagePart)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .with(request -> {
                request.setMethod("PUT");
                return request;
            }))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    public void updateShouldReturnStatus200WhenDataIsValid() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms/{id}", existingId)
            .file(roomRequestPart)
            .file(imagePart)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .with(request -> {
                request.setMethod("PUT");
                return request;
            }))
            .andExpect(status().isOk());
    }

    @Test
    public void updateShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        mockMvc.perform(multipart("/api/v1/rooms/{id}", nonExistingId)
            .file(roomRequestPart)
            .file(imagePart)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .with(request -> {
                request.setMethod("PUT");
                return request;
            }))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{id}", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{id}", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnStatus204WhenRoomExistsAndHasNoOtherEntityAssocietedWith() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{id}", 3L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{id}", nonExistingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteImageShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/images?imagesIds={id}", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteImageShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/images?imagesIds={id}", 5L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    public void deleteImageShouldReturnStatus204WhenImageExists() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/images?imagesIds={id}", 5L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void findAllRatingsByRoomIdShouldReturnStatus200AndPageOfRatingResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/{id}/ratings", existingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].rating").value(4.5))
            .andExpect(jsonPath("$.content[0].description").value("Quarto muito bom e confortável."))
            .andExpect(jsonPath("$.content[0].roomId").value(1))
            .andExpect(jsonPath("$.content[0].userFullName").value("Fernando"))
            .andExpect(jsonPath("$.content[0].userEmail").value("fernando@gmail.com"));
    }

    @Test
    public void addRatingShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/rooms/{id}/ratings", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    public void addAndUpdateAndDeleteAndFindMyRatingsShouldAllReturnSuccessStatusWhenDataIsValid() throws Exception {
        mockMvc.perform(post("/api/v1/rooms/{id}/ratings", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        Long createdRoomRatingId = 3L;
        mockMvc.perform(put("/api/v1/rooms/ratings/{id}", createdRoomRatingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/rooms/ratings/{id}", createdRoomRatingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    public void findMyRoomRatingsShouldReturnStatus200AndPageOfRoomRatingResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/my-ratings")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(2L))
            .andExpect(jsonPath("$.content[0].rating").value(4.5))
            .andExpect(jsonPath("$.content[0].description").value("Quarto muito bom e confortável."))
            .andExpect(jsonPath("$.content[0].roomId").value(2L));
    }

    @Test
    @Order(7)
    public void findRoomRatingByIdShouldReturnStatus200AndRoomRatingResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/ratings/{id}", 2L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2L))
            .andExpect(jsonPath("$.rating").value(4.5))
            .andExpect(jsonPath("$.description").value("Quarto muito bom e confortável."))
            .andExpect(jsonPath("$.roomId").value(2L));
    }

    @Test
    public void findRoomRatingByIdShouldReturnStatus404WhenRatingDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/ratings/{id}", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void addRatingShouldReturnStatus404WhenRoomDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/v1/rooms/{id}/ratings", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateRatingShouldReturnStatus403WhenRatingDoesNotBelongToCurrentUser() throws Exception {
        Long otherUserRoomRatingId = 1L;
        mockMvc.perform(put("/api/v1/rooms/ratings/{id}", otherUserRoomRatingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateRatingShouldReturnStatus404WhenRatingDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/rooms/ratings/{id}", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteRatingShouldReturnStatus403WhenRatingDoesNotBelongToCurrentUser() throws Exception {
        Long otherUserRoomRatingId = 1L;
        mockMvc.perform(delete("/api/v1/rooms/ratings/{id}", otherUserRoomRatingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteRatingShouldReturnStatus404WhenRatingDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/ratings/{id}", nonExistingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomRatingRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }


}
