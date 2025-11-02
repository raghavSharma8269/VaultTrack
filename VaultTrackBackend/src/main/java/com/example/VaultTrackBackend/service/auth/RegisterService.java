package com.example.VaultTrackBackend.service.auth;

import com.example.VaultTrackBackend.dto.auth.AuthResponseDTO;
import com.example.VaultTrackBackend.dto.auth.RegisterRequestDTO;
import com.example.VaultTrackBackend.excpetions.ExceptionMessages;
import com.example.VaultTrackBackend.excpetions.user.EmailAlreadyExistsException;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.UserRole;
import com.example.VaultTrackBackend.repository.UserRepository;
import com.example.VaultTrackBackend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public RegisterService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public ResponseEntity<AuthResponseDTO> execute (RegisterRequestDTO registerRequestDTO) {
        if(userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(ExceptionMessages.EMAIL_ALREADY_EXISTS.getMessage());
        }

        User newUser = User.builder()
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .firstName(registerRequestDTO.getFirstName())
                .lastName(registerRequestDTO.getLastName())
                .role(UserRole.USER)
                .build();

        userRepository.save(newUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(jwtToken)
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .userId(newUser.getUserId())
                .role(newUser.getRole().name())
                .build());
    }
}
