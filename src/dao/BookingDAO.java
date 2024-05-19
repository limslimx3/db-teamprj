package dao;

import java.util.List;

public interface BookingDAO {
    void bookSeats(List<String> seatNumbers, int theaterId, int scheduleId, String paymentMethod, String paymentStatus, double amount, int memberId);

}
