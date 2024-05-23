package com.msrts.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "referral_sequence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private long sequence;
}