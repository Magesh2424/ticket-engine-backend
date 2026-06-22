package com.example.demo.controllers;

import com.example.demo.dto.SeatResponse; // Updated Import
import com.example.demo.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Updated return type to use SeatResponse instead of the raw Seat entity
    @GetMapping("/{eventId}/seats")
    public ResponseEntity<List<SeatResponse>> getEventSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getAvailableSeatsForEvent(eventId));
    }
}