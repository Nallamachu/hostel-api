package com.msrts.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @Column(name = "zipcode")
    private String zipcode;
    @Column(name = "state")
    private String state;
    @Column(name = "street")
    private String street;

}
