package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.UserRepository;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfiguration.USER_CACHE_NAME, key = "#username", unless = "#result == null")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        VehicleEntity vehicle = vehicleRepository.findByUserId(user.getId())
            .orElse(null);

        return new CustomUserDetails(user, vehicle);
    }

    @CachePut(value = CacheConfiguration.USER_CACHE_NAME, key = "#userDetails.username")
    public void updateUserBalance(CustomUserDetails userDetails) {
        userRepository.findById(userDetails.getUserId())
        .ifPresent(user -> {
            user.setBalanceCents(userDetails.getBalanceCents());
            userRepository.save(user);
        });
    }
}
