package dao;

import domain.Schedule;
import domain.Seat;

import java.sql.ResultSet;
import java.util.List;

public interface TheaterDAO {
    List<String> getTheaters(Long movieId);
    List<Schedule> getSchedules(Long movieId, String theaterId);
    List<Seat> getSeats(int theaterId, int scheduleId);
}
