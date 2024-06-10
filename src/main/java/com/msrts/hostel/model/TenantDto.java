package com.msrts.hostel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msrts.hostel.entity.Address;
import com.msrts.hostel.entity.Payment;
import com.msrts.hostel.entity.Room;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantDto {

    private Long id;
    @NotNull(message = "Tenant firstname should not be null")
    private String firstName;
    private String middleName;
    @NotNull(message = "Tenant lastname should not be null")
    private String lastName;
    @NotNull(message = "Tenant mobile number is mandatory and should be 10 digits")
    private String mobile;
    @NotNull(message = "Tenant id-proof number should not be null")
    private String idNumber;
    @NotNull(message = "Tenant id-type can be Aadhaar/Driving License/Passport")
    private String idType;
    @NotNull(message = "Tenant entry date should not be null")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime entryDate;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @FutureOrPresent(message = "Exit date should be present of future not past date")
    private LocalDateTime exitDate;
    private boolean isActive;
    @NotNull(message = "Tenant address should not be null")
    private Address address;
    @NotNull(message = "Tenant room no should not be null")
    private Room room;
    private Set<Payment> payments;
}
