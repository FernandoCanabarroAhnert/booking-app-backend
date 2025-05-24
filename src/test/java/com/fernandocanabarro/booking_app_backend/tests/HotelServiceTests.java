package com.fernandocanabarro.booking_app_backend.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.fernandocanabarro.booking_app_backend.factories.HotelFactory;
import com.fernandocanabarro.booking_app_backend.factories.RoomFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelSearchResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.projections.HotelSearchProjection;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.ImageRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.impl.HotelServiceImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("it")
public class HotelServiceTests {

    @InjectMocks
    private HotelServiceImpl hotelService;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ImageRepository imageRepository;

    private Hotel hotel;
    private Room room;
    private MockMultipartFile mockImage;
    private HotelRequestDTO request;
    private HotelSearchProjection projection;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    public void setup() {
        this.hotel = HotelFactory.createHotel();
        this.room = RoomFactory.createRoom();
        this.mockImage = new MockMultipartFile(
            "image",                   
            "imagem.jpg",           
            "image/jpeg",        
            "image".getBytes()
        );
        this.request = new HotelRequestDTO("name", "description", 10, "street", "number", "city", "zipCode", "state", "(11) 99999-9999");
        this.projection = new HotelSearchProjection() {
            @Override
            public Long getId() {
                return hotel.getId();
            }

            @Override
            public String getName() {
                return hotel.getName();
            }
        };
        this.existingId = 1L;
        this.nonExistingId = 1000L;
    }

    @Test
    public void findAllByNameShouldReturnListOfSearchHotelResponseDTO() {
        when(this.hotelRepository.findAllByNameContainingIgnoreCase("name")).thenReturn(List.of(projection));
        List<HotelSearchResponseDTO> response = this.hotelService.findAllByName("name");
        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getName()).isEqualTo("name");
    }

    @Test
    public void findAllShouldReturnListOfHotels() {
        when(this.hotelRepository.findAll()).thenReturn(List.of(this.hotel));
        List<HotelResponseDTO> response = this.hotelService.findAll();
        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getName()).isEqualTo("name");
        assertThat(response.get(0).getRoomQuantity()).isEqualTo(10);
        assertThat(response.get(0).getStreet()).isEqualTo("street");
    }

    @Test
    public void findAllPageableShouldReturnPageOfHotels() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Hotel> page = new PageImpl<>(List.of(this.hotel));
        String name = "name";

        when(this.hotelRepository.findAllByNameContainingIgnoreCase(name, pageable)).thenReturn(page);

        Page<HotelResponseDTO> response = this.hotelService.findAllPageable(pageable, name);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getRoomQuantity()).isEqualTo(10);
        assertThat(response.getContent().get(0).getStreet()).isEqualTo("street");
    }

    @Test
    public void findRoomsByHotelIdShouldReturnPageOfRooms() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> page = new PageImpl<>(List.of(this.room));

        when(this.roomRepository.findByHotelId(this.existingId, pageable)).thenReturn(page);

        Page<RoomResponseDTO> response = this.hotelService.findRoomsByHotelId(this.existingId, pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getNumber()).isEqualTo("101");
        assertThat(response.getContent().get(0).getPricePerNight()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(response.getContent().get(0).getType()).isEqualTo(1);
    }

    @Test
    public void findByIdShouldReturnHotelDetailResponseDTO() {
        when(this.hotelRepository.findById(this.existingId)).thenReturn(Optional.of(this.hotel));
        HotelDetailResponseDTO response = this.hotelService.findById(this.existingId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getRoomQuantity()).isEqualTo(10);
        assertThat(response.getStreet()).isEqualTo("street");
        assertThat(response.getNumber()).isEqualTo("number");
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundException() {
        when(this.hotelRepository.findById(this.nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.hotelService.findById(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createShouldThrowNoException() {
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        assertThatCode(() -> this.hotelService.create(request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void updateShouldThrowNoException() {
        when(this.hotelRepository.findById(this.existingId)).thenReturn(Optional.of(this.hotel));
        when(this.hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        assertThatCode(() -> this.hotelService.update(this.existingId, request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void updateShouldThrowNoExceptionWhenImagesAreNull() {
        when(this.hotelRepository.findById(this.existingId)).thenReturn(Optional.of(this.hotel));
        when(this.hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        assertThatCode(() -> this.hotelService.update(this.existingId, request, null)).doesNotThrowAnyException();
    }

    @Test
    public void updateShouldThrowResourceNotFoundException() {
        when(this.hotelRepository.findById(this.nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.hotelService.update(this.nonExistingId, request, List.of(mockImage))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteShouldThrowNoException() {
        when(this.hotelRepository.existsById(this.existingId)).thenReturn(true);

        assertThatCode(() -> this.hotelService.delete(this.existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteShouldThrowResourceNotFoundException() {
        when(this.hotelRepository.existsById(this.nonExistingId)).thenReturn(false);

        assertThatThrownBy(() -> this.hotelService.delete(this.nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteShouldThrowBadRequestExceptionWhenHotelHasRoomsAssociatedWithIt() {
        when(this.hotelRepository.existsById(this.existingId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(this.hotelRepository).deleteById(existingId);

        assertThatThrownBy(() -> hotelService.delete(existingId)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void deleteImageShouldThrowNoExceptionWhenImageIdExists() {
        when(imageRepository.existsById(existingId)).thenReturn(true);

        assertThatCode(() -> hotelService.deleteImage(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteImageShouldThrowResourceNotFoundExceptionWhenImageIdDoesNotExist() {
        when(imageRepository.existsById(nonExistingId)).thenReturn(false);

        assertThatThrownBy(() -> hotelService.deleteImage(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }
}
