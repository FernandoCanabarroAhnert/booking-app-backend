package com.fernandocanabarro.booking_app_backend.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.HotelMapper;
import com.fernandocanabarro.booking_app_backend.mappers.RoomMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoomRepository;
import com.fernandocanabarro.booking_app_backend.services.HotelService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<HotelResponseDTO> findAll(Pageable pageable) {
        return this.hotelRepository.findAll(pageable).map(HotelMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findRoomsByHotelId(Long hotelId, Pageable pageable) {
        return this.roomRepository.findByHotelId(hotelId, pageable).map(RoomMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO findById(Long id) {
        return this.hotelRepository.findById(id)
            .map(HotelMapper::convertEntityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
    }

    @Override
    @Transactional
    public void create(HotelRequestDTO request) {
        Hotel entity = HotelMapper.convertRequestToEntity(request);
        this.hotelRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, HotelRequestDTO request) {
        Hotel hotel = this.hotelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        HotelMapper.updateEntity(hotel, request);
        this.hotelRepository.save(hotel);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel", id);
        }
        this.hotelRepository.deleteById(id);
    }

}
