package com.msrts.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Number mobile;
    private String idProof;
    private String idType;
    private Date entryDate;
    private Date exitDate;
    private boolean isActive;

    @OneToOne(
            orphanRemoval = true,
            cascade = { CascadeType.PERSIST, CascadeType.REFRESH }
    )
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id", nullable = false)
    private Room room;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private Set<Payment> payments;
}
