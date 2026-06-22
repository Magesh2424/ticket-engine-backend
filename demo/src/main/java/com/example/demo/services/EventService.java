package com.example.demo.services;

import com.example.demo.dto.SeatResponse;
import com.example.demo.repositories.SeatRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final SeatRepository seatRepository;

    public EventService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Cacheable(value = "eventSeats", key = "#eventId")
    public List<SeatResponse> getAvailableSeatsForEvent(Long eventId) {
        System.out.println("⚠️ Cache Miss! Fetching seats from MySQL database for event: " + eventId);
        return seatRepository.findByEventId(eventId).stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getStatus()))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "eventSeats", key = "#eventId")
    public void clearSeatCache(Long eventId) {
        System.out.println("🧹 Clearing Redis seat cache for event: " + eventId);
    }
}