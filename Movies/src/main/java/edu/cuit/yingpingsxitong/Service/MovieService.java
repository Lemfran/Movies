package edu.cuit.yingpingsxitong.Service;

import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Dao.MovieDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieDao movieDao;

    @Autowired
    public MovieService(SqlSessionTemplate sqlSessionTemplate) {
        this.movieDao = sqlSessionTemplate.getMapper(MovieDao.class);
    }

    public void insertMovie(Movie movie) {
        movieDao.insertMovie(movie);
    }

    public Movie findMovieById(Integer movieId) {
        return movieDao.findMovieById(movieId);
    }

    public List<Movie> findAllMovies() {
        return movieDao.findAllMovies();
    }

    public List<Movie> getSearchList(String title){
        return movieDao.getSearchList(title);
    }

    public void updateMovie(Movie movie) {
        movieDao.updateMovie(movie);
    }

    public void deleteMovie(Integer movieId) {
        movieDao.deleteMovie(movieId);
    }

    public void updateAverageScore(Integer movieId){
        movieDao.updateAverageScore(movieId);
    };

    public int countMovies() {
        return movieDao.countMovies();
    }
}