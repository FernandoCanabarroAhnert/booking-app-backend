package com.fernandocanabarro.booking_app_backend.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.RoomMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.RoomService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findAll(Pageable pageable) {
        return this.roomRepository.findAll(pageable).map(RoomMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDTO findById(Long id) {
        return this.roomRepository.findById(id)
            .map(RoomMapper::convertEntityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getUnavailableDatesFromRoomByRoomId(Long id) {
        Room room = this.roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return room.getUnavailableDates();
    }

    @Override
    @Transactional
    public void create(RoomRequestDTO request) {
        Hotel hotel = this.hotelRepository.findById(request.getHotelId())
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));
        Room entity = RoomMapper.convertRequestToEntity(request, hotel);
        this.roomRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, RoomRequestDTO request) {
        Room room = this.roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        RoomMapper.updateRoom(room, request);
        if (!request.getHotelId().equals(room.getHotel().getId())) {
            Hotel hotel = this.hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));
            room.setHotel(hotel);
        }
        this.roomRepository.save(room);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room", id);
        }
        this.roomRepository.deleteById(id);
    }

    
}
