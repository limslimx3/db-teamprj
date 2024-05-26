package dao;

import domain.Booking;

import java.util.List;

public interface BookingDAO {
    void createBooking(List<String> seatNumbers, int theaterId, int scheduleId, String paymentMethod, String paymentStatus, double amount, int memberId);
    List<Booking> getBookingsByMemberId(int memberId);
    void deleteBooking(int bookingId);
    void updateBooking(int bookId, String theaterId, int scheduleId, List<String> selectedSeats, String paymentMethod, String paymentStatus, double amount);
}
