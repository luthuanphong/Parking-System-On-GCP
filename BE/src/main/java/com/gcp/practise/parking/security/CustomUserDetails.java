package com.gcp.practise.parking.security;

import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.entities.VehicleEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    private final Integer userId;
    private final String username;
    private final String password;
    private final String licensePlate;
    private Long balanceCents;
    private final Integer vehicleId;
    private final VehicleEntity vehicle;

    public CustomUserDetails(UserEntity user, VehicleEntity vehicle) {
        this.userId = user.getId();
        this.username = user.getEmail();
        this.password = user.getPasswordCiphertext();
        this.licensePlate = vehicle != null ? vehicle.getPlateNormalized() : null;
        this.balanceCents = user.getBalanceCents();
        this.vehicleId = vehicle != null ? vehicle.getId() : null;
        this.vehicle = vehicle;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setBalanceCents(Long newBalanceCents) {
        this.balanceCents = newBalanceCents;
    }
}