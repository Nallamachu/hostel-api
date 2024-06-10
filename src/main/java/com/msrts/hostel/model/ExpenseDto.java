package com.msrts.hostel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msrts.hostel.entity.Hostel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {

    private Long id;
    @NotNull(message = "Expense Type should be mandatory")
    private String expenseType;
    private String description;
    @Min(value = 0L, message = "Minimum amount value should be greater than Zero")
    private double amount;
    @NotNull(message = "Expense date should not be null")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    @NotNull(message = "Select the valid hostel to create expense record")
    private Hostel hostel;
}
