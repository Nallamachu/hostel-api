package com.msrts.hostel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msrts.hostel.entity.Address;
import com.msrts.hostel.entity.Payment;
import com.msrts.hostel.entity.Room;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @NotNull(message = "Tenant id-type can be Aadhaar/Driving License/Voter Id/Passport")
    private String idType;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime entryDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @FutureOrPresent(message = "Exit date should be present of future not past date")
    private LocalDateTime exitDate;

    private boolean isActive;
    @NotNull(message = "Tenant address should not be null")
    private Address address;
    @NotNull(message = "Tenant room no should not be null")
    private RoomDto room;
    private Set<PaymentDto> payments;
}
