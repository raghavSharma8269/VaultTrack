package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:email = '' OR :email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "ORDER BY u.updatedAt DESC")
    List<User> findUsersByFilters(
            @Param("email") String email,
            @Param("role") UserRole role
    );

}
