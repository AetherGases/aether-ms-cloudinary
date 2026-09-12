package com.aether.ms_cloudinary.shared.security.dto.output;

import java.util.Date;

public record LoginOutputDTO(
    String email,
    Boolean authenticated,
    Date created,
    Date expiration,
    String accessToken,
    String refreshToken
) {
}
