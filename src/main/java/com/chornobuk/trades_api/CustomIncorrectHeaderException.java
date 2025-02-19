package com.chornobuk.trades_api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomIncorrectHeaderException extends RuntimeException {
    public CustomIncorrectHeaderException(String message) {
        super(message);
    }
}

