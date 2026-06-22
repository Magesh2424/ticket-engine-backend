package com.example.demo.controllers;

import com.example.demo.models.Seat;
import com.example.demo.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{eventId}/seats")
    public ResponseEntity<List<Seat>> getEventSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getAvailableSeatsForEvent(eventId));
    }
}