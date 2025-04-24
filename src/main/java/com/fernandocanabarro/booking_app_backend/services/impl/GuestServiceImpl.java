package com.fernandocanabarro.booking_app_backend.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.GuestMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Guest;
import com.fernandocanabarro.booking_app_backend.repositories.GuestRepository;
import com.fernandocanabarro.booking_app_backend.services.GuestService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
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
        Optional<Guest> guestByEmail = this.guestRepository.findByEmail(request.getEmail());
        if (guestByEmail.isPresent()) {
            throw new AlreadyExistingPropertyException("E-mail");
        }
        Optional<Guest> guestByCpf = this.guestRepository.findByCpf(request.getCpf());
        if (guestByCpf.isPresent()) {
            throw new AlreadyExistingPropertyException("CPF");
        }
        Guest entity = GuestMapper.convertRequestToEntity(request);
        this.guestRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, GuestRequestDTO request) {
        Guest entity = this.guestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Guest", id));
        Optional<Guest> guestByEmail = this.guestRepository.findByEmail(request.getEmail());
        if (guestByEmail.isPresent()) {
            if (!guestByEmail.get().getId().equals(entity.getId())) {
                throw new AlreadyExistingPropertyException("E-mail");
            }
        }
        Optional<Guest> guestByCpf = this.guestRepository.findByCpf(request.getCpf());
        if (guestByCpf.isPresent()) {
            if (!guestByCpf.get().getId().equals(entity.getId())) {
                throw new AlreadyExistingPropertyException("CPF");
            }
        }
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

    @Override
    public UserWithPropertyAlreadyExistsDTO verifyIfGuestExistsByEmail(String email) {
        Optional<Guest> guestByEmail = this.guestRepository.findByEmail(email);
        return new UserWithPropertyAlreadyExistsDTO(guestByEmail.isPresent());
    }

    @Override
    public UserWithPropertyAlreadyExistsDTO verifyIfGuestExistsByCpf(String cpf) {
        Optional<Guest> guestByCpf = this.guestRepository.findByCpf(cpf);
        return new UserWithPropertyAlreadyExistsDTO(guestByCpf.isPresent());
    }

}
