package com.aether.ms_cloudinary.shared.security.jwt;

import com.aether.ms_cloudinary.shared.exceptions.UnauthorizedException;
import com.aether.ms_cloudinary.shared.security.AuthenticationKeyFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
  private final JwtTokenProvider tokenProvider;

  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // The authentication-key takes precedence; the JWT is only required when it is absent.
    return request.getHeader(AuthenticationKeyFilter.HEADER_NAME) != null;
  }

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter)
      throws IOException, ServletException {
    try {
      String token = tokenProvider.resolveToken(request);

      if (token != null) {
        SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
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
}
