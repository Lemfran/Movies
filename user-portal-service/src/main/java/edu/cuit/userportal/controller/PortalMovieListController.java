package edu.cuit.userportal.controller;

import edu.cuit.userportal.client.MovieClient;
import edu.cuit.userportal.client.ReviewClient;
import edu.cuit.userportal.client.UserClient;
import edu.cuit.userportal.entity.Movie;
import edu.cuit.userportal.entity.Review;
import edu.cuit.userportal.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("movies")
public class PortalMovieListController {
    @Autowired
    private MovieClient movieClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ReviewClient reviewClient;

    private User getUserFromSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return user;
    }

    @RequestMapping("/search")
    public String searchMovies(@RequestParam("title") String title,
                               @RequestParam(value = "runtimeFilter", required = false) String runtimeFilter,
                               @RequestParam(value = "scoreFilter", required = false) String scoreFilter,
                               @RequestParam(value = "yearFilter", required = false) String yearFilter,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               HttpSession session, Model model) {
        List<Movie> movies = movieClient.getSearchList(title).getData();
        movies = applyFilterAndSort(movies, runtimeFilter, scoreFilter, yearFilter, sortBy);
        model.addAttribute("movies", movies);
        model.addAttribute("searchTitle", title);
        model.addAttribute("runtimeFilter", runtimeFilter);
        model.addAttribute("scoreFilter", scoreFilter);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("sortBy", sortBy);
        getUserFromSession(session, model);
        return "movielist";
    }

    @GetMapping("/movielist")
    public String listMovies(@RequestParam(value = "runtimeFilter", required = false) String runtimeFilter,
                             @RequestParam(value = "scoreFilter", required = false) String scoreFilter,
                             @RequestParam(value = "yearFilter", required = false) String yearFilter,
                             @RequestParam(value = "sortBy", required = false) String sortBy,
                             HttpSession session, Model model) {
        List<Movie> movies = movieClient.findAllMovies().getData();
        movies = applyFilterAndSort(movies, runtimeFilter, scoreFilter, yearFilter, sortBy);
        model.addAttribute("movies", movies);
        model.addAttribute("runtimeFilter", runtimeFilter);
        model.addAttribute("scoreFilter", scoreFilter);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("sortBy", sortBy);
        getUserFromSession(session, model);
        return "movielist";
    }

    private List<Movie> applyFilterAndSort(List<Movie> movies, String runtimeFilter, String scoreFilter, String yearFilter, String sortBy) {
        if (movies == null) return movies;
        List<Movie> result = movies.stream().filter(m -> m != null).collect(Collectors.toList());

        if (runtimeFilter != null && !runtimeFilter.isEmpty()) {
            result = result.stream().filter(m -> {
                Integer rt = m.getRuntime();
                if (rt == null) return false;
                return switch (runtimeFilter) {
                    case "short" -> rt < 90;
                    case "medium" -> rt >= 90 && rt <= 150;
                    case "long" -> rt > 150;
                    default -> true;
                };
            }).collect(Collectors.toList());
        }

        if (scoreFilter != null && !scoreFilter.isEmpty()) {
            result = result.stream().filter(m -> {
                double sc = m.getAverageScore();
                return switch (scoreFilter) {
                    case "high" -> sc >= 4.0;
                    case "mid" -> sc >= 2.0 && sc < 4.0;
                    case "low" -> sc < 2.0;
                    default -> true;
                };
            }).collect(Collectors.toList());
        }

        if (yearFilter != null && !yearFilter.isEmpty()) {
            result = result.stream().filter(m -> {
                if (m.getReleaseDate() == null) return false;
                Calendar cal = Calendar.getInstance();
                cal.setTime(m.getReleaseDate());
                int year = cal.get(Calendar.YEAR);
                return switch (yearFilter) {
                    case "2020s" -> year >= 2020;
                    case "2010s" -> year >= 2010 && year < 2020;
                    case "2000s" -> year >= 2000 && year < 2010;
                    case "1990s" -> year >= 1990 && year < 2000;
                    case "before1990" -> year < 1990;
                    default -> true;
                };
            }).collect(Collectors.toList());
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Movie> comparator = switch (sortBy) {
                case "scoreDesc" -> Comparator.comparingDouble(Movie::getAverageScore).reversed();
                case "scoreAsc" -> Comparator.comparingDouble(Movie::getAverageScore);
                case "runtimeDesc" -> Comparator.comparingInt(Movie::getRuntime).reversed();
                case "runtimeAsc" -> Comparator.comparingInt(Movie::getRuntime);
                case "dateDesc" -> Comparator.comparing(Movie::getReleaseDate, Comparator.nullsLast(Comparator.reverseOrder()));
                case "dateAsc" -> Comparator.comparing(Movie::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> null;
            };
            if (comparator != null) {
                result = result.stream().sorted(comparator).collect(Collectors.toList());
            }
        }
        return result;
    }

    @GetMapping("/getmovie")
    public String getmovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        Movie movie = movieClient.findMovieById(movieId).getData();
        List<Review> reviews = reviewClient.findTopLevelReviewsByMovieId(movieId).getData();
        User currentUser = getUserFromSession(session, model);
        if (reviews != null) {
            for (Review review : reviews) {
                enrichReviewUser(review);
                // 获取回复
                List<Review> replies = reviewClient.findRepliesByParentId(review.getReviewId()).getData();
                if (replies != null) {
                    for (Review reply : replies) {
                        enrichReviewUser(reply);
                    }
                    review.setReplies(replies);
                }
                // 点赞状态
                if (currentUser != null && review.getReviewId() != null) {
                    review.setLiked(reviewClient.hasLiked(review.getReviewId(), currentUser.getUserId()).getData());
                }
            }
        }
        if (movie != null) {
            model.addAttribute("movie", movie);
            model.addAttribute("reviews", reviews);
            return "review";
        }
        return "error";
    }

    private void enrichReviewUser(Review review) {
        if (review == null || review.getUserId() == null) return;
        User user1 = userClient.findUserById(review.getUserId()).getData();
        if (user1 != null) {
            review.setUsername(user1.getUsername());
            review.setAvatar(user1.getAvatar());
            review.setNickname(user1.getNickname());
        }
    }
}
