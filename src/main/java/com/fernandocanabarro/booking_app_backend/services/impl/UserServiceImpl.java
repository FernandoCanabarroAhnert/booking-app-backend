package com.fernandocanabarro.booking_app_backend.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.UserMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseUserProperties;
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
    public List<UserResponseDTO> adminFindAllUsers() {
        return this.userRepository.findAll().stream().map(UserMapper::convertEntityToResponse).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> adminFindAllUsersPageable(Pageable pageable) {
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
        this.verifyIfEmailIsAlreadyInUse(request.getEmail());
        this.verifyIfCpfIsAlreadyInUse(request.getCpf());
        User entity = UserMapper.convertRequestToEntity(request, passwordEncoder);
        this.setUserRoles(entity, request.getRolesIds());
        if (entity.hasRole("ROLE_OPERATOR") || entity.hasRole("ROLE_ADMIN")) {
            this.verifyIfRequestHasWorkingHotelIdWhenUserHasRoleOperatorOrAdmin(request);
            Hotel hotel = hotelRepository.findById(request.getWorkingHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getWorkingHotelId()));    
            entity.setWorkingHotel(hotel);
        }
        this.userRepository.save(entity);
    }

    private void verifyIfEmailIsAlreadyInUse(String email) {
        Optional<User> UserByEmail = this.userRepository.findByEmail(email);
        if (UserByEmail.isPresent()) {
            throw new AlreadyExistingPropertyException("E-mail");
        }
    }

    private void verifyIfCpfIsAlreadyInUse(String cpf) {
        Optional<User> UserByCpf = this.userRepository.findByCpf(cpf);
        if (UserByCpf.isPresent()) {
            throw new AlreadyExistingPropertyException("CPF");
        }
    }

    private void setUserRoles(User entity, List<Long> rolesIds) {
        rolesIds.forEach(roleId -> {
                Role role = this.roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));
                entity.addRole(role);
            });
    }

    private void verifyIfRequestHasWorkingHotelIdWhenUserHasRoleOperatorOrAdmin(BaseUserProperties request) {
        if (request.getWorkingHotelId() == null) {
            throw new RequiredWorkingHotelIdException();
        }
    }

    @Override
    @Transactional
    public void adminUpdateUser(Long id, AdminUpdateUserRequestDTO request) {
        User entity = this.userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        this.verifyIfUpdateRequestEmailIsAlreadyInUse(request.getEmail(), entity);
        this.verifyIfUpdateRequestCpfIsAlreadyInUse(request.getCpf(), entity);
        UserMapper.updateUser(entity, request);
        entity.getRoles().clear();
        this.setUserRoles(entity, request.getRolesIds());
        if (entity.hasRole("ROLE_OPERATOR") || entity.hasRole("ROLE_ADMIN")) {
            this.verifyIfRequestHasWorkingHotelIdWhenUserHasRoleOperatorOrAdmin(request);
            this.updateUserWorkingHotelIfNeeded(entity, request);
        }
        this.userRepository.save(entity);
    }

    private void verifyIfUpdateRequestEmailIsAlreadyInUse(String email, User entity) {
        Optional<User> UserByEmail = this.userRepository.findByEmail(email);
        if (UserByEmail.isPresent()) {
            if (!UserByEmail.get().getId().equals(entity.getId())) {
                throw new AlreadyExistingPropertyException("E-mail");
            }
        }
    }

    private void verifyIfUpdateRequestCpfIsAlreadyInUse(String cpf, User entity) {
        Optional<User> UserByCpf = this.userRepository.findByCpf(cpf);
        if (UserByCpf.isPresent()) {
            if (!UserByCpf.get().getId().equals(entity.getId())) {
                throw new AlreadyExistingPropertyException("CPF");
            }
        }
    }

    private void updateUserWorkingHotelIfNeeded(User entity, AdminUpdateUserRequestDTO request) {
        boolean needsUpdate = entity.getWorkingHotel() == null ||
                          !request.getWorkingHotelId().equals(entity.getWorkingHotel().getId());
        if (needsUpdate) {
            Hotel hotel = hotelRepository.findById(request.getWorkingHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getWorkingHotelId()));
            entity.setWorkingHotel(hotel);
        }
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
