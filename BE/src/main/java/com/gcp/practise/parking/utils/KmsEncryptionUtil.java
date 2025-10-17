package com.gcp.practise.parking.utils;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class KmsEncryptionUtil {

    @Value("${gcp.kms.project-id}")
    private String projectId;

    @Value("${gcp.kms.location-id}")
    private String locationId;

    @Value("${gcp.kms.key-ring-id}")
    private String keyRingId;

    @Value("${gcp.kms.key-id}")
    private String keyId;

    public String encrypt(String plaintext) {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            CryptoKeyName keyName = CryptoKeyName.of(projectId, locationId, keyRingId, keyId);
            
            ByteString plaintextBytes = ByteString.copyFrom(plaintext, StandardCharsets.UTF_8);
            ByteString ciphertext = client.encrypt(keyName, plaintextBytes).getCiphertext();
            
            return Base64.getEncoder().encodeToString(ciphertext.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data with KMS", e);
        }
    }

    public String decrypt(String ciphertext) {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            CryptoKeyName keyName = CryptoKeyName.of(projectId, locationId, keyRingId, keyId);
            
            byte[] decodedCiphertext = Base64.getDecoder().decode(ciphertext);
            ByteString ciphertextBytes = ByteString.copyFrom(decodedCiphertext);
            ByteString plaintext = client.decrypt(keyName, ciphertextBytes).getPlaintext();
            
            return plaintext.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data with KMS", e);
        }
    }
}