package com.msrts.hostel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long id;
    @NotBlank(message = "Payment type should be DEPOSIT/RENT")
    private String paymentType;
    @NotBlank(message = "Transaction type should be CASH/UPI/CARD/ACCOUNT-TRANSFER")
    private String transactionType;
    @NotNull(message = "Payment date should not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime paymentDate;
    @Min(value = 0L, message = "Amount should be greater than Zero")
    private double amount;
    @NotNull(message = "Tenant should not be null")
    private TenantDto tenant;

}