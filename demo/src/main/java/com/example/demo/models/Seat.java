package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

@Entity
@Table(name="seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="event_id",nullable=false)
    private Event event;
    @Column(nullable=false)
    private String seatNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private SeatStatus status=SeatStatus.AVAILABLE;
    @Version
    @Column(nullable=false)
    private Long version;

}
