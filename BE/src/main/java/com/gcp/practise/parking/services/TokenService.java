package com.gcp.practise.parking.services;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface TokenService {
    String generateToken(String email);
    String createToken(String email, Map<String, Object> claims);
    Boolean validateToken(String token, UserDetails userDetails);
    String extractUsername(String token);
    Date extractExpiration(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
