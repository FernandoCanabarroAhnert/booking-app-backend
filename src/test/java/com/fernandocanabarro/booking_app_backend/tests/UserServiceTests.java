package com.fernandocanabarro.booking_app_backend.tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.fernandocanabarro.booking_app_backend.factories.HotelFactory;
import com.fernandocanabarro.booking_app_backend.factories.RoleFactory;
import com.fernandocanabarro.booking_app_backend.factories.UserFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.HotelRepository;
import com.fernandocanabarro.booking_app_backend.repositories.RoleRepository;
import com.fernandocanabarro.booking_app_backend.repositories.UserRepository;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RequiredWorkingHotelIdException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("it")
public class UserServiceTests {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private AdminCreateUserRequestDTO request;
    private AdminUpdateUserRequestDTO updateRequest;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    public void setup() {
        this.user = UserFactory.createUser();

        this.request = new AdminCreateUserRequestDTO();
        request.setFullName("name");
        request.setEmail("email2");
        request.setCpf("cpf2");
        request.setPhone("(11) 99999-9999");
        request.setBirthDate(LocalDate.of(2005,10, 28));
        request.setPassword("12345");
        request.setActivated(true);
        request.setRolesIds(new ArrayList<>(Arrays.asList(1L)));

        this.updateRequest = new AdminUpdateUserRequestDTO();
        updateRequest.setFullName("name");
        updateRequest.setEmail("email2");
        updateRequest.setCpf("cpf2");
        updateRequest.setPhone("(11) 99999-9999");
        updateRequest.setBirthDate(LocalDate.of(2005,10, 28));
        updateRequest.setActivated(true);
        updateRequest.setRolesIds(new ArrayList<>(Arrays.asList(1L)));

        existingId = 1L;
        nonExistingId = 99L;
    }

    @Test
    public void adminFindAllUsersShouldReturnListOfUserResponseDTO() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> response = userService.adminFindAllUsers();

