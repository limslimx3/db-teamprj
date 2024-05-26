package view;

import dao.impl.BookingDAOImpl;
import domain.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BookingDetailView extends JFrame {
    private Booking booking;

    public BookingDetailView(Booking booking) {
        this.booking = booking;

        JPanel detailPanel = new JPanel();
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        // 기존 예매 정보를 보여주는 레이블
        JLabel detailInfo = new JLabel(generateBookingInfoHTML(booking));
        detailInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailPanel.add(detailInfo);

        // 예매 취소 버튼
        JButton deleteButton = new JButton("예매 취소");
        deleteButton.addActionListener(this::handleDeleteAction);
        detailPanel.add(deleteButton);

        // 예매 수정 버튼
        JButton editButton = new JButton("예매 수정");
        editButton.addActionListener(e -> openEditView(booking));
        detailPanel.add(editButton);

        JScrollPane scrollPane = new JScrollPane(detailPanel);
        add(scrollPane, BorderLayout.CENTER);

        setTitle("예매내역 상세");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void handleDeleteAction(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(null, "예매를 취소하시겠습니까?", "예매 취소", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            new BookingDAOImpl().deleteBooking(booking.getId());
            JOptionPane.showMessageDialog(null, "예매가 취소되었습니다.");
            dispose();
        }
    }

    private void openEditView(Booking booking) {
        new BookingEditView(booking).setVisible(true);
    }

    private String generateBookingInfoHTML(Booking booking) {
        StringBuilder seatsInfo = new StringBuilder();
        for (String seat : booking.getSeats()) {
            seatsInfo.append(seat).append(", ");
        }

        return "<html>" +
                "Booking ID: " + booking.getId() + "<br>" +
                "Payment Method: " + booking.getPaymentMethod() + "<br>" +
                "Payment Status: " + booking.getPaymentStatus() + "<br>" +
                "Payment Amount: $" + booking.getPaymentAmount() + "<br>" +
                "Payment Date: " + booking.getPaymentDate() + "<br>" +
                "Show Day: " + booking.getShowDay() + "<br>" +
                "Show Time: " + booking.getShowTime() + "<br>" +
                "Theater Number: " + booking.getTheaterNumber() + "<br>" +
                "Seats: " + seatsInfo.substring(0, seatsInfo.length() - 2) + "</html>";  // 마지막 쉼표 제거
    }
}
