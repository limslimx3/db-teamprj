package dao.impl;

import dao.BookingDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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

    public void bookSeats(List<String> seatNumbers, int theaterId, int scheduleId, String paymentMethod, String paymentStatus, double amount, int memberId) {
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
}
