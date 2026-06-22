package com.example.demo.config;

import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public DataSeeder(UserRepository userRepository, EventRepository eventRepository, SeatRepository seatRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only seed data if the database is currently completely empty
        if (userRepository.count() == 0 && eventRepository.count() == 0) {

            System.out.println("🌱 Database is empty! Starting automatic data seeding...");

            // 1. Create and save a dummy User
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john.doe@example.com");
            user.setPasswordHash("hashed_password_123"); // Simple placeholder for now
            userRepository.save(user);
            System.out.println("Created User: ID = " + user.getId() + " (" + user.getEmail() + ")");

            // 2. Create and save a dummy Event
            Event event = new Event();
            event.setTitle("Grand Tech Concert 2026");
            event.setDescription("An amazing high-concurrency music and tech festival.");
            event.setEventDate(LocalDateTime.now().plusDays(10)); // 10 days from now
            event.setTotalSeats(5); // Keeping it small for testing
            eventRepository.save(event);
            System.out.println("Created Event: ID = " + event.getId() + " (" + event.getTitle() + ")");

            // 3. Automatically generate 5 individual seats for this event
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                Seat seat = new Seat();
                seat.setEvent(event);
                seat.setSeatNumber("A-" + i);
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setVersion(0L); // Set initial version for locking tracking
                seats.add(seat);
            }
            seatRepository.saveAll(seats);
            System.out.println("Successfully generated 5 available seats for the event (IDs 1 to 5).");

            System.out.println("✅ Data seeding finished successfully!");
        } else {
            System.out.println("📢 Database already contains data. Skipping seeding step.");
        }
    }
}