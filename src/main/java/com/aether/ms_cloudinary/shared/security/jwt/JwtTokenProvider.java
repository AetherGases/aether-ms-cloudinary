package com.aether.ms_cloudinary.shared.security.jwt;

import com.aether.ms_cloudinary.shared.exceptions.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Stateless validation of access tokens issued by ms-auth: only the signature, expiration and
 * claims are checked, no user lookup is performed.
 */
@Component
public class JwtTokenProvider {
  public static final String HEADER_NAME = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String ROLES_CLAIM = "roles";

  private final JWTVerifier verifier;

  public JwtTokenProvider(@Value("${security.jwt.secret-key}") String secretKey) {
    if (!StringUtils.hasText(secretKey)) {
      throw new IllegalStateException("JWT_SECRET must be set.");
    }

    // Same key derivation used by ms-auth when signing the tokens.
    String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));

    this.verifier = JWT.require(Algorithm.HMAC256(encodedSecretKey.getBytes(StandardCharsets.UTF_8))).build();
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(HEADER_NAME);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }

    return null;
  }

  public Authentication getAuthentication(String token) {
    DecodedJWT decodedJWT = decodeToken(token);

    List<String> roles = decodedJWT.getClaim(ROLES_CLAIM).asList(String.class);

    List<SimpleGrantedAuthority> authorities = roles == null
        ? List.of()
        : roles.stream().map(SimpleGrantedAuthority::new).toList();

    return UsernamePasswordAuthenticationToken.authenticated(decodedJWT.getSubject(), null, authorities);
  }

  private DecodedJWT decodeToken(String token) {
    DecodedJWT decodedJWT;

    try {
      decodedJWT = verifier.verify(token);
    } catch (JWTVerificationException e) {
      throw new UnauthorizedException("exception.jwt.invalid-or-expired", e);
    }

    // Refresh tokens carry a jti and must not be accepted as access tokens.
    if (decodedJWT.getId() != null || !StringUtils.hasText(decodedJWT.getSubject())) {
      throw new UnauthorizedException("exception.jwt.invalid-or-expired");
    }

    return decodedJWT;
  }
}
