package com.fernandocanabarro.booking_app_backend.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.ActiveProfiles;

import com.fernandocanabarro.booking_app_backend.factories.BookingFactory;
import com.fernandocanabarro.booking_app_backend.factories.CreditCardFactory;
import com.fernandocanabarro.booking_app_backend.factories.PaymentFactory;
import com.fernandocanabarro.booking_app_backend.factories.RoomFactory;
import com.fernandocanabarro.booking_app_backend.factories.UserFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminUpdateBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.BoletoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;
import com.fernandocanabarro.booking_app_backend.repositories.BookingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.CreditCardRepository;
import com.fernandocanabarro.booking_app_backend.repositories.PaymentRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.EmailService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RoomIsUnavailableForBookingException;
import com.fernandocanabarro.booking_app_backend.services.impl.BookingServiceImpl;
import com.sendgrid.helpers.mail.Mail;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("it")
public class BookingServiceTests {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private AuthService authService;
    @Mock
    private EmailService emailService;

    private Booking booking;
    private User user;
    private Room room;
    private BookingRequestDTO bookingRequest;
    private AdminBookingRequestDTO adminBookingRequest;
    private BaseBookingRequestDTO updateBookingRequest;
    private AdminUpdateBookingRequestDTO adminUpdateBookingRequest;
    private BookingPaymentRequestDTO bookingPaymentRequest;
    private Long existingId;
    private Long nonExistingId;
    private Pageable pageable;
    private Page<Booking> page;

    @BeforeEach
    public void setup() {
        this.booking = BookingFactory.createBooking();
        this.user = UserFactory.createUser();
        this.room = RoomFactory.createRoom();
        this.bookingRequest = BookingFactory.createBookingRequest();
        this.adminBookingRequest = BookingFactory.createAdminBookingRequest();
        this.updateBookingRequest = BookingFactory.createUpdateBookingRequest();
        this.adminUpdateBookingRequest = BookingFactory.createAdminUpdateBookingRequest();
        this.bookingPaymentRequest = PaymentFactory.createDinheiroPaymentRequest();

        this.existingId = 1L;
        this.nonExistingId = 99L;
        this.pageable = PageRequest.of(0, 10);
        this.page = new PageImpl<>(List.of(booking));
    }

