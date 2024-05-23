package com.msrts.hostel.model;

import com.msrts.hostel.entity.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long id;
    @NotBlank(message = "Payment type should be DEPOSIT/RENT")
    private String paymentType;
    @NotBlank(message = "Transaction type should be CASH/UPI/CARD/ACCOUNT-TRANSFER")
    private String transactionType;
    @NotNull(message = "Start date should not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @NotNull(message = "End date should not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @Min(value = 0L, message = "Amount should be greater than Zero")
    private double amount;
    @NotNull(message = "Tenant should not be null")
    private Tenant tenant;

}