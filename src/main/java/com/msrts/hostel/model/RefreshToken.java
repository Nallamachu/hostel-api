package com.msrts.hostel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    private int id;
    private String userId;
    private String token;
    private int refreshCount;
    private Date expiryDate;
}
