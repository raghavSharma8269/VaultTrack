package com.example.VaultTrackBackend.excpetions.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNameAlreadyExistsException extends RuntimeException {
    public AccountNameAlreadyExistsException(String message) {
        super(message);
    }
}
