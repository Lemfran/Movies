package edu.cuit.yingpingsxitong.Dao;

import edu.cuit.yingpingsxitong.Entity.Movie;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MovieDao {
    void insertMovie(Movie movie);
    Movie findMovieById(Integer movieId);
    List<Movie> findAllMovies();
    //模糊查询
    public List<Movie> getSearchList(String title);
    void updateMovie(Movie movie);
    void deleteMovie(Integer movieId);
    void updateAverageScore(Integer movieId);
}
