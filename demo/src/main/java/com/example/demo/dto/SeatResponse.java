package com.example.demo.dto;

import com.example.demo.models.SeatStatus;
import java.io.Serializable;

// A Java Record automatically implements getters, constructors, and equals/hashCode natively!
public record SeatResponse(Long id, String seatNumber, SeatStatus status) implements Serializable {
    private static final long serialVersionUID = 1L;
}