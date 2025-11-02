package com.example.VaultTrackBackend.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    @NotNull(message = "Email cannot be empty")
    private String email;

    @NotNull(message = "Password cannot be empty")
    private String password;
}