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
    private final EventService eventService;

    // Constructor injection for our dependencies
    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository,
                          UserRepository userRepository, EventRepository eventRepository,EventService eventService) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
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
        eventService.clearSeatCache(eventId);
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    }

    // 2. READ: Fetch the entire history of bookings for a specific user
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // 3. UPDATE: Cancel a booking and mark all associated seats back to AVAILABLE
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() == BookingStatus.FAILED) {
            throw new RuntimeException("Cannot cancel an already cancelled booking");
        }

        // Loop through booking items to release the seats back to the public pool
        for (BookingItem item : booking.getItems()) {
            Seat seat = item.getSeat();
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        }
        eventService.clearSeatCache(booking.getEvent().getId());
        booking.setStatus(BookingStatus.FAILED); // Using FAILED as our cancelled state
        return bookingRepository.save(booking);
    }

    // 4. DELETE: Purge the booking entirely from the system database
    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        // Safety check: If it was a confirmed active booking, release seats before deleting
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            for (BookingItem item : booking.getItems()) {
                Seat seat = item.getSeat();
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }
        }
        eventService.clearSeatCache(booking.getEvent().getId());
        bookingRepository.delete(booking);
    }
}
