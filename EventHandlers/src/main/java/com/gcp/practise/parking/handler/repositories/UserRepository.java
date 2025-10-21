package com.gcp.practise.parking.handler.repositories;

import com.gcp.practise.parking.handler.common.CacheConfiguration;
import com.gcp.practise.parking.handler.entities.UserEntity;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);

    @Cacheable(value = CacheConfiguration.USER_REPOSITORY_CACHE, key = "#email")
    default UserEntity findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow();
    }

    @CachePut(value = CacheConfiguration.USER_REPOSITORY_CACHE, key = "#email")
    UserEntity save(UserEntity e);

    @Query("""
            SELECT u FROM UserEntity u
            """)
    Stream<UserEntity> getAllUser();
}