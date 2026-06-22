package com.example.demo.dto;

import java.util.List;

public class BookingRequest {
    private Long userId;
    private Long eventId;
    private List<Long> seatIds;

    // Default Constructor
    public BookingRequest() {}

    // All Arguments Constructor
    public BookingRequest(Long userId, Long eventId, List<Long> seatIds) {
        this.userId = userId;
        this.eventId = eventId;
        this.seatIds = seatIds;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
}