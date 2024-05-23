package com.msrts.hostel.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private Long id;
    @NotNull(message = "Street should not be null in address")
    private String street;
    @NotNull(message = "City should not be null in address")
    private String city;
    @NotNull(message = "State should not be null in address")
    private String state;
    @NotNull(message = "Country should not be null in address")
    private String country;
    @NotNull(message = "Zipcode should not be null in address")
    private Long zipcode;
}
