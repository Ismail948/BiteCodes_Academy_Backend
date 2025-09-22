package academy.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import academy.models.ExamAttempt;
import academy.request_response.CourseHistory;
import academy.request_response.DetailedAttemptAnalysis;
import academy.request_response.LeaderboardEntry;
import academy.request_response.PerformanceTrends;
import academy.request_response.UserAnalytics;
import academy.services.ExamAttemptService;

@RestController
@CrossOrigin(origins = "*") // Allow Next.js frontend
@RequestMapping("/api/attempts")
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

    public ExamAttemptController(ExamAttemptService examAttemptService) {
        this.examAttemptService = examAttemptService;
    }

//    @GetMapping("/user/{userId}")
//    public List<ExamAttempt> getAttemptsByUser(@PathVariable Long userId) {
//        return examAttemptService.getAttemptsByUser(userId);
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExamAttempt>> getAttemptsByUser(@PathVariable Long userId) {
        try {
            List<ExamAttempt> attempts = examAttemptService.getAttemptsByUser(userId);
            return ResponseEntity.ok(attempts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/analytics")
    public ResponseEntity<UserAnalytics> getUserAnalytics(@PathVariable Long userId) {
        try {
            UserAnalytics analytics = examAttemptService.getUserAnalytics(userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/course/{courseName}/history")
    public ResponseEntity<CourseHistory> getCourseHistory(@PathVariable Long userId, 
                                                         @PathVariable String courseName) {
        try {
            CourseHistory history = examAttemptService.getCourseHistory(userId, courseName);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{attemptId}/detailed")
    public ResponseEntity<DetailedAttemptAnalysis> getDetailedAnalysis(@PathVariable Long attemptId) {
        try {
            DetailedAttemptAnalysis analysis = examAttemptService.getDetailedAnalysis(attemptId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/performance-trends")
    public ResponseEntity<PerformanceTrends> getPerformanceTrends(@PathVariable Long userId,
                                                                 @RequestParam(defaultValue = "30") int days) {
        try {
            PerformanceTrends trends = examAttemptService.getPerformanceTrends(userId, days);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/course/{courseName}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getCourseLeaderboard(@PathVariable String courseName,
                                                                      @RequestParam(defaultValue = "10") int limit) {
        try {
            List<LeaderboardEntry> leaderboard = examAttemptService.getCourseLeaderboard(courseName, limit);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping
    public ExamAttempt saveAttempt(@RequestBody ExamAttempt attempt) {
        return examAttemptService.saveAttempt(attempt);
    }
}
