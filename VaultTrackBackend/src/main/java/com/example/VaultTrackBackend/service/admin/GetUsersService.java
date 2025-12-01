package com.example.VaultTrackBackend.service.admin;

import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.UserRole;
import com.example.VaultTrackBackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUsersService {
    private final UserRepository userRepository;

    public GetUsersService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<User>> execute(
            String email,
            UserRole role
    ) {
        List<User> users = userRepository.findUsersByFilters(
                email,
                role
        );

        return ResponseEntity.ok(users);
    }
}
