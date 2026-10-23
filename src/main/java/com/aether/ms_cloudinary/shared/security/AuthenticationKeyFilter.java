package com.aether.ms_cloudinary.shared.security;

import com.aether.ms_cloudinary.shared.exceptions.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RequiredArgsConstructor
public class AuthenticationKeyFilter extends OncePerRequestFilter {
  public static final String HEADER_NAME = "authentication-key";
  private static final String PRINCIPAL = "internal-client";

  private final String internalAuthenticationKey;

  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter)
      throws IOException, ServletException {
    try {
      String authenticationKey = request.getHeader(HEADER_NAME);

      if (authenticationKey != null) {
        if (!isValid(authenticationKey)) throw new UnauthorizedException("exception.authentication-key.invalid");

        SecurityContextHolder.getContext().setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(PRINCIPAL, null, List.of())
        );
      }

      filter.doFilter(request, response);
    } catch (UnauthorizedException e) {

      handlerExceptionResolver.resolveException(
          request,
          response,
          null,
          e
      );
    }
  }

  private boolean isValid(String authenticationKey) {
    return MessageDigest.isEqual(
        authenticationKey.getBytes(StandardCharsets.UTF_8),
        internalAuthenticationKey.getBytes(StandardCharsets.UTF_8)
    );
  }
}
