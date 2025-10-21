package com.gcp.practise.parking.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepositRequest {
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "Amount is required")
    @Min(value = 1000, message = "Minimum deposit is 1000 cents ($10.00)")
    private Long amountCents;
}