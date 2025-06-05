package com.fernandocanabarro.booking_app_backend.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.utils.AccessTokenUtils;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@Sql(scripts = {"classpath:create_tables.sql", "classpath:insert_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("it")
public class HotelControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail, adminPassword;
    private String guestEmail, guestPassword;
    private String adminBearerToken, guestBearerToken;
    private Long existingId, nonExistingId;
    private HotelRequestDTO request;
    private MockMultipartFile requestJsonPart;
    private MockMultipartFile imagePart;

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
        this.request = new HotelRequestDTO("name", "description", 10, "street", "number", "city", "zipCode", "state", "(11) 99999-9999");
        this.requestJsonPart = new MockMultipartFile(
            "request", 
            "", 
            MediaType.APPLICATION_JSON_VALUE, 
            objectMapper.writeValueAsBytes(request));
        this.imagePart = new MockMultipartFile(
            "images", 
            "image.jpg", 
            MediaType.IMAGE_JPEG_VALUE, 
            "image".getBytes());
    }

    @Test
    @Order(1)
    public void findAllHotelsShouldReturnStatus200AndPageOfHotelResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/hotels")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Hotel Mar Azul"))
            .andExpect(jsonPath("$.content[0].description").value("Hotel com vista para o mar, ideal para férias e descanso."))
            .andExpect(jsonPath("$.content[0].roomQuantity").value(45))
            .andExpect(jsonPath("$.content[0].street").value("Av. Beira Mar"))
            .andExpect(jsonPath("$.content[0].number").value("123"))
            .andExpect(jsonPath("$.content[0].zipCode").value("60165-121"))
            .andExpect(jsonPath("$.numberOfElements").value(3));
    }

    @Test
    public void findAllHotelsByNameShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/search?name={name}", "Hotel Mar Azul")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllHotelsByNameShouldReturnStatus403WhenUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/search?name={name}", "Hotel Mar Azul")
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    public void findAllHotelsByNameShouldReturnStatus200AndListOfHotelSearchResponseDTOWhenAdminIsLogged() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/search?name={name}", "Hotel Mar Azul")
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Hotel Mar Azul"));
    }

    @Test
    @Order(3)
    public void findRoomsByHotelIdShouldReturnStatus200AndPageOfRoomResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/{id}/rooms", existingId)
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
    @Order(4)
    public void findHotelByIdShouldReturnStatus200AndHotelDetailResponseDTO() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/{id}", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Hotel Mar Azul"))
            .andExpect(jsonPath("$.description").value("Hotel com vista para o mar, ideal para férias e descanso."))
            .andExpect(jsonPath("$.roomQuantity").value(45))
            .andExpect(jsonPath("$.street").value("Av. Beira Mar"))
            .andExpect(jsonPath("$.number").value("123"))
            .andExpect(jsonPath("$.zipCode").value("60165-121"));
    }

    @Test
    public void findHotelByIdShouldReturnStatus404AndWhenHotelDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/{id}", nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createHotelShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels")
            .file(this.requestJsonPart)
            .file(this.imagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createHotelShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels")
            .file(this.requestJsonPart)
            .file(this.imagePart)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createHotelShouldReturnStatus201WhenDataIsValid() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels")
            .file(this.requestJsonPart)
            .file(this.imagePart)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void updateHotelShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels/{id}", existingId)
            .file(this.requestJsonPart)
            .file(this.imagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .with(request -> {
                request.setMethod("PUT");
                return request;
            }))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateHotelShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels/{id}", existingId)
            .file(this.requestJsonPart)
            .file(this.imagePart)
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
    public void updateHotelShouldReturnStatus404WhenUserIsAdminButHotelDoesNotExist() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels/{id}", nonExistingId)
            .file(this.requestJsonPart)
            .file(this.imagePart)
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
    public void updateHotelShouldReturnStatus200WhenDataIsValid() throws Exception {
        mockMvc.perform(multipart("/api/v1/hotels/{id}", existingId)
            .file(this.requestJsonPart)
            .file(this.imagePart)
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
    public void deleteHotelShouldReturnStatus401AndWhenAuthTokenIsNotProvided() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/{id}", existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteHotelShouldReturnStatus403AndWhenUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/{id}", existingId)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    public void deleteHotelShouldReturnStatus204WhenHotelExists() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/{id}", 3)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    public void deleteHotelShouldReturnStatus404WhenHotelDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/{id}", nonExistingId)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    public void deleteImageShouldReturnStatus401WhenAuthTokenIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/images?imagesIds={id}", 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteImageShouldReturnStatus403WhenConnectedUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/images?imagesIds={id}", 2L)
            .header("Authorization", guestBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    public void deleteImageShouldReturnStatus204WhenImageExists() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/images?imagesIds={id}", 2L)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

}
