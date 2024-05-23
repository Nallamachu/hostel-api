package com.msrts.hostel.model;

import com.msrts.hostel.entity.Address;
import com.msrts.hostel.entity.Payment;
import com.msrts.hostel.entity.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
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
    @Size(min = 10, max = 10, message = "Tenant mobile number is mandatory and should be 10 digits")
    private Number mobile;
    @NotNull(message = "Tenant id-proof number should not be null")
    private String idProof;
    @NotNull(message = "Tenant id-type can be Aadhaar/Driving License/Passport")
    private String idType;
    @NotNull(message = "Tenant entry date should not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date exitDate;
    private boolean isActive;
    @NotNull(message = "Tenant address should not be null")
    private Address address;
    @NotNull(message = "Tenant room no should not be null")
    private Room room;
    private Set<Payment> payments;
}
