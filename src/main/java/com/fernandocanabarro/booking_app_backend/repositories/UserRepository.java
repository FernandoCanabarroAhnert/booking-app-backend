package com.fernandocanabarro.booking_app_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.projections.UserDetailsProjection;
import com.fernandocanabarro.booking_app_backend.projections.UserSearchProjection;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);

    Page<User> findAllByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT u.email AS username, u.password, r.id AS roleId, r.authority
        FROM users u
        INNER JOIN user_role ur ON u.id = ur.user_id
        INNER JOIN roles r ON r.id = ur.role_id
        WHERE u.email = :email
    """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    List<UserSearchProjection> findAllByCpfContainingIgnoreCase(String cpf);

}
