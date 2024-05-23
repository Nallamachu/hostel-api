package com.msrts.hostel.model;

import com.msrts.hostel.constant.Role;
import com.msrts.hostel.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String firstname;
  private String lastname;
  private String email;
  private String password;
  private String mobile;
  private String referralCode;
  private String referredByCode;
  private Address address;
  private Role role;
}