        assertThat(response).isNotNull();
        assertThat(response.get(0).getFullName()).isEqualTo("name");
        assertThat(response.get(0).getEmail()).isEqualTo("email");
        assertThat(response.get(0).getCpf()).isEqualTo("cpf");
        assertThat(response.get(0).getPhone()).isEqualTo("(11) 99999-9999");
    }

    @Test
    public void adminFindAllUsersPageableShouldReturnPageOfUserResponseDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserResponseDTO> response = userService.adminFindAllUsersPageable(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent().get(0).getFullName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getEmail()).isEqualTo("email");
        assertThat(response.getContent().get(0).getCpf()).isEqualTo("cpf");
        assertThat(response.getContent().get(0).getPhone()).isEqualTo("(11) 99999-9999");
    }

    @Test
    public void adminFindUserByIdShouldReturnUserResponseDTOWhenIdExists() {
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.adminFindUserById(existingId);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("name");
        assertThat(response.getEmail()).isEqualTo("email");
        assertThat(response.getCpf()).isEqualTo("cpf");
        assertThat(response.getPhone()).isEqualTo("(11) 99999-9999");
    }

    @Test
    public void adminFindByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.adminFindUserById(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void adminCreateUserShouldThrowNoExceptionWhenDataIsValid() {
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> userService.adminCreateUser(request)).doesNotThrowAnyException();
    }

    @Test
    public void adminCreateUserShouldThrowNoExceptionWhenUserIsOperatorOrAdminAndHotelExists() {
        request.getRolesIds().add(2L);
        request.setWorkingHotelId(existingId);
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));
        when(hotelRepository.findById(existingId)).thenReturn(Optional.of(HotelFactory.createHotel()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> userService.adminCreateUser(request)).doesNotThrowAnyException();
    }

    @Test
    public void adminCreateUserShouldThrowResourceNotFoundExceptionWhenUserIsOperatorOrAdminButHotelDoesNotExist() {
        request.getRolesIds().add(2L);
        request.setWorkingHotelId(nonExistingId);
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));
        when(hotelRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.adminCreateUser(request)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void adminCreateUserShouldThrowRequiredWorkingHotelIdExceptionWhenUserIsOperatorOrAdminButHotelIdHasNotBeenProvided() {
        request.getRolesIds().add(2L);
        request.setWorkingHotelId(null);
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));

        assertThatThrownBy(() -> userService.adminCreateUser(request)).isInstanceOf(RequiredWorkingHotelIdException.class);
    }

    @Test
    public void adminCreateUserShouldThrowAlreadyExistingPropertyExceptionWhenEmailIsAlreadyInUse() {
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("email2")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.adminCreateUser(request)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void adminCreateUserShouldThrowAlreadyExistingPropertyExceptionWhenCpfIsAlreadyInUse() {
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.adminCreateUser(request)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void adminCreateUserShouldThrowResourceNotFoundExceptionWhenRoleDoesNotExist() {
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.adminCreateUser(request)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void adminUpdateUserShouldThrowNoExceptionWhenDataIsValid() {
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> userService.adminUpdateUser(existingId, updateRequest)).doesNotThrowAnyException();
    }

    @Test
    public void adminUpdateUserShouldThrowNoExceptionWhenUserIsOperatorOrAdminAndHotelExists() {
        updateRequest.getRolesIds().add(2L);
        updateRequest.getRolesIds().add(3L);
        updateRequest.setWorkingHotelId(existingId);
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(RoleFactory.createAdminRole()));
        when(hotelRepository.findById(existingId)).thenReturn(Optional.of(HotelFactory.createHotel()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> userService.adminUpdateUser(existingId, updateRequest)).doesNotThrowAnyException();
    }

    @Test
    public void adminUpdateUserShouldThrowNoExceptionWhenUserIsOperatorOrAdminAndHotelIdChangedAndItExists() {
        updateRequest.getRolesIds().add(2L);
        updateRequest.getRolesIds().add(3L);
        updateRequest.setWorkingHotelId(2L);
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(RoleFactory.createAdminRole()));
        when(hotelRepository.findById(2L)).thenReturn(Optional.of(HotelFactory.createHotel()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThatCode(() -> userService.adminUpdateUser(existingId, updateRequest)).doesNotThrowAnyException();
    }

    @Test
    public void adminUpdateUserShouldThrowResourceNotFoundExceptionWhenUserIsOperatorOrAdminButHotelDoesNotExist() {
        updateRequest.getRolesIds().add(2L);
        updateRequest.setWorkingHotelId(existingId);
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));
        when(hotelRepository.findById(existingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.adminUpdateUser(existingId, updateRequest)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void adminUpdateUserShouldThrowRequiredWorkingHotelIdExceptionWhenUserIsOperatorOrAdminButWorkingHotelIdHasNotBeenProvided() {
        updateRequest.getRolesIds().add(2L);
        updateRequest.setWorkingHotelId(null);
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(RoleFactory.createGuestRole()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(RoleFactory.createOperatorRole()));

        assertThatThrownBy(() -> userService.adminUpdateUser(existingId, updateRequest)).isInstanceOf(RequiredWorkingHotelIdException.class);
    }

    @Test
    public void adminUpdateUserShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> userService.adminUpdateUser(nonExistingId, updateRequest)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void adminUpdateUserShouldThrowNoExceptionWhenEmailIsAlreadyInUse() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("email2");
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.adminUpdateUser(existingId, updateRequest)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void adminUpdateUserShouldThrowNoExceptionWhenCPFIsAlreadyInUse() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setCpf("cpf2");
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("email2")).thenReturn(Optional.empty());
        when(userRepository.findByCpf("cpf2")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.adminUpdateUser(existingId, updateRequest)).isInstanceOf(AlreadyExistingPropertyException.class);
    }

    @Test
    public void adminDeleteUserShoulThrowNoExceptionWhenIdExists() {
        when(userRepository.existsById(existingId)).thenReturn(true);

        assertThatCode(() -> userService.adminDeleteUser(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void adminDeleteUserShoulThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(userRepository.existsById(nonExistingId)).thenReturn(false);

        assertThatThrownBy(() -> userService.adminDeleteUser(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void adminDeleteUserShouldThrowBadRequestExceptionWhenUserHasBookingsAssociatedWithIt() {
        when(userRepository.existsById(existingId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(existingId);

        assertThatThrownBy(() -> userService.adminDeleteUser(existingId)).isInstanceOf(BadRequestException.class);
    }

}
