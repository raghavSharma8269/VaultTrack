package com.example.VaultTrackBackend.service.auth;

import com.example.VaultTrackBackend.excpetions.ExceptionMessages;
import com.example.VaultTrackBackend.excpetions.user.UserNotFoundException;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserService {
    private final UserRepository userRepository;

    public GetCurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }
}
