package com.example.demo.movie.model;

public class Movie {

    private Long id;
    private String externalMovieId;
    private String title;
    private Integer releaseYear;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalMovieId() {
        return externalMovieId;
    }

    public void setExternalMovieId(String externalMovieId) {
        this.externalMovieId = externalMovieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
}
