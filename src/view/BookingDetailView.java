package view;

import dao.impl.BookingDAOImpl;
import domain.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BookingDetailView extends JFrame {
    private Booking booking;

    public BookingDetailView(Booking booking) {
        this.booking = booking;

        JPanel detailPanel = new JPanel();
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        StringBuilder seatsInfo = new StringBuilder();
        for (String seat : booking.getSeats()) {
            seatsInfo.append(seat).append(", ");
        }

        JLabel detailInfo = new JLabel("<html>" +
                "Booking ID: " + booking.getId() + "<br>" +
                "Payment Method: " + booking.getPaymentMethod() + "<br>" +
                "Payment Status: " + booking.getPaymentStatus() + "<br>" +
                "Payment Amount: $" + booking.getPaymentAmount() + "<br>" +
                "Payment Date: " + booking.getPaymentDate() + "<br>" +
                "Show Day: " + booking.getShowDay() + "<br>" +
                "Show Time: " + booking.getShowTime() + "<br>" +
                "Theater Number: " + booking.getTheaterNumber() + "<br>" +
                "Seats: " + seatsInfo.substring(0, seatsInfo.length() - 2) +  // Remove the last comma
                "</html>");
        detailInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailPanel.add(detailInfo);

        JButton deleteButton = new JButton("Delete Booking");
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this booking?", "Delete Booking", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                BookingDAOImpl bookingDAO = new BookingDAOImpl();
                bookingDAO.deleteBooking(booking.getId());
                JOptionPane.showMessageDialog(null, "Booking deleted successfully.");
                dispose(); // Close the current window
            }
        });
        detailPanel.add(deleteButton);

        JScrollPane scrollPane = new JScrollPane(detailPanel);
        add(scrollPane, BorderLayout.CENTER);

        setTitle("Booking Details");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
