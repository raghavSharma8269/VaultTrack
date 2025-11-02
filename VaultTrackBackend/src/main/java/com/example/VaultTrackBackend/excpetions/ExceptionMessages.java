package com.example.VaultTrackBackend.excpetions;

import lombok.Getter;

@Getter
public enum ExceptionMessages {
    EMAIL_ALREADY_EXISTS("Email already exists"),
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid credentials"),
    ACCESS_DENIED("Access denied"),
    ITEM_NOT_FOUND("Item not found"),
    GENERIC_ERROR("An unexpected error occurred");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }
}
