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
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class PortalRecommendationController {

    @Autowired
    private MovieClient movieClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ReviewClient reviewClient;

    private void addUserToModel(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            User freshUser = userClient.findUserByUsername(user.getUsername()).getData();
            model.addAttribute("user", freshUser != null ? freshUser : user);
        }
    }

    @GetMapping({"/recommend", "/recommend/"})
    public String recommend(HttpSession session, Model model) {
        List<Movie> movies = movieClient.findAllMovies().getData();
        if (movies == null) movies = new ArrayList<>();

        // 热门推荐：按评分降序，取前8
        List<Movie> hotMovies = movies.stream()
            .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
            .limit(8)
            .collect(Collectors.toList());

        // 高分推荐：评分>=4.0，按评分降序，取前8
        List<Movie> highScoreMovies = movies.stream()
            .filter(m -> m.getAverageScore() >= 4.0)
            .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
            .limit(8)
            .collect(Collectors.toList());

        // 个性化推荐：基于用户评分历史
        User currentUser = (User) session.getAttribute("user");
        List<Movie> personalizedMovies = generatePersonalizedRecommendations(movies, currentUser, 8);

        model.addAttribute("hotMovies", hotMovies);
        model.addAttribute("highScoreMovies", highScoreMovies);
        model.addAttribute("personalizedMovies", personalizedMovies);

        addUserToModel(session, model);
        return "recommend";
    }

    @GetMapping({"/recommend/hot", "/recommend/hot/"})
    public String hotRecommendations(HttpSession session, Model model) {
        List<Movie> movies = movieClient.findAllMovies().getData();
        if (movies == null) movies = new ArrayList<>();

        List<Movie> hotMovies = movies.stream()
            .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
            .collect(Collectors.toList());

        model.addAttribute("movies", hotMovies);
        model.addAttribute("pageTitle", "热门推荐");
        model.addAttribute("pageIcon", "🔥");
        model.addAttribute("emptyMessage", "暂无热门电影推荐");

        addUserToModel(session, model);
        return "recommend-list";
    }

    @GetMapping({"/recommend/high-score", "/recommend/high-score/"})
    public String highScoreRecommendations(HttpSession session, Model model) {
        List<Movie> movies = movieClient.findAllMovies().getData();
        if (movies == null) movies = new ArrayList<>();

        List<Movie> highScoreMovies = movies.stream()
            .filter(m -> m.getAverageScore() >= 4.0)
            .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
            .collect(Collectors.toList());

        model.addAttribute("movies", highScoreMovies);
        model.addAttribute("pageTitle", "高分推荐");
        model.addAttribute("pageIcon", "⭐");
        model.addAttribute("emptyMessage", "暂无高分电影推荐");

        addUserToModel(session, model);
        return "recommend-list";
    }

    @GetMapping({"/recommend/personalized", "/recommend/personalized/"})
    public String personalizedRecommendations(HttpSession session, Model model) {
        List<Movie> movies = movieClient.findAllMovies().getData();
        if (movies == null) movies = new ArrayList<>();

        User currentUser = (User) session.getAttribute("user");
        List<Movie> personalizedMovies = generatePersonalizedRecommendations(movies, currentUser, Integer.MAX_VALUE);

        model.addAttribute("movies", personalizedMovies);
        model.addAttribute("pageTitle", "为你推荐");
        model.addAttribute("pageIcon", "💡");
        model.addAttribute("emptyMessage", "暂无个性化推荐");

        addUserToModel(session, model);
        return "recommend-list";
    }

    /**
     * 生成个性化推荐列表
     * 算法：根据用户历史高评分，推荐平均分接近的未评分电影
     */
    private List<Movie> generatePersonalizedRecommendations(List<Movie> allMovies, User user, int limit) {
        if (allMovies == null || allMovies.isEmpty()) {
            return new ArrayList<>();
        }

        // 未登录或没有评分记录时，返回热门高分电影
        if (user == null) {
            return allMovies.stream()
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .limit(limit)
                .collect(Collectors.toList());
        }

        List<Review> userReviews = reviewClient.findReviewsByUserId(user.getUserId()).getData();
        if (userReviews == null || userReviews.isEmpty()) {
            return allMovies.stream()
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .limit(limit)
                .collect(Collectors.toList());
        }

        // 过滤出用户评过分的顶级评论（非回复）
        List<Review> ratedReviews = userReviews.stream()
            .filter(r -> r.getParentId() == null && r.getScore() != null)
            .collect(Collectors.toList());

        if (ratedReviews.isEmpty()) {
            return allMovies.stream()
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .limit(limit)
                .collect(Collectors.toList());
        }

        // 计算用户平均评分倾向
        double userAvgScore = ratedReviews.stream()
            .mapToInt(Review::getScore)
            .average()
            .orElse(4.0);

        // 找出用户评分最高的电影，以其平均分为锚点
        Review topReview = ratedReviews.stream()
            .max(Comparator.comparingInt(Review::getScore))
            .orElse(null);

        double targetScore = userAvgScore;
        if (topReview != null) {
            Movie topMovie = allMovies.stream()
                .filter(m -> m.getMovieId().equals(topReview.getMovieId()))
                .findFirst()
                .orElse(null);
            if (topMovie != null) {
                targetScore = topMovie.getAverageScore();
            }
        }

        // 用户已评分电影ID集合
        Set<Integer> ratedMovieIds = ratedReviews.stream()
            .map(Review::getMovieId)
            .collect(Collectors.toSet());

        // 推荐与用户偏好平均分最接近的未评分电影
        final double finalTargetScore = targetScore;
        List<Movie> candidates = allMovies.stream()
            .filter(m -> !ratedMovieIds.contains(m.getMovieId()))
            .sorted(Comparator.comparingDouble(m -> Math.abs(m.getAverageScore() - finalTargetScore)))
            .limit(limit)
            .collect(Collectors.toList());

        // 如果候选不足，用热门高分补足
        if (candidates.size() < limit) {
            Set<Integer> candidateIds = candidates.stream()
                .map(Movie::getMovieId)
                .collect(Collectors.toSet());
            int need = limit - candidates.size();
            List<Movie> fillers = allMovies.stream()
                .filter(m -> !ratedMovieIds.contains(m.getMovieId()) && !candidateIds.contains(m.getMovieId()))
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .limit(need)
                .collect(Collectors.toList());
            candidates.addAll(fillers);
        }

        return candidates;
    }
}
