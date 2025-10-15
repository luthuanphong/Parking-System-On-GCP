package com.gcp.practise.parking.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_ciphertext", nullable = false)
    private String passwordCiphertext;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Column(name = "balance_cents", nullable = false)
    @Builder.Default
    private Long balanceCents = 0L;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
