package com.gcp.practise.parking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.gcp.practise.parking.filters.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST = {
        "/api/users/login", 
        "/api/users/signup"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http, JwtTokenFilter jwtFilter) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
          .cors(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(req -> req
          .requestMatchers(AUTH_WHITELIST)
          .permitAll()
          .anyRequest()
          .authenticated())
          .sessionManagement(session -> session.sessionCreationPolicy(
              org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
          .addFilterBefore(
              jwtFilter, 
              UsernamePasswordAuthenticationFilter.class
           );

        return http.build();
    }
}
