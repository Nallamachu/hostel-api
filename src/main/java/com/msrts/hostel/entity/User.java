package com.msrts.hostel.entity;

import com.msrts.hostel.constant.Role;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String firstname;
  private String lastname;
  private String email;
  private String password;
  private String mobile;
  private String referralCode;
  private String referredByCode;
  private long points;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
  private Set<Hostel> hostels;

  @OneToOne(
          orphanRemoval = true,
          cascade = { CascadeType.PERSIST, CascadeType.REMOVE }
  )
  private Address address;

  public User(Integer id) {
    this.id = id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
