package com.fernandocanabarro.booking_app_backend.services.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.BookingMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.repositories.BookingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.repositories.PaymentRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RoomIsUnavailableForBookingException;
import com.fernandocanabarro.booking_app_backend.services.strategy.BoletoPaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.CartaoPaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.DinheiroPaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.PaymentStrategy;
import com.fernandocanabarro.booking_app_backend.services.strategy.PixPaymentStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AuthService authService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> findAll(Pageable pageable) {
        return this.bookingRepository.findAll(pageable).map(BookingMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO findById(Long id) {
        Booking booking = this.bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        authService.verifyIfConnectedUserHasAdminPermission(booking.getUser().getId());
        return BookingMapper.convertEntityToResponse(booking);
    }

    @Override
    @Transactional
    public void create(BookingRequestDTO request, boolean isSelfBooking) {
        Room room = this.roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));
        this.validateRoomAvailability(request, room, null);
        User user = this.getUserForBookingLogic(isSelfBooking, request);
        Booking entity = BookingMapper.convertRequestToEntity(request, room, user);
        Payment payment = this.getBookingPayment(request.getPayment().getPaymentType(), entity.getTotalPrice(), request.getPayment().getInstallmentQuantity());
        payment = this.paymentRepository.save(payment);
        entity.setPayment(payment);
        this.bookingRepository.save(entity);
    }

    private Payment getBookingPayment(Integer paymentType, BigDecimal amount, Integer installmentQuantity) {
        Map<Integer, PaymentStrategy> paymentStrategyMap = Map.of(
            1, new DinheiroPaymentStrategy(),
            2, new CartaoPaymentStrategy(),
            3, new PixPaymentStrategy(),
            4, new BoletoPaymentStrategy()
        );
        return paymentStrategyMap.get(paymentType).processBookingPayment(amount, installmentQuantity);
    }

    private User getUserForBookingLogic(boolean isSelfBooking, BookingRequestDTO request) {
        return isSelfBooking
            ? this.authService.getConnectedUser() 
            : this.getUserFromAdminBookingRequest(request);
    }

    private User getUserFromAdminBookingRequest(BookingRequestDTO request) {
        return this.userRepository.findById(((AdminBookingRequestDTO) request).getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", ((AdminBookingRequestDTO) request).getUserId())); 
    }

    private void validateRoomAvailability(BookingRequestDTO request, Room room, Long bookingIdToIgnore) {
        if (!room.isAvalableToBook(request.getCheckIn(), request.getCheckOut(), bookingIdToIgnore)) {
            throw new RoomIsUnavailableForBookingException(room.getId(), request.getCheckIn(), request.getCheckOut());
        }
    }

    @Override
    @Transactional
    public void update(Long id, BookingRequestDTO request, boolean isSelfBooking) {
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
        this.updateBookingPaymentIfNeeded(entity, request);
        this.bookingRepository.save(entity);
    }

    private void validateBookingOwnership(Booking entity, User user, boolean isSelfBooking) {
        if (isSelfBooking && !user.getId().equals(entity.getUser().getId())) {
            throw new ForbiddenException("This booking does not belong to current user.");
        }
    }

    private void updateBookingRoomIfNeeded(Booking entity, BookingRequestDTO request, Room room) {
        if (!request.getRoomId().equals(entity.getRoom().getId())) {
            entity.setRoom(room);
        }
    }

    private void updateBookingUserIfNeeded(Booking entity, BookingRequestDTO request, User user, boolean isSelfBooking) {
        if (!isSelfBooking && !((AdminBookingRequestDTO) request).getUserId().equals(entity.getUser().getId())) {
            entity.setUser(user);
        }
    }

    private void updateBookingPaymentIfNeeded(Booking entity, BookingRequestDTO request) {
        if (request.getPayment() != null) {
            if (!((Integer) entity.getPayment().getPaymentType().getPaymentType()).equals(request.getPayment().getPaymentType())) {
                Payment oldPayment = this.paymentRepository.findById(entity.getPayment().getId()).get();
                entity.setPayment(null);
                paymentRepository.delete(oldPayment);
                Payment newPayment = this.getBookingPayment(request.getPayment().getPaymentType(), entity.getTotalPrice(), request.getPayment().getInstallmentQuantity());
                newPayment = this.paymentRepository.save(newPayment);
                entity.setPayment(newPayment);
            }
        } 
    }

    @Override
    @Transactional
    public void delete(Long id) {
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
