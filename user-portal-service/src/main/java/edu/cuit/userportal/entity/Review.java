package edu.cuit.userportal.entity;

import java.io.Serializable;

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer reviewId;
    private Integer movieId;
    private Integer userId;
    private String content;
    private Integer score;
    private java.util.Date createdAt;

    private User user;
    private Movie movie;
    private String username;
    private String title;
    private String avatar;
    private String nickname;
    private String moviePoster;
    private String releaseDate;
    private Double averageScore;

    public Review() {}

    public Review(Integer movieId, Integer userId, String content, Integer score, java.util.Date createdAt) {
        this.movieId = movieId;
        this.userId = userId;
        this.content = content;
        this.score = score;
        this.createdAt = createdAt;
    }

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public java.util.Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getMoviePoster() { return moviePoster; }
    public void setMoviePoster(String moviePoster) { this.moviePoster = moviePoster; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
}
