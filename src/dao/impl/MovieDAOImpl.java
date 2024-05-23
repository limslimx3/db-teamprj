package dao.impl;

import dao.MovieDAO;
import domain.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAOImpl implements MovieDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/db1";
    private String jdbcUsername = "user1";
    private String jdbcPassword = "user1";

    private static final String INSERT_MOVIE_SQL = "INSERT INTO 영화 (영화명, 감독명, 배우명, 장르, 썸네일경로) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_MOVIE_BY_ID = "SELECT 영화번호, 영화명, 감독명, 배우명, 장르, 썸네일경로 FROM 영화 WHERE 영화번호 = ?";
    private static final String SELECT_ALL_MOVIES = "SELECT * FROM 영화";
    private static final String DELETE_MOVIE_SQL = "DELETE FROM 영화 WHERE 영화번호 = ?";
    private static final String UPDATE_MOVIE_SQL = "UPDATE 영화 SET 영화명 = ?, 감독명 = ?, 배우명 = ?, 장르 = ?, 썸네일경로 = ? WHERE 영화번호 = ?";

    protected Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void addMovie(Movie movie) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MOVIE_SQL)) {
            preparedStatement.setString(1, movie.getTitle());
            preparedStatement.setString(2, movie.getDirector());
            preparedStatement.setString(3, movie.getActor());
            preparedStatement.setString(4, movie.getGenre());
            preparedStatement.setString(5, movie.getImageUrl());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Movie getMovie(Long id) {
        Movie movie = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MOVIE_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String title = rs.getString("영화명");
                String director = rs.getString("감독명");
                String actor = rs.getString("배우명");
                String genre = rs.getString("장르");
                String imageUrl = rs.getString("썸네일경로");
                movie = new Movie(id, title, director, actor, genre, imageUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }

    @Override
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_MOVIES)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("영화번호");
                String title = rs.getString("영화명");
                String director = rs.getString("감독명");
                String actor = rs.getString("배우명");
                String genre = rs.getString("장르");
                String imageUrl = rs.getString("썸네일경로");
                movies.add(new Movie(id, title, director, actor, genre, imageUrl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public void updateMovie(Movie movie) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MOVIE_SQL)) {
            preparedStatement.setString(1, movie.getTitle());
            preparedStatement.setString(2, movie.getDirector());
            preparedStatement.setString(3, movie.getActor());
            preparedStatement.setString(4, movie.getGenre());
            preparedStatement.setString(5, movie.getImageUrl());
            preparedStatement.setLong(6, movie.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMovie(int id) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MOVIE_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 제목,감독,배우,장르로 영화 리스트 조회
     */
    @Override
    public List<Movie> searchMovies(String title, String director, String actor, String genre) {
        List<Movie> movies = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM 영화 WHERE 1=1");

        List<String> parameters = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            query.append(" AND 영화명 LIKE ?");
            parameters.add("%" + title + "%");
        }
        if (director != null && !director.isEmpty()) {
            query.append(" AND 감독명 LIKE ?");
            parameters.add("%" + director + "%");
        }
        if (actor != null && !actor.isEmpty()) {
            query.append(" AND 배우명 LIKE ?");
            parameters.add("%" + actor + "%");
        }
        if (genre != null && !genre.isEmpty()) {
            query.append(" AND 장르 LIKE ?");
            parameters.add("%" + genre + "%");
        }

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setString(i + 1, parameters.get(i));
            }
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("영화번호");
                String resultTitle = rs.getString("영화명");
                String resultDirector = rs.getString("감독명");
                String resultActor = rs.getString("배우명");
                String resultGenre = rs.getString("장르");
                String imageUrl = rs.getString("썸네일경로");
                movies.add(new Movie(id, resultTitle, resultDirector, resultActor, resultGenre, imageUrl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
