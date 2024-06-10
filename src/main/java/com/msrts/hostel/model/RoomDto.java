package com.msrts.hostel.model;

import com.msrts.hostel.entity.Tenant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
    private Long id;
    @Size(min = 1, max = 1000, message = "Room number range should be between 1 to 1000")
    private Long roomNo;
    @Size(min = 1, max = 20, message = "Floor no range should be between 1 to 20")
    private Long floorNo;
    @Size(min = 1, max = 10, message = "Capacity range should be between 1 to 10")
    private Long capacity;
    private Set<Tenant> tenants;
    @NotNull(message = "Hostel should not be null")
    private HostelDto hostel;
}
