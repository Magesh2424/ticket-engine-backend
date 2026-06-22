package com.example.demo.dto;

import com.example.demo.models.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse implements Serializable {
    private static final long serialVersionUID = 1L; // Essential for Redis storage!

    private Long id;
    private String seatNumber;
    private SeatStatus status;
}