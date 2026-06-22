package com.example.demo.services;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // Constructor injection for our dependencies
    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository,
                          UserRepository userRepository, EventRepository eventRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }
    @Transactional
    public Booking createBooking(Long userId, Long eventId, List<Long> seatIds){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. Initialize the booking layout
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.PENDING);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat id " + seatId + " does not exist"));

            // Check availability
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already taken!");
            }

            // Reserve seat status change
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);

            // Link seat to this unique booking record
            BookingItem item = new BookingItem();
            item.setBooking(booking);
            item.setSeat(seat);
            booking.getItems().add(item);
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }
}
