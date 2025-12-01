package com.example.VaultTrackBackend.service.admin;

import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.UserRole;
import com.example.VaultTrackBackend.repository.UserRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUsersService {
    private final UserRepository userRepository;
    private final GetCurrentUserService getCurrentUserService;

    public GetUsersService(
            UserRepository userRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.userRepository = userRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<List<User>> execute(
            String email,
            UserRole role
    ) {

        User currentUser = getCurrentUserService.execute();

        if (!(currentUser.getRole() == UserRole.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        List<User> users = userRepository.findUsersByFilters(
                email,
                role
        );

        return ResponseEntity.ok(users);
    }
}
