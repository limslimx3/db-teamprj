package dao.impl;

import dao.BookingDAO;
import domain.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDAOImpl implements BookingDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/db1";
    private String jdbcUsername = "user1";
    private String jdbcPassword = "user1";

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 예매 및 티켓 데이터 삽입
     */
    public void createBook(List<String> seatNumbers, int theaterId, int scheduleId, String paymentMethod, String paymentStatus, double amount, int memberId) {
        String bookingSql = "INSERT INTO 예매 (결제방법, 결제상태, 결제금액, 결제일자, 회원번호) VALUES (?, ?, ?, CURDATE(), ?)";
        String ticketSql = "INSERT INTO 티켓 (발권여부, 표준가격, 판매가격, 예매번호, 상영일정번호, 상영관번호, 좌석번호) VALUES (1, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement bookingStmt = conn.prepareStatement(bookingSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement ticketStmt = conn.prepareStatement(ticketSql)) {

            conn.setAutoCommit(false); // 트랜잭션 시작

            // 예매 정보 삽입
            bookingStmt.setString(1, paymentMethod);
            bookingStmt.setString(2, paymentStatus);
            bookingStmt.setDouble(3, amount);
            bookingStmt.setInt(4, memberId);
            bookingStmt.executeUpdate();

            // 예매 번호 가져오기
            try (var generatedKeys = bookingStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookingId = generatedKeys.getInt(1);

                    // 티켓 정보 삽입
                    for (String seatNumber : seatNumbers) {
                        ticketStmt.setDouble(1, amount / seatNumbers.size());
                        ticketStmt.setDouble(2, amount / seatNumbers.size());
                        ticketStmt.setInt(3, bookingId);
                        ticketStmt.setInt(4, scheduleId);
                        ticketStmt.setInt(5, theaterId);
                        ticketStmt.setInt(6, Integer.parseInt(seatNumber));
                        ticketStmt.executeUpdate();
                    }
                }
            }

            conn.commit(); // 트랜잭션 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = getConnection()) {
                conn.rollback(); // 트랜잭션 롤백
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    /**
     * 특정 회원의 총 예매내역 조회
     */
    public List<Booking> getBookingsByMemberId(int memberId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT e.예매번호, m.영화명, s.상영시작일, s.상영시작시간, " +
                "t.상영관번호, seat.좌석번호, t.판매가격, " +
                "e.결제방법, e.결제상태, e.결제금액, e.결제일자, s.상영요일, s.상영관번호 " +
                "FROM 예매 e " +
                "JOIN 티켓 t ON e.예매번호 = t.예매번호 " +
                "JOIN 상영일정 s ON t.상영일정번호 = s.상영일정번호 " +
                "JOIN 영화 m ON s.영화번호 = m.영화번호 " +
                "JOIN 좌석 seat ON t.좌석번호 = seat.좌석번호 " +
                "WHERE e.회원번호 = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Booking> bookingMap = new HashMap<>();
                while (rs.next()) {
                    int bookingId = rs.getInt("예매번호");
                    Booking booking = bookingMap.get(bookingId);

                    if (booking == null) {
                        booking = new Booking();
                        booking.setId(bookingId);
                        booking.setMovieTitle(rs.getString("영화명"));
                        booking.setShowDate(rs.getDate("상영시작일").toString());
                        booking.setShowTime(rs.getTime("상영시작시간").toString());
                        booking.setTheaterName("Theater " + rs.getInt("상영관번호"));
                        booking.setPaymentMethod(rs.getString("결제방법"));
                        booking.setPaymentStatus(rs.getString("결제상태"));
                        booking.setPaymentAmount(rs.getDouble("결제금액"));
                        booking.setPaymentDate(rs.getDate("결제일자").toString());
                        booking.setShowDay(rs.getString("상영요일"));
                        booking.setTheaterNumber(rs.getString("상영관번호"));
                        bookingMap.put(bookingId, booking);
                    }

                    booking.addSeat(rs.getString("좌석번호"));
                    booking.addPrice(rs.getDouble("판매가격"));
                }

                bookings.addAll(bookingMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    /**
     * 예매 취소
     *  - 기존 예매한 좌석이나 티켓 정보 등 원래대로 복원
     */
    public void deleteBooking(int bookingId) {
        String selectSeatsSql = "SELECT 좌석번호 FROM 티켓 WHERE 예매번호 = ?";
        String deleteTicketsSql = "DELETE FROM 티켓 WHERE 예매번호 = ?";
        String deleteBookingSql = "DELETE FROM 예매 WHERE 예매번호 = ?";
        String updateSeatSql = "UPDATE 좌석 SET 좌석사용여부 = 0 WHERE 좌석번호 = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectSeatsStmt = conn.prepareStatement(selectSeatsSql);
             PreparedStatement deleteTicketsStmt = conn.prepareStatement(deleteTicketsSql);
             PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingSql);
             PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatSql)) {

            conn.setAutoCommit(false); // 트랜잭션 시작

            // 예매된 좌석 번호 가져오기
            selectSeatsStmt.setInt(1, bookingId);
            ResultSet rs = selectSeatsStmt.executeQuery();
            List<Integer> seatNumbers = new ArrayList<>();
            while (rs.next()) {
                seatNumbers.add(rs.getInt("좌석번호"));
            }

            // 예매 삭제
            deleteTicketsStmt.setInt(1, bookingId);
            deleteTicketsStmt.executeUpdate();

            deleteBookingStmt.setInt(1, bookingId);
            deleteBookingStmt.executeUpdate();

            // 좌석 상태 업데이트
            for (int seatNumber : seatNumbers) {
                updateSeatStmt.setInt(1, seatNumber);
                updateSeatStmt.executeUpdate();
            }

            conn.commit(); // 트랜잭션 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = getConnection()) {
                conn.rollback(); // 트랜잭션 롤백
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
}
