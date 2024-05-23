package view;

import dao.impl.BookingDAOImpl;
import domain.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BookedListView extends JFrame {
    private int memberId;

    public BookedListView(int memberId) {
        this.memberId = memberId;

        // 예매 내역 패널 생성
        JPanel bookingPanel = new JPanel();
        bookingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bookingPanel.setLayout(new BoxLayout(bookingPanel, BoxLayout.Y_AXIS));

        // 스크롤 패널 추가
        JScrollPane scrollPane = new JScrollPane(bookingPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 예매 내역 불러오기
        loadBookingHistory(bookingPanel);

        // 기본 설정
        setTitle("예매내역");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void loadBookingHistory(JPanel bookingPanel) {
        BookingDAOImpl bookingDAO = new BookingDAOImpl();
        List<Booking> bookings = bookingDAO.getBookingsByMemberId(memberId);

        for (Booking booking : bookings) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(5, 5));
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            panel.setBackground(Color.WHITE);

            // 예매 정보 표시
            StringBuilder seatsInfo = new StringBuilder();
            for (int i = 0; i < booking.getSeats().size(); i++) {
                seatsInfo.append(booking.getSeats().get(i))
                        .append(" (₩")
                        .append(booking.getPrices().get(i))
                        .append(")");
                if (i < booking.getSeats().size() - 1) {
                    seatsInfo.append(", ");
                }
            }

            JLabel bookingInfo = new JLabel("<html>" +
                    "Booking ID: " + booking.getId() + "<br>" +
                    "Movie: " + booking.getMovieTitle() + "<br>" +
                    "Theater: " + booking.getTheaterName() + "<br>" +
                    "Date: " + booking.getShowDate() + "<br>" +
                    "Time: " + booking.getShowTime() + "<br>" +
                    "Seats: " + seatsInfo.toString() +
                    "</html>");
            bookingInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.add(bookingInfo, BorderLayout.CENTER);

            // 상세 조회 버튼 추가
            JButton detailButton = new JButton("예매내역 상세 조회");
            detailButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new BookingDetailView(booking).setVisible(true);
                }
            });
            panel.add(detailButton, BorderLayout.SOUTH);

            bookingPanel.add(panel);
        }

        bookingPanel.revalidate();
        bookingPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookedListView(1).setVisible(true)); // memberId를 1로 설정
    }
}
