package edu.cuit.yingpingsxitong.Entity;

import java.io.Serializable;

public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer movieId;
    private String title;
    private String description;
    private java.util.Date releaseDate;
    private Integer runtime;
    private String posterImage;
    private double averageScore;

    // Constructors
    public Movie() {}

    public Movie(String title, String description, java.util.Date releaseDate, Integer runtime, String posterImage) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.posterImage = posterImage;
    }

    // Getters and Setters
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.util.Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(java.util.Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}