    @Test
    public void findAllBookingsDetailedShouldReturnListOfBookingDetailResponseDTO() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDetailResponseDTO> response = bookingService.findAllBookingsDetailed();

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getId()).isEqualTo(booking.getId());
        assertThat(response.get(0).getCheckIn()).isEqualTo(booking.getCheckIn());
        assertThat(response.get(0).getCheckOut()).isEqualTo(booking.getCheckOut());
        assertThat(response.get(0).getRoom().getId()).isEqualTo(booking.getRoom().getId());
        assertThat(response.get(0).getUser().getId()).isEqualTo(booking.getUser().getId());
    }

    @Test
    public void findAllPageableShouldReturnPageOfBookingResponseDTO() {
        
        when(bookingRepository.findAll(pageable)).thenReturn(page);

        Page<BookingResponseDTO> response = bookingService.findAllPageable(pageable);

        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(booking.getId());
        assertThat(response.getContent().get(0).getCheckIn()).isEqualTo(booking.getCheckIn());
        assertThat(response.getContent().get(0).getCheckOut()).isEqualTo(booking.getCheckOut());
        assertThat(response.getContent().get(0).getRoomId()).isEqualTo(booking.getRoom().getId());
        assertThat(response.getContent().get(0).getUserId()).isEqualTo(booking.getUser().getId());
    }

    @Test
    public void findByIdShouldReturnBookingDetailResponseDTOWhenBookingExists() {
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        doNothing().when(authService).verifyIfConnectedUserHasAdminPermission(booking.getId());

        BookingDetailResponseDTO response = bookingService.findById(existingId, true);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(booking.getId());
        assertThat(response.getCheckIn()).isEqualTo(booking.getCheckIn());
        assertThat(response.getCheckOut()).isEqualTo(booking.getCheckOut());
        assertThat(response.getRoom().getId()).isEqualTo(booking.getRoom().getId());
        assertThat(response.getUser().getId()).isEqualTo(booking.getUser().getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenBookingDoesNotExist() {
        when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findById(nonExistingId, true)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void findAllBookingsByUserShouldReturnPageOfBookingResponseDTOWhenIsSelfUser() {
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findByUserId(user.getId(), pageable)).thenReturn(page);

        Page<BookingResponseDTO> response = bookingService.findAllBookingsByUser(null, pageable, true);

        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(booking.getId());
        assertThat(response.getContent().get(0).getCheckIn()).isEqualTo(booking.getCheckIn());
        assertThat(response.getContent().get(0).getCheckOut()).isEqualTo(booking.getCheckOut());
        assertThat(response.getContent().get(0).getRoomId()).isEqualTo(booking.getRoom().getId());
        assertThat(response.getContent().get(0).getUserId()).isEqualTo(booking.getUser().getId());
    }

    @Test
    public void findAllBookingsByUserShouldReturnPageOfBookingResponseDTOWhenIsNotSelfUser() {
        when(bookingRepository.findByUserId(user.getId(), pageable)).thenReturn(page);

        Page<BookingResponseDTO> response = bookingService.findAllBookingsByUser(user.getId(), pageable, false);

        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(booking.getId());
        assertThat(response.getContent().get(0).getCheckIn()).isEqualTo(booking.getCheckIn());
        assertThat(response.getContent().get(0).getCheckOut()).isEqualTo(booking.getCheckOut());
        assertThat(response.getContent().get(0).getRoomId()).isEqualTo(booking.getRoom().getId());
        assertThat(response.getContent().get(0).getUserId()).isEqualTo(booking.getUser().getId());
    }

    @Test
    public void findAllBookingsByRoomShouldReturnPageOfBookingResponseDTOWhenRoomExists() {
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(RoomFactory.createRoom()));
        when(bookingRepository.findByRoomId(existingId, pageable)).thenReturn(page);

        Page<BookingResponseDTO> response = bookingService.findAllBookingsByRoom(booking.getRoom().getId(), pageable);

        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo(booking.getId());
        assertThat(response.getContent().get(0).getCheckIn()).isEqualTo(booking.getCheckIn());
        assertThat(response.getContent().get(0).getCheckOut()).isEqualTo(booking.getCheckOut());
        assertThat(response.getContent().get(0).getRoomId()).isEqualTo(booking.getRoom().getId());
        assertThat(response.getContent().get(0).getUserId()).isEqualTo(booking.getUser().getId());
    }

    @Test
    public void findAllBookingsByRoomShouldThrowResourceNotFoundExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> bookingService.findAllBookingsByRoom(nonExistingId, pageable)).isInstanceOf(ResourceNotFoundException.class);
    }
    
    @Test
    public void createBookingShouldThrowNoExceptionWhenDataIsValidAndIsSelfBooking() {
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> bookingService.createBooking(bookingRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void createBookingShouldThrowNoExceptionWhenPaymentTypeIsBoletoAndIsSelfBooking() {
        BoletoPayment boletoPayment = new BoletoPayment(booking.getTotalPrice(), false);
        boletoPayment.setId(1L);
        booking.setPayment(boletoPayment);
        bookingRequest.setPayment(PaymentFactory.createBoletoPaymentRequest());
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> bookingService.createBooking(bookingRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void createBookingShouldThrowNoExceptionWhenDataIsValidAndPaymentTypeIsCartaoAndIsOnlineAndIsSelfBooking() {
        bookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.findById(bookingRequest.getPayment().getCreditCardId())).thenReturn(Optional.of(CreditCardFactory.createCreditCard()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> bookingService.createBooking(bookingRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void createBookingShouldThrowNoExceptionWhenDataIsValidAndIsNotSelfBooking() {
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> bookingService.createBooking(adminBookingRequest, false)).doesNotThrowAnyException();
    }

    @Test
    public void createBookingShouldThrowBadRequestExceptionWhenPaymentTypeIsCartaoAndIsOnlineButIsNotSelfBooking() {
        bookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, false)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void createBookingShouldThrowForbiddenRequestExceptionWhenPaymentTypeIsCartaoAndIsOnlineAndIsSelfBookingButCreditCardDoesNotBelongToCurrentUser() {
        CreditCard creditCard = CreditCardFactory.createCreditCard();
        creditCard.getUser().setId(2L);
        bookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.findById(bookingRequest.getPayment().getCreditCardId())).thenReturn(Optional.of(creditCard));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void createBookingShouldThrowBadRequestExceptionWhenGuestsQuantityIsGreaterThanRoomCapacity() {
        bookingRequest.setGuestsQuantity(100);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void createBookingShouldThrowResourceNotFoundExceptionWhenIsNotSelfBookingButUserDoesNotExist() {
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));
        when(userRepository.findById(existingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(adminBookingRequest, false)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createBookingShouldThrowBadRequestExceptionWhenPaymentTypeIsDinheiroButIsOnline() {
        bookingRequest.getPayment().setIsOnlinePayment(true);

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void createBookingShouldThrowBadRequestExceptionWhenPaymentTypeIsCartaoButCreditCardIdHasNotBeenProvided() {
        bookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        bookingRequest.getPayment().setCreditCardId(null);

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void createBookingShouldThrowBadRequestExceptionWhenPaymentTypeIsCartaoButInstallmentQuantityIsNull() {
        bookingRequest.setPayment(PaymentFactory.createOnlineCartaoPaymentRequest());
        bookingRequest.getPayment().setInstallmentQuantity(null);

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void createBookingShouldThrowResourceNotFoundExceptionWhenRoomDoesNotExist() {
        bookingRequest.setRoomId(nonExistingId);
        when(roomRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createBookingShouldThrowRoomIsUnavailableForBookingExceptionWhenCheckInDateIsAfterCheckOutDate() {
        bookingRequest.setCheckIn(bookingRequest.getCheckOut().plusDays(1));
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(RoomIsUnavailableForBookingException.class);
    }

    @Test
    public void createBookingShouldThrowRoomIsUnavailableForBookingExceptionWhenRoomIsOccupiedOnBookingDates() {
        booking.setCheckIn(bookingRequest.getCheckIn());
        booking.setCheckOut(bookingRequest.getCheckOut());
        room.getBookings().add(booking);
        when(roomRepository.findById(existingId)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest, true)).isInstanceOf(RoomIsUnavailableForBookingException.class);
    }

    @Test
    public void updateBookingShouldThrowNoExceptionWhenDataIsValidAndIsSelfBooking() {
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));

        assertThatCode(() -> bookingService.updateBooking(existingId, updateBookingRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingShouldThrowNoExceptionWhenRoomIsChangedAndIsSelfBooking() {
        room.setId(2L);
        updateBookingRequest.setRoomId(2L);
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBooking(existingId, updateBookingRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingShouldThrowBadRequestExceptionWhenUpdateRequestGuestsQuantityIsGreaterThanCurrentRoomCapacity() {
        updateBookingRequest.setGuestsQuantity(100);
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(existingId, updateBookingRequest, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void updateBookingShouldThrowResourceNotFoundExceptionWhenRoomIsChangedButRoomDoesNotExistAndIsSelfBooking() {
        room.setId(2L);
        updateBookingRequest.setRoomId(2L);
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(roomRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBooking(existingId, updateBookingRequest, true)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateBookingShouldThrowBadRequestExceptionWhenRoomIsChangedAndUpdateRequestGuestsQuantityIsGreaterThanNewRoomCapacity() {
        room.setId(2L);
        updateBookingRequest.setRoomId(2L);
        updateBookingRequest.setGuestsQuantity(100);
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.updateBooking(existingId, updateBookingRequest, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void updateBookingShouldThrowNoExceptionWhenDataIsValidAndIsNotSelfBooking() {
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBooking(existingId, adminUpdateBookingRequest, false)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingShouldThrowNoExceptionWhenDataIsValidAndIsNotSelfBookingAndUserHasChanged() {
        User otherUser = new User();
        user.setId(2L);
        adminUpdateBookingRequest.setUserId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBooking(existingId, adminUpdateBookingRequest, false)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingShouldThrowNoExceptionWhenIsNotSelfBookingButUserDoesNotExist() {
        when(userRepository.findById(existingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBooking(existingId, adminUpdateBookingRequest, false)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateBookingShouldThrowNoExceptionWhenBookingDoesNotExist() {
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBooking(nonExistingId, updateBookingRequest, true)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowNoExceptionWhenDataIsValidAndIsSelfBooking() {
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingPaymentShouldThrowNoExceptionWhenPaymentTypeIsBoletoAndIsSelfBooking() {
        bookingPaymentRequest.setPaymentType(PaymentTypeEnum.BOLETO.getPaymentType());
        BoletoPayment boletoPayment = new BoletoPayment(booking.getTotalPrice(), false);
        boletoPayment.setId(1L);
        booking.setPayment(boletoPayment);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(emailService.createEmail(anyString(), anyString(), anyMap(), anyString())).thenReturn(new Mail());
        doNothing().when(emailService).sendEmail(any(Mail.class));

        assertThatCode(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingPaymentShouldThrowNoExceptionWhenBookingPaymentRequestIsCartaoAndIsSelfBookingAndIsOnline() {
        this.bookingPaymentRequest = PaymentFactory.createOnlineCartaoPaymentRequest();
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(creditCardRepository.findById(bookingPaymentRequest.getCreditCardId())).thenReturn(Optional.of(CreditCardFactory.createCreditCard()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingPaymentShouldThrowNoExceptionWhenDataIsValidAndIsNotSelfBooking() {
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, false)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingPaymentShouldThrowNoExceptionWhenBookingPaymentRequestIsCartaoAndISelfBookingAndIsOnline() {
        this.bookingPaymentRequest = PaymentFactory.createOnlineCartaoPaymentRequest();
        when(authService.getConnectedUser()).thenReturn(user);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(creditCardRepository.findById(bookingPaymentRequest.getCreditCardId())).thenReturn(Optional.of(CreditCardFactory.createCreditCard()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(booking.getPayment());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertThatCode(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true)).doesNotThrowAnyException();
    }

    @Test
    public void updateBookingPaymentShouldThrowResourceNotFoundExceptionWhenBookingPaymentRequestIsCartaoAndIsSelfBookingAndIsOnlineAndButCartaoDoesNotExist() {
        this.bookingPaymentRequest = PaymentFactory.createOnlineCartaoPaymentRequest();
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(creditCardRepository.findById(bookingPaymentRequest.getCreditCardId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowResourceNotFoundExceptionWhenBookingDoesNotExist() {
        when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBookingPayment(nonExistingId, bookingPaymentRequest, true))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowForbiddenExceptionWhenIsSelfBookingAndConnectedUserIsNotTheOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowBadRequestExceptionWhenPaymentTypeIsCartaoAndIsOnlineButIsNotSelfBooking() {
        bookingPaymentRequest = PaymentFactory.createOnlineCartaoPaymentRequest();
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, false))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowBadRequestExceptionWhenPaymentTypeIsCartaoAndIsOnlineButCreditCardDoesNotBelongToCurrentUser() {
        bookingPaymentRequest = PaymentFactory.createOnlineCartaoPaymentRequest();
        CreditCard creditCard = CreditCardFactory.createCreditCard();
        creditCard.getUser().setId(2L);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);
        when(paymentRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getPayment()));
        when(creditCardRepository.findById(bookingPaymentRequest.getCreditCardId())).thenReturn(Optional.of(creditCard));

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowBadRequestExceptionWhenPaymentTypeIsDinheiroButIsOnlinePayment() {
        bookingPaymentRequest.setIsOnlinePayment(true);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowBadRequestExceptionWhenIsCartaoPaymentButCreditCardIdHasNotBeenProvided() {
        this.bookingPaymentRequest = PaymentFactory.createOnlineCartaoPaymentRequest();
        this.bookingPaymentRequest.setCreditCardId(null);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void updateBookingPaymentShouldThrowBadRequestExceptionWhenIsCartaoPaymentButInstallmentQuantityIsNull() {
        this.bookingPaymentRequest = PaymentFactory.createOfflineCartaoPaymentRequest();
        this.bookingPaymentRequest.setInstallmentQuantity(null);
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        when(authService.getConnectedUser()).thenReturn(user);

        assertThatThrownBy(() -> bookingService.updateBookingPayment(existingId, bookingPaymentRequest, true))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void deleteBookingShouldThrowNoExceptionWhenBookingExists() {
        when(bookingRepository.findById(existingId)).thenReturn(Optional.of(booking));
        doNothing().when(authService).verifyIfConnectedUserHasAdminPermission(booking.getId());

        assertThatCode(() -> bookingService.deleteBooking(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteBookingShouldThrowResourceNotFoundExceptionWhenBookingDoesNotExist() {
        when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.deleteBooking(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

}
