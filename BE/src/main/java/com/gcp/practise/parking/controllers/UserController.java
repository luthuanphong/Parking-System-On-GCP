package com.gcp.practise.parking.controllers;

import com.gcp.practise.parking.dtos.requests.DepositRequest;
import com.gcp.practise.parking.dtos.requests.LoginRequest;
import com.gcp.practise.parking.dtos.requests.SignupRequest;
import com.gcp.practise.parking.dtos.responses.LoginResponse;
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/deposit")
    public ResponseEntity<UserEntity> deposit(
            @PathVariable Integer userId,
            @Valid @RequestBody DepositRequest request) {
        UserEntity updatedUser = userService.deposit(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        UserEntity user = userService.signup(request);
        return ResponseEntity.ok(user);
    }
}