package com.fernandocanabarro.booking_app_backend.services.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.BookingMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.Guest;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.repositories.BookingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.GuestRepository;
import com.fernandocanabarro.booking_app_backend.repositories.PaymentRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
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
    private final GuestRepository guestRepository;
    private final PaymentRepository paymentRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> findAll(Pageable pageable) {
        return this.bookingRepository.findAll(pageable).map(BookingMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO findById(Long id) {
        return this.bookingRepository.findById(id)
            .map(BookingMapper::convertEntityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    @Override
    @Transactional
    public void create(BookingRequestDTO request) {
        Room room = this.roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));
        if (!room.isAvalableToBook(request.getCheckIn(), request.getCheckOut())) {
            throw new RoomIsUnavailableForBookingException(room.getId(), request.getCheckIn(), request.getCheckOut());
        }
        Guest guest = this.guestRepository.findById(request.getGuestId())
            .orElseThrow(() -> new ResourceNotFoundException("Guest", request.getGuestId()));
        Booking entity = BookingMapper.convertRequestToEntity(request, room, guest);
        Payment payment = this.getBookingPayment(request.getPaymentType(), entity.getTotalPrice(), request.getInstallmentQuantity());
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

    @Override
    @Transactional
    public void update(Long id, BookingRequestDTO request) {
        Booking entity = this.bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        Room room = this.roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));
        if (!room.isAvalableToBook(request.getCheckIn(), request.getCheckOut())) {
            throw new RoomIsUnavailableForBookingException(room.getId(), request.getCheckIn(), request.getCheckOut());
        }
        BookingMapper.updateEntity(entity, request);
        if (!request.getRoomId().equals(entity.getRoom().getId())) {
            entity.setRoom(room);
        }
        if (!request.getGuestId().equals(entity.getGuest().getId())) {
            Guest guest = this.guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest", request.getGuestId()));
            entity.setGuest(guest);
        }
        this.bookingRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking", id);
        }
        this.bookingRepository.deleteById(id);
    }

}
