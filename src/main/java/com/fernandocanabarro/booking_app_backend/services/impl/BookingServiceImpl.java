package com.fernandocanabarro.booking_app_backend.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.BookingMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminUpdateBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.BoletoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.CartaoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.repositories.BookingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.CreditCardRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.repositories.PaymentRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.EmailService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.InvalidPaymentException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RoomIsUnavailableForBookingException;
import com.fernandocanabarro.booking_app_backend.services.strategy.BoletoPaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.CartaoPaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.DinheiroPaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.PaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.PixPaymentStrategy;
import com.sendgrid.helpers.mail.Mail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CreditCardRepository creditCardRepository;
    private final AuthService authService;
    private final EmailService emailService;
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingDetailResponseDTO> findAllBookingsDetailed() {
        return this.bookingRepository.findAll().stream().map(BookingMapper::convertEntityToDetailResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> findAllPageable(Pageable pageable) {
        return this.bookingRepository.findAll(pageable).map(BookingMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDetailResponseDTO findById(Long id) {
        Booking booking = this.bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        authService.verifyIfConnectedUserHasAdminPermission(booking.getUser().getId());
        return BookingMapper.convertEntityToDetailResponse(booking);
    }

    @Override
    @Transactional
    public void createBooking(BookingRequestDTO request, boolean isSelfBooking) {
        this.validateIfPaymentIsNotOnlineWhenPaymentTypeIsDinheiro(request.getPayment());
        this.validateIfCreditCardIdHasBeenProvidedWhenPaymentIsOnlineAndInstallmentQuantityWhenPaymentIsWithCreditCard(request.getPayment());
        Room room = this.roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));
        this.validateRoomAvailability(request, room, null);
        User user = this.getUserForBookingLogic(isSelfBooking, request);
        Booking entity = BookingMapper.convertRequestToEntity(request, room, user);
        Payment payment = this.getBookingPayment(request.getPayment().getPaymentType(), entity.getTotalPrice(),
            request.getPayment().getInstallmentQuantity(), request.getPayment().getIsOnlinePayment());
        this.setCartaoPaymentDataWhenPaymentIsOnlineAndWithCreditCard(payment, request.getPayment());
        payment = this.paymentRepository.save(payment);
        entity.setPayment(payment);
        this.bookingRepository.save(entity);
        this.sendBookingSummaryEmail(entity, user);
        this.sendBookingBoletoEmailWhenPaymentIsBoleto(entity, user, payment instanceof BoletoPayment);
    }

    private void validateIfPaymentIsNotOnlineWhenPaymentTypeIsDinheiro(BookingPaymentRequestDTO payment) {
        if (payment.getPaymentType() == 1 && payment.getIsOnlinePayment()) {
            throw new InvalidPaymentException("Dinheiro payment cannot be online.");
        }
    }

    private void validateIfCreditCardIdHasBeenProvidedWhenPaymentIsOnlineAndInstallmentQuantityWhenPaymentIsWithCreditCard(BookingPaymentRequestDTO payment) {
        if (payment.getPaymentType() == 2) {
            if (payment.getIsOnlinePayment() && payment.getCreditCardId() == null) {
                throw new BadRequestException("Credit Card ID must be provided for online credit card payments.");
            }
            if (payment.getInstallmentQuantity() == null) {
                throw new BadRequestException("Installment quantity must be provided for credit card payments.");
            }
        }
    }

    private void validateRoomAvailability(BaseBookingRequestDTO request, Room room, Long bookingIdToIgnore) {
        if (!room.isAvalableToBook(request.getCheckIn(), request.getCheckOut(), bookingIdToIgnore)) {
            throw new RoomIsUnavailableForBookingException(room.getId(), request.getCheckIn(), request.getCheckOut());
        }
        if (request.getCheckOut().isBefore(request.getCheckIn())) {
            throw new RoomIsUnavailableForBookingException("Check-out date cannot be before check-in date.");
        }
    }

    private User getUserForBookingLogic(boolean isSelfBooking, BaseBookingRequestDTO request) {
        return isSelfBooking
            ? this.authService.getConnectedUser() 
            : this.getUserFromAdminBookingRequest(request);
    }

    private User getUserFromAdminBookingRequest(BaseBookingRequestDTO request) {
        return request instanceof AdminBookingRequestDTO
            ? this.userRepository.findById(((AdminBookingRequestDTO) request).getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", ((AdminBookingRequestDTO) request).getUserId()))
            : request instanceof AdminUpdateBookingRequestDTO
                ? this.userRepository.findById(((AdminUpdateBookingRequestDTO) request).getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", ((AdminUpdateBookingRequestDTO) request).getUserId()))
                : this.authService.getConnectedUser();
    }

    private Payment getBookingPayment(Integer paymentType, BigDecimal amount, Integer installmentQuantity, boolean isOnlinePayment) {
        Map<Integer, PaymentStrategy> paymentStrategyMap = Map.of(
            1, new DinheiroPaymentStrategy(),
            2, new CartaoPaymentStrategy(),
            3, new PixPaymentStrategy(),
            4, new BoletoPaymentStrategy()
        );
        return paymentStrategyMap.get(paymentType).processBookingPayment(amount, installmentQuantity, isOnlinePayment);
    }

    private void setCartaoPaymentDataWhenPaymentIsOnlineAndWithCreditCard(Payment payment, BookingPaymentRequestDTO paymentRequest) {
        if (payment instanceof CartaoPayment && paymentRequest.getIsOnlinePayment()) {
            CreditCard creditCard = this.creditCardRepository.findById(paymentRequest.getCreditCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Credit Card", paymentRequest.getCreditCardId()));
            ((CartaoPayment)payment).setCreditCardId(creditCard.getId());
            ((CartaoPayment)payment).setCardHolderName(creditCard.getHolderName());
            ((CartaoPayment)payment).setLastFourDigits(creditCard.getCardNumber().substring(creditCard.getCardNumber().length() - 4));
            ((CartaoPayment)payment).setBrand(creditCard.getBrand());
            ((CartaoPayment)payment).setExpirationDate(creditCard.getExpirationDate());
        }
    }

    private void sendBookingSummaryEmail(Booking booking, User user) {
        Map<String, Object> variables = this.createEmailVariables(
            "Resumo da Reserva #" + booking.getId(),
            user.getFullName(),
            "Sua reserva foi aprovada! Clique no botão abaixo para visualizar/imprimir o resumo da sua reserva:",
            "http://localhost:8080/api/v1/bookings/"  + booking.getId() + "/pdf"
        );
        Mail mail = this.emailService.createEmail(user.getEmail(), "Resumo da Reserva", variables, "booking-email");
        this.emailService.sendEmail(mail);
    }

    private void sendBookingBoletoEmailWhenPaymentIsBoleto(Booking booking, User user, boolean isBoletoPayment) {
        if (isBoletoPayment) {
            Map<String, Object> variables = this.createEmailVariables(
                "Boleto da Reserva #" + booking.getId(),
                user.getFullName(),
                "O seu pagamento foi aprovado! Clique no botão abaixo para visualizar/imprimir o boleto da sua reserva:",
                "http://localhost:8080/api/v1/bookings/"  + booking.getId() + "/boleto/pdf"
            );
            Mail mail = this.emailService.createEmail(user.getEmail(), "Boleto da Reserva", variables, "booking-email");
            this.emailService.sendEmail(mail);
        }
    }

    private Map<String, Object> createEmailVariables(String titleText, String username, String bodyText, String link) {
        return Map.of(
            "titleText", titleText,
            "username", username,
            "bodyText", bodyText,
            "link", link
        );
    }

    @Override
    @Transactional
    public void updateBooking(Long id, BaseBookingRequestDTO request, boolean isSelfBooking) {
        User user = this.getUserForBookingLogic(isSelfBooking, request);
        Booking entity = this.bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        this.validateBookingOwnership(entity, user, isSelfBooking); 
        Room room = this.roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));
        this.validateRoomAvailability(request, room, entity.getId());
        BookingMapper.updateEntity(entity, request);
        this.updateBookingRoomIfNeeded(entity, request, room);
        this.updateBookingUserIfNeeded(entity, request, user, isSelfBooking);
        this.updateBookingStatusIfIsAdminUpdateRequest(request, entity, isSelfBooking);
        this.bookingRepository.save(entity);
    }

    private void validateBookingOwnership(Booking entity, User user, boolean isSelfBooking) {
        if (isSelfBooking && !user.getId().equals(entity.getUser().getId())) {
            throw new ForbiddenException("This booking does not belong to current user.");
        }
    }

    private void updateBookingRoomIfNeeded(Booking entity, BaseBookingRequestDTO request, Room room) {
        if (!request.getRoomId().equals(entity.getRoom().getId())) {
            entity.setRoom(room);
        }
    }

    private void updateBookingUserIfNeeded(Booking entity, BaseBookingRequestDTO request, User user, boolean isSelfBooking) {
        if (!isSelfBooking && !((AdminUpdateBookingRequestDTO) request).getUserId().equals(entity.getUser().getId())) {
            entity.setUser(user);
        }
    }

    private void updateBookingStatusIfIsAdminUpdateRequest(BaseBookingRequestDTO request, Booking entity, boolean isSelfBooking) {
        if (!isSelfBooking && request instanceof AdminUpdateBookingRequestDTO) {
            entity.setFinished(((AdminUpdateBookingRequestDTO) request).getIsFinished());
        }
    }

    @Override
    @Transactional
    public void updateBookingPayment(Long id, BookingPaymentRequestDTO request, boolean isSelfBooking) {
        Booking entity = this.bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        this.validateBookingOwnership(entity, entity.getUser(), isSelfBooking);
        this.validateIfPaymentIsNotOnlineWhenPaymentTypeIsDinheiro(request);
        this.validateIfCreditCardIdHasBeenProvidedWhenPaymentIsOnlineAndInstallmentQuantityWhenPaymentIsWithCreditCard(request);
        this.updatePayment(entity, request);
    }

    @Transactional
    private void updatePayment(Booking entity, BookingPaymentRequestDTO request) {
        Payment oldPayment = this.paymentRepository.findById(entity.getPayment().getId()).get();
        entity.setPayment(null);
        this.paymentRepository.delete(oldPayment);
        Payment newPayment = this.getBookingPayment(request.getPaymentType(), entity.getTotalPrice(), 
            request.getInstallmentQuantity(), request.getIsOnlinePayment());
        this.setCartaoPaymentDataWhenPaymentIsOnlineAndWithCreditCard(newPayment, request);
        newPayment = this.paymentRepository.save(newPayment);
        entity.setPayment(newPayment);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        if (!this.bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking", id);
        }
        this.bookingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> findAllBookingsByUser(Long userId, Pageable pageable, boolean isSelfUser) {
        Page<Booking> response = isSelfUser
            ? this.bookingRepository.findByUserId(authService.getConnectedUser().getId(), pageable)
            : this.bookingRepository.findByUserId(userId, pageable);
        return response.map(BookingMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> findAllBookingsByRoom(Long roomId, Pageable pageable) {
        this.roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        return this.bookingRepository.findByRoomId(roomId, pageable)
            .map(BookingMapper::convertEntityToResponse);
    }

}
