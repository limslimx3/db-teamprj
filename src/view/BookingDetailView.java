package view;

import domain.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BookingDetailView extends JFrame {
    private Booking booking;

    public BookingDetailView(Booking booking) {
        this.booking = booking;

        // 상세 정보 패널 생성
        JPanel detailPanel = new JPanel();
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        // 상세 정보 표시
        StringBuilder seatsInfo = new StringBuilder();
        for (int i = 0; i < booking.getSeats().size(); i++) {
            seatsInfo.append(booking.getSeats().get(i))
                    .append(" ($")
                    .append(booking.getPrices().get(i))
                    .append(")");
            if (i < booking.getSeats().size() - 1) {
                seatsInfo.append(", ");
            }
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
                "Seats: " + seatsInfo.toString() +
                "</html>");
        detailInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailPanel.add(detailInfo);

        // 스크롤 패널 추가
        JScrollPane scrollPane = new JScrollPane(detailPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 기본 설정
        setTitle("Booking Details");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
