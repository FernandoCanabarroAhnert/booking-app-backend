package com.fernandocanabarro.booking_app_backend.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.fernandocanabarro.booking_app_backend.factories.BookingFactory;
import com.fernandocanabarro.booking_app_backend.factories.HotelFactory;
import com.fernandocanabarro.booking_app_backend.factories.RoleFactory;
import com.fernandocanabarro.booking_app_backend.factories.RoomFactory;
import com.fernandocanabarro.booking_app_backend.factories.UserFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.entities.RoomRating;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.ImageRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRatingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.impl.RoomServiceImpl;
import com.fernandocanabarro.booking_app_backend.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("it")
public class RoomServiceTests {

    @InjectMocks
    private RoomServiceImpl roomService;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private RoomRatingRepository roomRatingRepository;
    @Mock
    private AuthService authService;

    private Hotel hotel;
    private Room room;
    private MockMultipartFile mockImage;
    private RoomRequestDTO request;
    private Long existingId;
    private Long nonExistingId;
    private RoomRating roomRating;
    private RoomRatingRequestDTO roomRatingRequest;
    private User user;

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
        this.request = new RoomRequestDTO("102", 2, 2, BigDecimal.valueOf(100.0), "Description", 2, 1L);
        this.existingId = 1L;
        this.nonExistingId = 1000L;
        this.roomRating = RoomFactory.createRoomRating();
        this.roomRatingRequest = new RoomRatingRequestDTO(BigDecimal.valueOf(4.5), "description");
        this.user = UserFactory.createUser();
    }

    @Test
    public void findAllRoomsShouldReturnListOfRooms() {
        when(this.roomRepository.findAll()).thenReturn(List.of(this.room));

        List<RoomResponseDTO> response = this.roomService.findAll();

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getNumber()).isEqualTo("101");
        assertThat(response.get(0).getPricePerNight()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(response.get(0).getType()).isEqualTo(1);
    }

    @Test
    public void findAllRoomsPageableWithQueryPageableShouldReturnPageOfRooms() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> page = new PageImpl<>(List.of(this.room));

        String checkIn = "2026-10-01";
        String checkOut = "2026-10-05";

        when(this.roomRepository.findByTypeOrCapacityOrPricePerNightOrByHotelCity(null, null, null, null, null, pageable)).thenReturn(page);

        Page<RoomResponseDTO> response = this.roomService.findAllPageable(null, null, null, null, null, 
            DateUtils.convertStringParamToLocalDate(checkIn), DateUtils.convertStringParamToLocalDate(checkOut), pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getNumber()).isEqualTo("101");
        assertThat(response.getContent().get(0).getPricePerNight()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(response.getContent().get(0).getType()).isEqualTo(1);
    }

    @Test
    public void findAllRoomsPageableShouldReturnPageOfRooms() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> page = new PageImpl<>(List.of(this.room));

        when(this.roomRepository.findAll(pageable)).thenReturn(page);

        Page<RoomResponseDTO> response = this.roomService.findAllPageable(pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getNumber()).isEqualTo("101");
        assertThat(response.getContent().get(0).getPricePerNight()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(response.getContent().get(0).getType()).isEqualTo(1);
    }

    @Test
    public void findRoomByIdShouldReturnRoomDetailResponseDTOWhenIdExists() {
        room.getBookings().add(BookingFactory.createBooking());
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));

        RoomDetailResponseDTO response = roomService.findById(existingId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNumber()).isEqualTo("101");
        assertThat(response.getPricePerNight()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(response.getType()).isEqualTo(1);
    }

    @Test
    public void findRoomByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(roomRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.findById(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createShouldThrowNoExceptionWhenOperatorWorkingHotelIsEqualToHotelRequest() {
        user.getRoles().clear();
        user.addRole(RoleFactory.createOperatorRole());
        user.setWorkingHotel(hotel);
        when(hotelRepository.findById(existingId)).thenReturn(Optional.of(hotel));
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        assertThatCode(() -> roomService.create(request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void createShouldThrowNoExceptionWhenAdminIsCreatingRoom() {
        user.getRoles().clear();
        user.addRole(RoleFactory.createAdminRole());
        when(hotelRepository.findById(existingId)).thenReturn(Optional.of(hotel));
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        assertThatCode(() -> roomService.create(request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void createShouldThrowForbiddenExceptionWhenOperatorWorkingHotelIsDifferentFromHotelRequest() {
        user.getRoles().clear();
        user.addRole(RoleFactory.createOperatorRole());
        user.setWorkingHotel(hotel);
        request.setHotelId(2L);
        when(hotelRepository.findById(2L)).thenReturn(Optional.of(hotel));
        when(authService.getConnectedUser()).thenReturn(user);

        assertThatCode(() -> roomService.create(request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void createShouldThrowResourceNotFoundExceptionWhenHotelIdDoesNotExist() {
        when(hotelRepository.findById(existingId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> roomService.create(request, List.of(mockImage))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateShouldThrowNoExceptionWhenRoomIdExists() {
        user.getRoles().clear();
        user.addRole(RoleFactory.createOperatorRole());
        user.setWorkingHotel(hotel);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        assertThatCode(() -> roomService.update(existingId, request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenRoomIdDoesNotExist() {
        when(roomRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.update(nonExistingId, request, List.of(mockImage))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateShouldThrowNoExceptionWhenHotelIdIsChangedAndHotelExists() {
        request.setHotelId(2L);
        user.getRoles().clear();
        user.addRole(RoleFactory.createAdminRole());
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(hotelRepository.findById(2L)).thenReturn(Optional.of(hotel));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        assertThatCode(() -> roomService.update(existingId, request, List.of(mockImage))).doesNotThrowAnyException();
    }

    @Test
    public void updateShouldThrowNoExceptionWhenHotelIdIsChangedButHotelDoesNotExist() {
        request.setHotelId(nonExistingId);
        user.getRoles().clear();
        user.addRole(RoleFactory.createAdminRole());
        hotel.setId(2L);
        user.setWorkingHotel(hotel);
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(hotelRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.update(existingId, request, List.of(mockImage))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateShouldThrowNoExceptionWhenImagesAreNull() {
        user.getRoles().clear();
        user.addRole(RoleFactory.createOperatorRole());
        user.setWorkingHotel(hotel);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        assertThatCode(() -> roomService.update(existingId, request, null)).doesNotThrowAnyException();
    }

    @Test
    public void deleteShouldThrowNoExceptionWhenIdExists() {
        when(roomRepository.existsById(existingId)).thenReturn(true);

        assertThatCode(() -> roomService.delete(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(roomRepository.existsById(nonExistingId)).thenReturn(false);
        
        assertThatThrownBy(() -> roomService.delete(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteImageShouldThrowNoExceptionWhenImageIdExists() {
        when(imageRepository.existsById(existingId)).thenReturn(true);

        assertThatCode(() -> roomService.deleteImage(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteImageShouldThrowResourceNotFoundExceptionWhenImageIdDoesNotExist() {
        when(imageRepository.existsById(nonExistingId)).thenReturn(false);

        assertThatThrownBy(() -> roomService.deleteImage(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void findRatingsByRoomIdShouldReturnPageOfRoomRatings() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoomRating> page = new PageImpl<>(List.of(this.roomRating));
        when(roomRatingRepository.findAllByRoomId(existingId, pageable)).thenReturn(page);

        Page<RoomRatingResponseDTO> response = roomService.findAllRatingsByRoomId(existingId, pageable);

        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getRating()).isEqualTo(BigDecimal.valueOf(4.5));
        assertThat(response.getContent().get(0).getDescription()).isEqualTo("description");
    }

    @Test
    public void addRatingShouldThrowNoExceptionWhenDataIsValid() {
        Booking booking = BookingFactory.createBooking();
        booking.setRoom(room);
        user.getBookings().add(booking);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(roomRatingRepository.save(any(RoomRating.class))).thenReturn(roomRating);

        assertThatCode(() -> roomService.addRating(existingId, roomRatingRequest)).doesNotThrowAnyException();
    }

    @Test
    public void addRatingShouldThrowResourceNotFoundExceptionWhenRoomIdDoesNotExist() {
        when(roomRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.addRating(nonExistingId, roomRatingRequest)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void addRatingShouldThrowForbiddenExceptionWhenUserIsNotAbleToRateRoom() {
        Booking booking = BookingFactory.createBooking();
        booking.setRoom(room);
        user.getBookings().add(booking);
        user.getRatings().add(roomRating);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);

        assertThatThrownBy(() -> roomService.addRating(existingId, roomRatingRequest)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void updateRatingShouldThrowNoExceptionWhenDataIsValid() {
        when(roomRatingRepository.findById(existingId)).thenReturn(Optional.of(roomRating));
        when(roomRatingRepository.save(any(RoomRating.class))).thenReturn(roomRating);

        assertThatCode(() -> roomService.updateRating(existingId, roomRatingRequest)).doesNotThrowAnyException();
    }

    @Test
    public void updateRatingShouldThrowNoExceptionWhenConnectedUserIsAdminOrOperator() {
        user.addRole(RoleFactory.createOperatorRole());
        user.addRole(RoleFactory.createAdminRole());
        when(roomRatingRepository.findById(existingId)).thenReturn(Optional.of(roomRating));
        when(roomRatingRepository.save(any(RoomRating.class))).thenReturn(roomRating);

        assertThatCode(() -> roomService.updateRating(existingId, roomRatingRequest)).doesNotThrowAnyException();
    }

    @Test
    public void updateRatingShouldThrowResourceNotFoundExceptionWhenRoomRatingDoesNotExist() {
        when(roomRatingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateRating(nonExistingId, roomRatingRequest)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateRoomRatingShouldThrowForbiddenExceptionWhenConnectedUserIsNotTheOwner() {
        user.setId(2L);
        when(roomRatingRepository.findById(existingId)).thenReturn(Optional.of(roomRating));
        doThrow(new ForbiddenException("User is not allowed to update this room rating")).when(authService).verifyIfConnectedUserHasAdminPermission(roomRating.getUser().getId());

        assertThatThrownBy(() -> roomService.updateRating(existingId, roomRatingRequest)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void deleteRoomRatingShouldThrowNoExceptionWhenRoomRatingExistsAndConnectedUserIsTheOwner() {
        when(roomRatingRepository.findById(existingId)).thenReturn(Optional.of(roomRating));
        doNothing().when(roomRatingRepository).delete(any(RoomRating.class));

        assertThatCode(() -> roomService.deleteRating(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteRoomRatingShouldThrowNoExceptionWhenRoomRatingExistsAndConnectedUserHasAdminPermission() {
        user.addRole(RoleFactory.createOperatorRole());
        user.addRole(RoleFactory.createAdminRole());
        when(roomRatingRepository.findById(existingId)).thenReturn(Optional.of(roomRating));
        doNothing().when(roomRatingRepository).delete(any(RoomRating.class));

        assertThatCode(() -> roomService.deleteRating(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteRatingShouldThrowResourceNotFoundExceptionWhenRoomRatingDoesNotExist() {
        when(roomRatingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.deleteRating(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteteRoomRatingShouldThrowForbiddenExceptionWhenConnectedUserIsNotTheOwner() {
        user.setId(2L);
        when(roomRatingRepository.findById(existingId)).thenReturn(Optional.of(roomRating));
        doThrow(new ForbiddenException("User is not allowed to update this room rating")).when(authService).verifyIfConnectedUserHasAdminPermission(roomRating.getUser().getId());

        assertThatThrownBy(() -> roomService.updateRating(existingId, roomRatingRequest)).isInstanceOf(ForbiddenException.class);
    }

    

}
