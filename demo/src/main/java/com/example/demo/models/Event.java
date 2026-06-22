package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String title;
    @Column(columnDefinition="TEXT")
    private LocalDateTime eventDate;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable=false)
    private Integer totalSeats;
}
