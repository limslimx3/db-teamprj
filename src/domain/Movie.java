package domain;

public class Movie {
    private Long id;
    private String title;
    private String director;
    private String actor;
    private String genre;
    private String imageUrl;

    public Movie(Long id, String title, String director, String actor, String genre, String imageUrl) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.actor = actor;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public String getActor() { return actor; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
}
