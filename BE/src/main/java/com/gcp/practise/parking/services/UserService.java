package com.gcp.practise.parking.services;

import com.gcp.practise.parking.dtos.requests.DepositRequest;
import com.gcp.practise.parking.dtos.requests.LoginRequest;
import com.gcp.practise.parking.dtos.requests.SignupRequest;
import com.gcp.practise.parking.dtos.responses.LoginResponse;
import com.gcp.practise.parking.entities.UserEntity;

public interface UserService {
    UserEntity signup(SignupRequest request);
    LoginResponse login(LoginRequest request);
    UserEntity deposit(Integer userId, DepositRequest request);
}