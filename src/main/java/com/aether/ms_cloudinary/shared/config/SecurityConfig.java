package com.aether.ms_cloudinary.shared.config;

import com.aether.ms_cloudinary.shared.security.AuthenticationKeyFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
  public static final String SECURITY = "AuthenticationKey";

  private final String internalAuthenticationKey;
  private final HandlerExceptionResolver handlerExceptionResolver;

  public SecurityConfig(
      @Value("${security.internal-authentication-key}") String internalAuthenticationKey,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
  ) {
    if (!StringUtils.hasText(internalAuthenticationKey)) {
      throw new IllegalStateException("INTERNAL_AUTHENTICATION_KEY must be set.");
    }

    this.internalAuthenticationKey = internalAuthenticationKey;
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  @Bean
  UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http){
    AuthenticationKeyFilter customFilter = new AuthenticationKeyFilter(internalAuthenticationKey, handlerExceptionResolver);

    return http
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(
            authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(
                    "/api/**"
                ).authenticated()
                .anyRequest().denyAll()

        ).exceptionHandling(exception -> exception
            .authenticationEntryPoint(
                (request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            )
        )
        .cors(cors -> {})
        .build();

  }
}
