package com.msrts.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long roomNo;
    private Long floorNo;
    private Long capacity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room")
    private Set<Tenant> tenants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", referencedColumnName = "id", nullable = false)
    private Hostel hostel;

}