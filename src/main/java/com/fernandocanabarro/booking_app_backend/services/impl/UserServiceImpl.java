package com.fernandocanabarro.booking_app_backend.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.UserMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Role;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoleRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.services.UserService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RequiredWorkingHotelIdException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> adminFindAllUsers(Pageable pageable) {
        return this.userRepository.findAll(pageable).map(UserMapper::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO adminFindUserById(Long id) {
        return this.userRepository.findById(id)
            .map(UserMapper::convertEntityToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    @Transactional
    public void adminCreateUser(AdminCreateUserRequestDTO request) {
        Optional<User> UserByEmail = this.userRepository.findByEmail(request.getEmail());
        if (UserByEmail.isPresent()) {
            throw new AlreadyExistingPropertyException("E-mail");
        }
        Optional<User> UserByCpf = this.userRepository.findByCpf(request.getCpf());
        if (UserByCpf.isPresent()) {
            throw new AlreadyExistingPropertyException("CPF");
        }
        
        User entity = UserMapper.convertRequestToEntity(request, passwordEncoder);
        request.getRolesIds().stream()
            .forEach(roleId -> {
                Role role = this.roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));
                entity.addRole(role);
            });
        if (entity.hasRole("ROLE_OPERATOR") || entity.hasRole("ROLE_ADMIN")) {
            if (request.getWorkingHotelId() == null) {
                throw new RequiredWorkingHotelIdException();
            }
            Hotel hotel = hotelRepository.findById(request.getWorkingHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getWorkingHotelId()));    
            entity.setWorkingHotel(hotel);
        }
        this.userRepository.save(entity);
    }

    @Override
    @Transactional
    public void adminUpdateUser(Long id, AdminUpdateUserRequestDTO request) {
        User entity = this.userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        Optional<User> UserByEmail = this.userRepository.findByEmail(request.getEmail());
        if (UserByEmail.isPresent()) {
            if (!UserByEmail.get().getId().equals(entity.getId())) {
                throw new AlreadyExistingPropertyException("E-mail");
            }
        }
        Optional<User> UserByCpf = this.userRepository.findByCpf(request.getCpf());
        if (UserByCpf.isPresent()) {
            if (!UserByCpf.get().getId().equals(entity.getId())) {
                throw new AlreadyExistingPropertyException("CPF");
            }
        }
        UserMapper.updateUser(entity, request);
        entity.getRoles().clear();
        request.getRolesIds().stream()
            .forEach(roleId -> {
                Role role = this.roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));
                entity.addRole(role);
            });
        if (entity.hasRole("ROLE_OPERATOR") || entity.hasRole("ROLE_ADMIN")) {
            if (request.getWorkingHotelId() == null) {
                throw new RequiredWorkingHotelIdException();
            }
            if (entity.getWorkingHotel() == null) {
                Hotel hotel = hotelRepository.findById(request.getWorkingHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getWorkingHotelId()));    
                entity.setWorkingHotel(hotel);
            }
            if (!request.getWorkingHotelId().equals(entity.getWorkingHotel().getId())) {
                Hotel hotel = hotelRepository.findById(request.getWorkingHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getWorkingHotelId()));    
                entity.setWorkingHotel(hotel);
            }
        }
        this.userRepository.save(entity);
    }

    @Override
    @Transactional
    public void adminDeleteUser(Long id) {
        if (!this.userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        this.userRepository.deleteById(id);
    }

    

}
