package com.asmaeel.tazkara.identity.config;

import com.asmaeel.tazkara.identity.security.jwt.JwtAuthFilter;
import com.asmaeel.tazkara.identity.security.jwt.JwtVerifier;
import com.asmaeel.tazkara.identity.security.jwt.entrypoint.JwtAccessDenied;
import com.asmaeel.tazkara.identity.security.jwt.entrypoint.JwtAuthenticationEntryPoint;
import com.asmaeel.tazkara.identity.security.jwt.entrypoint.SecurityErrorWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtVerifier jwtVerifier,
                                                   ObjectMapper objectMapper) throws Exception {

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtVerifier);
        SecurityErrorWriter errorWriter = new SecurityErrorWriter(objectMapper);

        http

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // permitAll
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**", "/error").permitAll()
                        .requestMatchers("/auth/**","/api").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint(errorWriter))
                        .accessDeniedHandler(new JwtAccessDenied(errorWriter)))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
