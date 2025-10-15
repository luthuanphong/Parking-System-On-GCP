package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.dtos.requests.DepositRequest;
import com.gcp.practise.parking.dtos.requests.LoginRequest;
import com.gcp.practise.parking.dtos.requests.SignupRequest;
import com.gcp.practise.parking.dtos.responses.LoginResponse;
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.UserRepository;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.services.TokenService;
import com.gcp.practise.parking.services.UserService;
import com.gcp.practise.parking.utils.KmsEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final KmsEncryptionUtil kmsEncryptionUtil;
    private final TokenService tokenService;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public UserEntity signup(SignupRequest request) {
        // Generate random salt
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        // Combine password with salt and encrypt
        String passwordWithSalt = request.getPassword() + saltBase64;
        String encryptedPassword = kmsEncryptionUtil.encrypt(passwordWithSalt);

        // Create and save user
        UserEntity user = UserEntity.builder()
                .email(request.getUsername())
                .passwordSalt(saltBase64)
                .passwordCiphertext(encryptedPassword)
                .balanceCents(0L)
                .createdAt(OffsetDateTime.now())
                .build();
        
        user = userRepository.save(user);

        // Create and save vehicle
        VehicleEntity vehicle = VehicleEntity.builder()
                .user(user)
                .plateNormalized(normalizeplate(request.getLicensePlate()))
                .createdAt(OffsetDateTime.now())
                .build();
        
        vehicleRepository.save(vehicle);

        return user;
    }

    private String normalizeplate(String licensePlate) {
        return licensePlate.toUpperCase().replaceAll("\\s+", "");
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Combine password with stored salt and encrypt
        String passwordWithSalt = request.getPassword() + user.getPasswordSalt();
        String encryptedPassword = kmsEncryptionUtil.encrypt(passwordWithSalt);

        // Verify password
        if (!encryptedPassword.equals(user.getPasswordCiphertext())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Get user's vehicle
        VehicleEntity vehicle = vehicleRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Generate JWT token with claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("licensePlate", vehicle.getPlateNormalized());
        String token = tokenService.createToken(user.getEmail(), claims);

        // Create response
        return LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .username(user.getEmail())
                .licensePlate(vehicle.getPlateNormalized())
                .balanceCents(user.getBalanceCents())
                .build();
    }

    @Override
    public UserEntity deposit(Integer userId, DepositRequest request) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();
        Long currentAmount = userEntity.getBalanceCents() != null ? userEntity.getBalanceCents() : 0L;
        userEntity.setBalanceCents(currentAmount + request.getAmountCents());
        return userRepository.save(userEntity);
    }
}