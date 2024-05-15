package dao;

import domain.Movie;

import java.util.List;

public interface MovieDAO {
    void addMovie(Movie movie);
    Movie getMovie(Long id);
    List<Movie> getAllMovies();
    void updateMovie(Movie movie);
    void deleteMovie(int id);
    List<Movie> searchMovies(String title, String director, String actor, String genre);
}
