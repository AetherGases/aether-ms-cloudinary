package com.aether.ms_cloudinary.shared.handlers.dto.output;

import org.springframework.http.HttpStatus;

public record ExceptionOutputDTO(
    String message,
    HttpStatus status
) {
}
