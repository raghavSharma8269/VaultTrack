package com.example.VaultTrackBackend.service.auth;


import com.example.VaultTrackBackend.dto.auth.AuthResponseDTO;
import com.example.VaultTrackBackend.dto.auth.LoginRequestDTO;
import com.example.VaultTrackBackend.excpetions.user.UserNotFoundException;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.UserRepository;
import com.example.VaultTrackBackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public ResponseEntity<AuthResponseDTO> execute (LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userId(user.getUserId())
                .role(user.getRole().name())
                .build());
    }
}