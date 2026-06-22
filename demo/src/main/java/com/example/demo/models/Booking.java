package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable=false)
    private User user;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="event_id",nullable=false)
    private Event event;
    @Column(nullable=false)
    private LocalDateTime bookingDate=LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private BookingStatus status=BookingStatus.PENDING;
    @OneToMany(mappedBy="booking",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<BookingItem> items=new ArrayList<>();
}
