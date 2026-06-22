package com.example.demo.models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name="booking_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="booking_id",nullable=false)
    private Booking booking;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="seat_id",nullable=false)
    private Seat seat;
}
