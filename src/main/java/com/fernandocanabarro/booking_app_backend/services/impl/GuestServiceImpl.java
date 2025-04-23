package com.fernandocanabarro.booking_app_backend.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.GuestMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Guest;
import com.fernandocanabarro.booking_app_backend.repositories.GuestRepository;
import com.fernandocanabarro.booking_app_backend.services.GuestService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<GuestResponseDTO> findAll(Pageable pageable) {
        return this.guestRepository.findAll(pageable).map(GuestMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GuestResponseDTO findById(Long id) {
        return this.guestRepository.findById(id)
            .map(GuestMapper::convertEntityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Guest", id));
    }

    @Override
    @Transactional
    public void create(GuestRequestDTO request) {
        Guest entity = GuestMapper.convertRequestToEntity(request);
        this.guestRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, GuestRequestDTO request) {
        Guest entity = this.guestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Guest", id));
        GuestMapper.updateEntity(entity, request);
        this.guestRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.guestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Guest", id);
        }
        this.guestRepository.deleteById(id);
    }

}
