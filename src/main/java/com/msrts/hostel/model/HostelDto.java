package com.msrts.hostel.model;

import com.msrts.hostel.entity.Room;
import com.msrts.hostel.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostelDto {

    private Long id;
    @NotBlank(message = "Hostel Name should not be blank")
    private String name;
    @NotBlank(message = "Hostel type should be Men/Women/Co-live")
    private String type;
    private boolean isActive;
    private Set<RoomDto> rooms;
    @NotNull(message = "Hostel address should be not null")
    private AddressDto address;
    @NotNull(message = "Hostel owner should be not null")
    private User owner;
}
