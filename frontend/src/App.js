import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]); // Tracks clean numeric IDs: [1, 2]
  const [bookingHistory, setBookingHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState({ type: '', text: '' });

  const SEAT_PRICE_INR = 1250; // Dynamic seat valuation in ₹

  // Synchronize seat maps directly with our Spring Boot application and Redis Cache
  const fetchSeats = async () => {
    try {
      const response = await axios.get('http://localhost:8081/api/events/1/seats');
      setSeats(response.data);
    } catch (error) {
      showToast('error', 'Could not sync live seat tracking matrix from Redis cache.');
    }
  };

  useEffect(() => {
    fetchSeats();
    // Long-polling interval fallback to keep layout accurate across clients
    const interval = setInterval(fetchSeats, 10000);
    return () => clearInterval(interval);
  }, []);

  const showToast = (type, text) => {
    setNotification({ type, text });
    setTimeout(() => setNotification({ type: '', text: '' }), 4000);
  };

  // BULLETPROOF SELECT & DESELECT CONTROLLER
  const handleSeatClick = (seat) => {
    if (seat.status === 'BOOKED') return; // Enforce immutable state lock on taken seats

    if (selectedSeats.includes(seat.id)) {
      // 🔓 DESELECT OPTION: Already selected, remove from active pool
      setSelectedSeats(selectedSeats.filter(id => id !== seat.id));
    } else {
      // 🔒 SELECT OPTION: Not selected yet, push into tracking array
      setSelectedSeats([...selectedSeats, seat.id]);
    }
  };

  const handleBookTickets = async () => {
    if (selectedSeats.length === 0) {
      showToast('error', 'Please pick at least one available seat node.');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        userId: 1, // Seeded user index mapping
        eventId: 1, // Seeded event index mapping
        seatIds: selectedSeats // Clean primitive array injection ([1, 2])
      };

      await axios.post('http://localhost:8081/api/bookings', payload);

      // Map names for the victory alert toast banner before array purges
      const bookedNumbers = selectedSeats.map(id => seats.find(s => s.id === id)?.seatNumber);
      showToast('success', `🎉 Booking Confirmed! Reserved: ${bookedNumbers.join(', ')}`);

      // Log successful transaction pipeline data into session history state
      const newHistoryItem = {
        timestamp: new Date().toLocaleTimeString(),
        seatsBooked: bookedNumbers.join(', '),
        amountPaid: selectedSeats.length * SEAT_PRICE_INR
      };
      setBookingHistory([newHistoryItem, ...bookingHistory]);

      setSelectedSeats([]); // Refresh checkout tray back to default
      fetchSeats(); // Evict and update global context structures from backend
    } catch (error) {
      const serverError = error.response?.data || "Transaction resource collision.";
      showToast('error', `Booking Denied: ${serverError}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-layout">
      {/* Dynamic Pop-Up Messaging Alerts */}
      {notification.text && (
        <div className={`toast-notification alert-${notification.type}`}>
          {notification.text}
        </div>
      )}

      <div className="booking-card">
        <header className="event-header">
          <span className="badge-live">● LIVE CACHE SYNC</span>
          <h1>Grand Tech Concert 2026</h1>
          <p className="event-meta">📍 Tech Arena Main Hall | Ticket Price: ₹{SEAT_PRICE_INR.toLocaleString('en-IN')}</p>
        </header>

        {/* Spatial Stage Horizon Visualizer */}
        <div className="stage-container">
          <div className="stage-glow"></div>
          <div className="stage-platform">STAGE DIRECTION</div>
        </div>

        {/* Color State Metadata Guide */}
        <div className="legend-container">
          <div className="legend-item"><span className="dot dot-available"></span> Available</div>
          <div className="legend-item"><span className="dot dot-selected"></span> Selected</div>
          <div className="legend-item"><span className="dot dot-booked"></span> Booked / Taken</div>
        </div>

        {/* Core Event Grid Mapping Structure */}
        <div className="grid-wrapper">
          {seats.map(seat => {
            const isSelected = selectedSeats.includes(seat.id);
            let seatClass = "seat-node";

            if (seat.status === 'BOOKED') seatClass += " booked";
            else if (isSelected) seatClass += " selected";
            else seatClass += " available";

            return (
              <div
                key={seat.id}
                className={seatClass}
                onClick={() => handleSeatClick(seat)}
              >
                <span className="seat-icon">🛋️</span>
                <span className="seat-label">{seat.seatNumber}</span>
                {seat.status === 'BOOKED' && <span className="booked-overlay-badge">TAKEN</span>}
              </div>
            );
          })}
        </div>

        {/* Interactive Dynamic Checkout Drawer */}
        <footer className="checkout-summary">
          <div className="price-breakdown">
            <span>
              Seats Selected: <strong>
                {selectedSeats.length > 0
                  ? selectedSeats.map(id => seats.find(s => s.id === id)?.seatNumber).join(', ')
                  : 'None'}
              </strong>
            </span>
            <span>Rupees to Pay: <strong className="vibrant-text">₹{(selectedSeats.length * SEAT_PRICE_INR).toLocaleString('en-IN')}</strong></span>
          </div>

          <button
            className="btn-checkout"
            disabled={loading || selectedSeats.length === 0}
            onClick={handleBookTickets}
          >
            {loading ? "Authorizing Gateway Entry..." : `Confirm Booking & Pay ₹${(selectedSeats.length * SEAT_PRICE_INR).toLocaleString('en-IN')}`}
          </button>
        </footer>

        {/* TRANSACTION HISTORY COMPONENT */}
        {bookingHistory.length > 0 && (
          <div className="history-panel">
            <h3>📜 Session Booking History</h3>
            <div className="history-list">
              {bookingHistory.map((item, idx) => (
                <div key={idx} className="history-row">
                  <span className="history-time">{item.timestamp}</span>
                  <span className="history-details">Confirmed: <strong>{item.seatsBooked}</strong></span>
                  <span className="history-cost">₹{item.amountPaid.toLocaleString('en-IN')}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;