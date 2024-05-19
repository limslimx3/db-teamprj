package dao.impl;

import dao.TheaterDAO;
import domain.Schedule;
import domain.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheaterDAOImpl implements TheaterDAO {
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

    public List<String> getTheaters(Long movieId) {
        List<String> theaters = new ArrayList<>();
        String sql = "SELECT DISTINCT 상영관.상영관번호 " +
                "FROM 상영관 " +
                "JOIN 상영일정 ON 상영관.상영관번호 = 상영일정.상영관번호 " +
                "WHERE 상영관.상영관사용여부 = 1 AND 상영일정.영화번호 = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    theaters.add(rs.getString("상영관번호"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    public List<Schedule> getSchedules(Long movieId, String theaterId) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT 상영일정번호, 상영요일, 상영시작시간 " +
                "FROM 상영일정 " +
                "WHERE 영화번호 = ? AND 상영관번호 = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, movieId);
            ps.setString(2, theaterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    schedules.add(new Schedule(
                            rs.getInt("상영일정번호"),
                            rs.getString("상영요일"),
                            rs.getString("상영시작시간")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public List<Seat> getSeats(int theaterId, int scheduleId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT 좌석.좌석번호, 티켓.티켓번호 IS NULL AS 좌석사용여부 " +
                "FROM 좌석 " +
                "LEFT JOIN 티켓 ON 좌석.좌석번호 = 티켓.좌석번호 AND 티켓.상영일정번호 = ? " +
                "WHERE 좌석.상영관번호 = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, theaterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(new Seat(
                            rs.getString("좌석번호"),
                            rs.getInt("좌석사용여부") == 1
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }
}
