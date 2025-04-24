package com.fernandocanabarro.booking_app_backend.services.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.BookingMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.BoletoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.CartaoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.DinheiroPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Guest;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;
import com.fernandocanabarro.booking_app_backend.models.entities.PixPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;
import com.fernandocanabarro.booking_app_backend.repositories.BookingRepository;
import com.fernandocanabarro.booking_app_backend.repositories.GuestRepository;
import com.fernandocanabarro.booking_app_backend.repositories.PaymentRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RoomIsUnavailableForBookingException;

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
        Payment payment = null;
        if (request.getPaymentType() == 1) {
            payment = new DinheiroPayment();
            payment.setPaymentType(PaymentTypeEnum.DINHEIRO);
            payment.setAmount(entity.getTotalPrice());
        }
        if (request.getPaymentType() == 2) {
            payment = new CartaoPayment();
            payment.setPaymentType(PaymentTypeEnum.CARTAO);
            payment.setAmount(entity.getTotalPrice());
            ((CartaoPayment) payment).setInstallmentQuantity(request.getInstallmentQuantity());
        }
        if (request.getPaymentType() == 3) {
            payment = new PixPayment();
            payment.setPaymentType(PaymentTypeEnum.PIX);
            payment.setAmount(entity.getTotalPrice());
        }
        if (request.getPaymentType() == 4) {
            payment = new BoletoPayment();
            payment.setPaymentType(PaymentTypeEnum.BOLETO);
            payment.setAmount(entity.getTotalPrice());
            ((BoletoPayment) payment).setExpirationDate(LocalDate.now().plusDays(30));
        }
        payment = this.paymentRepository.save(payment);
        entity.setPayment(payment);
        this.bookingRepository.save(entity);
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
