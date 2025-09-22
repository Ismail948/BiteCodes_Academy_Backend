package academy.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import academy.models.ExamAttempt;
import academy.models.QuestionResult;
import academy.models.TopicPerformance;
import academy.repositories.ExamAttemptRepository;
import academy.repositories.ExamQuestionRepository;
import academy.request_response.ComparisonData;
import academy.request_response.CourseHistory;
import academy.request_response.DetailedAttemptAnalysis;
import academy.request_response.LeaderboardEntry;
import academy.request_response.PerformanceTrends;
import academy.request_response.ProgressData;
import academy.request_response.QuestionAnalysis;
import academy.request_response.ScoreProgression;
import academy.request_response.TimeAnalysis;
import academy.request_response.TimeDistribution;
import academy.request_response.TopicAnalysis;
import academy.request_response.TrendPoint;
import academy.request_response.UserAnalytics;
@Service
public class ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public ExamAttemptService(ExamAttemptRepository examAttemptRepository,
                                    ExamQuestionRepository examQuestionRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamAttempt> getAttemptsByUser(Long userId) {
        return examAttemptRepository.findByUserId(userId);
    }
    public List<ExamAttempt> getAttemptsByUserId(Long userId) {
        return examAttemptRepository.findByUserId(userId);
    }

    
    
    public ExamAttempt saveAttempt(ExamAttempt attempt) {
        return examAttemptRepository.save(attempt);
    }
    public Optional<ExamAttempt> getAttemptById(Long attemptId) {
        return examAttemptRepository.findById(attemptId);
    }

    // New method to get attempts by course name
    public List<ExamAttempt> getAttemptsByCourseName(String courseName) {
        return examAttemptRepository.findByCourseName(courseName);
    }

    public UserAnalytics getUserAnalytics(Long userId) {
        List<ExamAttempt> attempts = examAttemptRepository.findByUserId(userId);
        
        if (attempts.isEmpty()) {
            return new UserAnalytics(); // Return empty analytics
        }

        UserAnalytics analytics = new UserAnalytics();
        
        // Basic statistics
        analytics.setTotalAttempts(attempts.size());
        
        double totalScore = attempts.stream().mapToDouble(ExamAttempt::getScore).sum();
        analytics.setAverageScore(totalScore / attempts.size());
        
        int totalCorrect = attempts.stream().mapToInt(ExamAttempt::getCorrectAnswers).sum();
        int totalQuestions = attempts.stream().mapToInt(ExamAttempt::getTotalQuestions).sum();
        
        analytics.setTotalCorrectAnswers(totalCorrect);
        analytics.setTotalQuestions(totalQuestions);
        analytics.setOverallAccuracy(totalQuestions > 0 ? ((double) totalCorrect / totalQuestions) * 100 : 0);

        // Topic-wise performance aggregation
        Map<String, TopicPerformance> aggregatedTopics = aggregateTopicPerformance(attempts);
        analytics.setTopicWisePerformance(aggregatedTopics);

        // Strong and weak topics
        analytics.setStrongTopics(identifyStrongTopics(aggregatedTopics));
        analytics.setWeakTopics(identifyWeakTopics(aggregatedTopics));

        // Course-wise scores
        Map<String, Double> courseScores = attempts.stream()
            .collect(Collectors.groupingBy(ExamAttempt::getCourseName,
                Collectors.averagingDouble(ExamAttempt::getScore)));
        analytics.setCourseWiseScores(courseScores);

        // Progress data
        analytics.setProgressData(calculateProgressData(attempts));

        // AI insights
        analytics.setAiInsights(generateUserInsights(analytics, attempts));

        return analytics;
    }

    public CourseHistory getCourseHistory(Long userId, String courseName) {
        List<ExamAttempt> courseAttempts = examAttemptRepository
            .findByUserIdAndCourseNameOrderByAttemptedAtDesc(userId, courseName);

        CourseHistory history = new CourseHistory();
        history.setCourseName(courseName);
        history.setAttempts(courseAttempts);

        if (!courseAttempts.isEmpty()) {
            history.setScoreProgression(calculateScoreProgression(courseAttempts));
            history.setTopicAnalysis(calculateTopicAnalysis(courseAttempts));
            history.setTimeAnalysis(calculateTimeAnalysis(courseAttempts));
            history.setImprovementSuggestions(generateImprovementSuggestions(courseAttempts));
        }

        return history;
    }

    public DetailedAttemptAnalysis getDetailedAnalysis(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new RuntimeException("Attempt not found"));

        DetailedAttemptAnalysis analysis = new DetailedAttemptAnalysis();
        analysis.setAttempt(attempt);
        
        // Get question analyses
        analysis.setQuestionAnalyses(generateQuestionAnalyses(attempt));
        
        // Topic breakdown
        analysis.setTopicBreakdown(attempt.getTopicPerformance());
        
        // Time distribution analysis
        analysis.setTimeDistribution(generateTimeDistribution(attempt));
        
        // Comparison with other users
        analysis.setComparisonData(generateComparisonData(attempt));
        
        // AI analysis
        analysis.setAiAnalysis(generateDetailedAIAnalysis(attempt));
        
        // Key insights
        analysis.setKeyInsights(extractKeyInsights(attempt));

        return analysis;
    }

    public PerformanceTrends getPerformanceTrends(Long userId, int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        List<ExamAttempt> recentAttempts = examAttemptRepository
            .findByUserIdAndAttemptedAtAfterOrderByAttemptedAtAsc(userId, fromDate.toString());

        PerformanceTrends trends = new PerformanceTrends();
        
        // Score trends
        trends.setScoreOverTime(recentAttempts.stream()
            .map(attempt -> new TrendPoint(
                LocalDateTime.parse(attempt.getAttemptedAt()),
                attempt.getScore(),
                attempt.getCourseName()))
            .collect(Collectors.toList()));

        // Accuracy trends
        trends.setAccuracyOverTime(recentAttempts.stream()
            .map(attempt -> new TrendPoint(
                LocalDateTime.parse(attempt.getAttemptedAt()),
                ((double) attempt.getCorrectAnswers() / attempt.getTotalQuestions()) * 100,
                attempt.getCourseName()))
            .collect(Collectors.toList()));

        // Time efficiency trends
        trends.setTimeEfficiencyOverTime(recentAttempts.stream()
            .map(attempt -> new TrendPoint(
                LocalDateTime.parse(attempt.getAttemptedAt()),
                calculateTimeEfficiency(attempt),
                attempt.getCourseName()))
            .collect(Collectors.toList()));

        // Topic trends
        trends.setTopicTrends(calculateTopicTrends(recentAttempts));
        
        // Trend analysis
        trends.setTrendAnalysis(analyzeTrends(trends));

        return trends;
    }

    public List<LeaderboardEntry> getCourseLeaderboard(String courseName, int limit) {
        List<ExamAttempt> attempts = examAttemptRepository.findByCourseName(courseName);
        
        return attempts.stream()
            .collect(Collectors.groupingBy(ExamAttempt::getUserId,
                Collectors.maxBy(Comparator.comparing(ExamAttempt::getScore))))
            .entrySet().stream()
            .filter(entry -> entry.getValue().isPresent())
            .map(entry -> {
                ExamAttempt bestAttempt = entry.getValue().get();
                long totalAttempts = attempts.stream()
                    .filter(a -> a.getUserId() == entry.getKey())
                    .count();
                
                LeaderboardEntry leaderEntry = new LeaderboardEntry();
                leaderEntry.setUserId(entry.getKey());
                leaderEntry.setUsername("User" + entry.getKey()); // You might want to fetch actual username
                leaderEntry.setBestScore(bestAttempt.getScore());
                leaderEntry.setTotalAttempts((int) totalAttempts);
                leaderEntry.setLastAttempt(LocalDateTime.parse(bestAttempt.getAttemptedAt()));
                return leaderEntry;
            })
            .sorted((a, b) -> Double.compare(b.getBestScore(), a.getBestScore()))
            .limit(limit)
            .peek(entry -> entry.setRank(0)) // Set ranks after sorting
            .collect(Collectors.toList());
    }

    // Helper methods
    private Map<String, TopicPerformance> aggregateTopicPerformance(List<ExamAttempt> attempts) {
        Map<String, TopicPerformance> aggregated = new HashMap<>();
        
        for (ExamAttempt attempt : attempts) {
            if (attempt.getTopicPerformance() != null) {
                for (Map.Entry<String, TopicPerformance> entry : attempt.getTopicPerformance().entrySet()) {
                    String topic = entry.getKey();
                    TopicPerformance performance = entry.getValue();
                    
                    TopicPerformance existing = aggregated.getOrDefault(topic, 
                        new TopicPerformance(topic, 0, 0, 0));
                    
                    existing.setTotalQuestions(existing.getTotalQuestions() + performance.getTotalQuestions());
                    existing.setCorrectAnswers(existing.getCorrectAnswers() + performance.getCorrectAnswers());
                    existing.setIncorrectAnswers(existing.getIncorrectAnswers() + performance.getIncorrectAnswers());
                    existing.setAccuracy(((double) existing.getCorrectAnswers() / existing.getTotalQuestions()) * 100);
                    
                    aggregated.put(topic, existing);
                }
            }
        }
        
        return aggregated;
    }

    private List<String> identifyStrongTopics(Map<String, TopicPerformance> topics) {
        return topics.values().stream()
            .filter(tp -> tp.getAccuracy() >= 80)
            .map(TopicPerformance::getTopicName)
            .collect(Collectors.toList());
    }

    private List<String> identifyWeakTopics(Map<String, TopicPerformance> topics) {
        return topics.values().stream()
            .filter(tp -> tp.getAccuracy() < 60)
            .map(TopicPerformance::getTopicName)
            .collect(Collectors.toList());
    }

    private ProgressData calculateProgressData(List<ExamAttempt> attempts) {
        ProgressData progress = new ProgressData();
        
        // Sort attempts by date
        attempts.sort(Comparator.comparing(attempt -> LocalDateTime.parse(attempt.getAttemptedAt())));
        
        // Score progression
        List<TrendPoint> scoreProgression = attempts.stream()
            .map(attempt -> new TrendPoint(
                LocalDateTime.parse(attempt.getAttemptedAt()),
                attempt.getScore(),
                "Score"))
            .collect(Collectors.toList());
        progress.setScoreProgression(scoreProgression);

        // Accuracy progression
        List<TrendPoint> accuracyProgression = attempts.stream()
            .map(attempt -> new TrendPoint(
                LocalDateTime.parse(attempt.getAttemptedAt()),
                ((double) attempt.getCorrectAnswers() / attempt.getTotalQuestions()) * 100,
                "Accuracy"))
            .collect(Collectors.toList());
        progress.setAccuracyProgression(accuracyProgression);

        // Time progression
        List<TrendPoint> timeProgression = attempts.stream()
            .map(attempt -> new TrendPoint(
                LocalDateTime.parse(attempt.getAttemptedAt()),
                attempt.getTimeTaken(),
                "Time"))
            .collect(Collectors.toList());
        progress.setTimeProgression(timeProgression);

        // Calculate improvement rate
        if (attempts.size() >= 2) {
            double firstScore = attempts.get(0).getScore();
            double lastScore = attempts.get(attempts.size() - 1).getScore();
            progress.setImprovementRate(lastScore - firstScore);
            
            if (progress.getImprovementRate() > 5) {
                progress.setProgressStatus("improving");
            } else if (progress.getImprovementRate() < -5) {
                progress.setProgressStatus("declining");
            } else {
                progress.setProgressStatus("stable");
            }
        } else {
            progress.setProgressStatus("insufficient_data");
        }

        // Calculate active days and streak
        Set<LocalDate> activeDates = attempts.stream()
            .map(attempt -> LocalDateTime.parse(attempt.getAttemptedAt()).toLocalDate())
            .collect(Collectors.toSet());
        progress.setTotalDaysActive(activeDates.size());
        progress.setStreakDays(calculateStreakDays(activeDates));

        return progress;
    }

    private int calculateStreakDays(Set<LocalDate> activeDates) {
        if (activeDates.isEmpty()) return 0;
        
        List<LocalDate> sortedDates = activeDates.stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
            
        int streak = 1;
        for (int i = 1; i < sortedDates.size(); i++) {
            if (sortedDates.get(i).equals(sortedDates.get(i-1).minusDays(1))) {
                streak++;
            } else {
                break;
            }
        }
        
        return streak;
    }

    private ScoreProgression calculateScoreProgression(List<ExamAttempt> attempts) {
        ScoreProgression progression = new ScoreProgression();
        
        List<Double> scores = attempts.stream()
            .map(ExamAttempt::getScore)
            .collect(Collectors.toList());
        progression.setScores(scores);
        
        progression.setBestScore(scores.stream().mapToDouble(Double::doubleValue).max().orElse(0));
        progression.setAverageScore(scores.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        progression.setAttemptCount(attempts.size());
        
        // Calculate trend (simple linear regression slope)
        if (scores.size() >= 2) {
            double trend = calculateTrend(scores);
            progression.setTrend(trend);
        }
        
        return progression;
    }

    private double calculateTrend(List<Double> scores) {
        int n = scores.size();
        double sumX = n * (n + 1) / 2.0; // Sum of indices (1, 2, 3, ...)
        double sumY = scores.stream().mapToDouble(Double::doubleValue).sum();
        double sumXY = 0;
        double sumXX = n * (n + 1) * (2 * n + 1) / 6.0; // Sum of squares of indices
        
        for (int i = 0; i < n; i++) {
            sumXY += (i + 1) * scores.get(i);
        }
        
        return (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
    }

    private TopicAnalysis calculateTopicAnalysis(List<ExamAttempt> attempts) {
        TopicAnalysis analysis = new TopicAnalysis();
        
        Map<String, TopicPerformance> aggregated = aggregateTopicPerformance(attempts);
        Map<String, Double> topicAccuracies = aggregated.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getAccuracy()));
        analysis.setTopicAccuracies(topicAccuracies);
        
        // Find strongest and weakest topics
        Optional<Map.Entry<String, Double>> strongest = topicAccuracies.entrySet().stream()
            .max(Map.Entry.comparingByValue());
        Optional<Map.Entry<String, Double>> weakest = topicAccuracies.entrySet().stream()
            .min(Map.Entry.comparingByValue());
            
        analysis.setStrongestTopic(strongest.map(Map.Entry::getKey).orElse(null));
        analysis.setWeakestTopic(weakest.map(Map.Entry::getKey).orElse(null));
        
        // Calculate improving/declining topics (requires historical data analysis)
        analysis.setImprovingTopics(calculateImprovingTopics(attempts));
        analysis.setDecliningTopics(calculateDecliningTopics(attempts));
        
        return analysis;
    }

    private List<String> calculateImprovingTopics(List<ExamAttempt> attempts) {
        // This would require more complex analysis comparing recent vs older attempts
        // For now, return empty list - you can enhance this based on your needs
        return new ArrayList<>();
    }

    private List<String> calculateDecliningTopics(List<ExamAttempt> attempts) {
        // Similar to improving topics, requires temporal analysis
        return new ArrayList<>();
    }

    private TimeAnalysis calculateTimeAnalysis(List<ExamAttempt> attempts) {
        if (attempts.isEmpty()) return new TimeAnalysis();
        
        double avgTimePerQuestion = attempts.stream()
            .mapToDouble(attempt -> (double) attempt.getTimeTaken() / attempt.getTotalQuestions())
            .average()
            .orElse(0);
            
        TimeAnalysis analysis = new TimeAnalysis();
        analysis.setAverageTimePerQuestion(avgTimePerQuestion);
        
        // Calculate time efficiency score
        double idealTime = 120; // 2 minutes per question ideal
        double efficiency = Math.max(0, 100 - Math.abs((avgTimePerQuestion - idealTime) / idealTime) * 100);
        analysis.setTimeEfficiencyScore(Math.min(100, efficiency));
        
        // For now, return basic analysis
        // You can enhance this with topic-specific time analysis
        analysis.setFastAnsweredTopics(new ArrayList<>());
        analysis.setSlowAnsweredTopics(new ArrayList<>());
        
        return analysis;
    }

    private String generateImprovementSuggestions(List<ExamAttempt> attempts) {
        if (attempts.isEmpty()) return "No attempts to analyze yet.";
        
        ExamAttempt latest = attempts.get(0); // Assuming sorted by date desc
        StringBuilder suggestions = new StringBuilder();
        
        suggestions.append("Based on your recent performance:\n\n");
        
        if (latest.getScore() >= 80) {
            suggestions.append("✅ Great job! You're performing well. ");
        } else {
            suggestions.append("📈 Focus on improving your score. ");
        }
        
        if (latest.getSkipped() > 0) {
            suggestions.append("\n⏰ Work on time management - you skipped ")
                      .append(latest.getSkipped()).append(" questions.");
        }
        
        if (latest.getWeakTopics() != null && !latest.getWeakTopics().isEmpty()) {
            suggestions.append("\n📚 Focus on these weak areas: ")
                      .append(String.join(", ", latest.getWeakTopics()));
        }
        
        // Add more personalized suggestions based on patterns
        if (attempts.size() >= 3) {
            double avgScore = attempts.stream().mapToDouble(ExamAttempt::getScore).average().orElse(0);
            if (latest.getScore() < avgScore - 5) {
                suggestions.append("\n📉 Your last score was below your average. Review your preparation strategy.");
            }
        }
        
        return suggestions.toString();
    }

    private List<QuestionAnalysis> generateQuestionAnalyses(ExamAttempt attempt) {
        List<QuestionAnalysis> analyses = new ArrayList<>();
        
        if (attempt.getDetailedResults() != null) {
            for (QuestionResult result : attempt.getDetailedResults()) {
                QuestionAnalysis analysis = new QuestionAnalysis();
                analysis.setQuestionId(result.getQuestionId());
                analysis.setTopic(result.getTopic());
                analysis.setDifficulty(result.getDifficulty() != null ? result.getDifficulty() : "medium");
                analysis.setUserCorrect(result.isCorrect());
                analysis.setUserAnswer(result.getUserAnswer());
                analysis.setCorrectAnswer(result.getCorrectAnswer());
                analysis.setExplanation(result.getExplanation());
                analysis.setTimeSpent(result.getTimeSpent());
                
                // Generate improvement tip
                if (!result.isCorrect()) {
                    analysis.setImprovementTip("Review " + result.getTopic() + 
                        " concepts and practice similar questions.");
                } else {
                    analysis.setImprovementTip("Well done! Continue practicing to maintain this level.");
                }
                
                analyses.add(analysis);
            }
        }
        
        return analyses;
    }

    private TimeDistribution generateTimeDistribution(ExamAttempt attempt) {
        TimeDistribution distribution = new TimeDistribution();
        
        distribution.setTotalTimeSpent(attempt.getTimeTaken());
        distribution.setAverageTimePerQuestion(
            (double) attempt.getTimeTaken() / attempt.getTotalQuestions());
        
        // Calculate time efficiency score
        double idealTime = attempt.getTotalQuestions() * 120; // 2 min per question
        double efficiency = Math.max(0, 100 - Math.abs((attempt.getTimeTaken() - idealTime) / idealTime) * 100);
        distribution.setTimeEfficiencyScore(Math.min(100, efficiency));
        
        // Generate time management tip
        if (attempt.getTimeTaken() > idealTime * 1.2) {
            distribution.setTimeManagementTip("Consider spending less time per question to complete all questions.");
        } else if (attempt.getTimeTaken() < idealTime * 0.8) {
            distribution.setTimeManagementTip("You finished quickly. Consider reviewing your answers for accuracy.");
        } else {
            distribution.setTimeManagementTip("Good time management! Maintain this pace.");
        }
        
        // Initialize other fields with empty collections for now
        distribution.setTimeByTopic(new HashMap<>());
        distribution.setTimeByDifficulty(new HashMap<>());
        distribution.setFastestTopics(new ArrayList<>());
        distribution.setSlowestTopics(new ArrayList<>());
        
        return distribution;
    }

    private ComparisonData generateComparisonData(ExamAttempt attempt) {
        ComparisonData comparison = new ComparisonData();
        
        // Get all attempts for the same course
        List<ExamAttempt> courseAttempts = examAttemptRepository.findByCourseName(attempt.getCourseName());
        
        if (!courseAttempts.isEmpty()) {
            double avgScore = courseAttempts.stream()
                .mapToDouble(ExamAttempt::getScore)
                .average()
                .orElse(0);
            comparison.setAverageScoreForCourse(avgScore);
            
            double avgTime = courseAttempts.stream()
                .mapToDouble(ExamAttempt::getTimeTaken)
                .average()
                .orElse(0);
            comparison.setAverageTimeForCourse(avgTime);
            
            // Calculate percentiles
            long betterScores = courseAttempts.stream()
                .mapToDouble(ExamAttempt::getScore)
                .filter(score -> score < attempt.getScore())
                .count();
            comparison.setUserScorePercentile(((double) betterScores / courseAttempts.size()) * 100);
            
            // Generate performance ranking
            if (comparison.getUserScorePercentile() >= 90) {
                comparison.setPerformanceRanking("Top 10%");
            } else if (comparison.getUserScorePercentile() >= 75) {
                comparison.setPerformanceRanking("Top 25%");
            } else if (comparison.getUserScorePercentile() >= 50) {
                comparison.setPerformanceRanking("Above Average");
            } else {
                comparison.setPerformanceRanking("Below Average");
            }
            
            // Generate comparison summary
            comparison.setComparisonSummary(
                String.format("You scored %.1f%% vs course average of %.1f%%. " +
                    "You're in the %s of all test takers.",
                    attempt.getScore(), avgScore, comparison.getPerformanceRanking()));
        }
        
        // Initialize other fields
        comparison.setTopicComparisonWithAverage(new HashMap<>());
        comparison.setBetterThanAverageTopics(new ArrayList<>());
        comparison.setBelowAverageTopics(new ArrayList<>());
        
        return comparison;
    }

    private String generateDetailedAIAnalysis(ExamAttempt attempt) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("🔍 Detailed Performance Analysis\n\n");
        
        // Overall performance
        analysis.append(String.format("📊 Overall Score: %.1f%% (%s)\n",
            attempt.getScore(), attempt.isPassed() ? "PASSED" : "NEEDS IMPROVEMENT"));
        
        // Time analysis
        double avgTimePerQ = (double) attempt.getTimeTaken() / attempt.getTotalQuestions();
        analysis.append(String.format("⏱️ Time Management: %.1f seconds per question\n", avgTimePerQ));
        
        // Accuracy analysis
        double accuracy = ((double) attempt.getCorrectAnswers() / attempt.getTotalQuestions()) * 100;
        analysis.append(String.format("🎯 Accuracy: %.1f%% (%d/%d correct)\n", 
            accuracy, attempt.getCorrectAnswers(), attempt.getTotalQuestions()));
        
        // Topic analysis
        if (attempt.getTopicPerformance() != null && !attempt.getTopicPerformance().isEmpty()) {
            analysis.append("\n📚 Topic-wise Performance:\n");
            for (Map.Entry<String, TopicPerformance> entry : attempt.getTopicPerformance().entrySet()) {
                TopicPerformance tp = entry.getValue();
                analysis.append(String.format("   • %s: %.1f%% (%d/%d)\n",
                    entry.getKey(), tp.getAccuracy(), tp.getCorrectAnswers(), tp.getTotalQuestions()));
            }
        }
        
        // AI recommendations
        analysis.append("\n🤖 AI Recommendations:\n");
        if (attempt.getScore() < 60) {
            analysis.append("   • Focus on fundamental concepts\n");
            analysis.append("   • Take more practice tests\n");
            analysis.append("   • Consider getting additional help\n");
        } else if (attempt.getScore() < 80) {
            analysis.append("   • Review incorrect answers carefully\n");
            analysis.append("   • Practice weak topic areas\n");
            analysis.append("   • Improve time management\n");
        } else {
            analysis.append("   • Excellent work! Maintain consistency\n");
            analysis.append("   • Challenge yourself with harder questions\n");
            analysis.append("   • Share your study methods with others\n");
        }
        
        return analysis.toString();
    }

    private List<String> extractKeyInsights(ExamAttempt attempt) {
        List<String> insights = new ArrayList<>();
        
        // Performance insights
        if (attempt.getScore() >= 90) {
            insights.add("🌟 Exceptional performance - you're in the top tier!");
        } else if (attempt.getScore() >= 80) {
            insights.add("✅ Strong performance with passing grade achieved");
        } else {
            insights.add("📈 Room for improvement - focus on weak areas");
        }
        
        // Time management insights
        double avgTime = (double) attempt.getTimeTaken() / attempt.getTotalQuestions();
        if (avgTime < 90) {
            insights.add("⚡ Fast completion - ensure accuracy wasn't compromised");
        } else if (avgTime > 180) {
            insights.add("🐌 Consider improving time management skills");
        } else {
            insights.add("⏰ Good time management balance");
        }
        
        // Completion insights
        if (attempt.getSkipped() == 0) {
            insights.add("💯 Great job completing all questions!");
        } else if (attempt.getSkipped() > attempt.getTotalQuestions() * 0.2) {
            insights.add("⚠️ High number of skipped questions - work on time management");
        }
        
        // Accuracy insights
        double accuracy = ((double) attempt.getCorrectAnswers() / (attempt.getCorrectAnswers() + attempt.getIncorrectAnswers())) * 100;
        if (accuracy >= 85) {
            insights.add("🎯 High accuracy rate - good knowledge retention");
        } else if (accuracy < 70) {
            insights.add("🎲 Lower accuracy suggests need for more practice");
        }
        
        return insights;
    }

    private String generateUserInsights(UserAnalytics analytics, List<ExamAttempt> attempts) {
        StringBuilder insights = new StringBuilder();
        
        insights.append("📊 Your Learning Journey Summary:\n\n");
        
        // Overall progress
        insights.append(String.format("🎯 Overall Performance: %.1f%% average score across %d attempts\n",
            analytics.getAverageScore(), analytics.getTotalAttempts()));
        
        // Strengths
        if (!analytics.getStrongTopics().isEmpty()) {
            insights.append("💪 Your Strengths: " + String.join(", ", analytics.getStrongTopics()) + "\n");
        }
        
        // Areas for improvement
        if (!analytics.getWeakTopics().isEmpty()) {
            insights.append("📚 Focus Areas: " + String.join(", ", analytics.getWeakTopics()) + "\n");
        }
        
        // Progress status
        if (analytics.getProgressData() != null) {
            String status = analytics.getProgressData().getProgressStatus();
            if ("improving".equals(status)) {
                insights.append("📈 Great news! Your scores are improving over time\n");
            } else if ("declining".equals(status)) {
                insights.append("📉 Consider reviewing your study approach - scores have declined recently\n");
            } else {
                insights.append("📊 Your performance has been consistent\n");
            }
        }
        
        // Recommendations
        insights.append("\n💡 Personalized Recommendations:\n");
        if (analytics.getAverageScore() < 70) {
            insights.append("   • Focus on building strong foundations\n");
            insights.append("   • Take more practice tests\n");
            insights.append("   • Consider structured learning approach\n");
        } else if (analytics.getAverageScore() < 85) {
            insights.append("   • Target your weak topics for improvement\n");
            insights.append("   • Practice time management\n");
            insights.append("   • Review incorrect answers thoroughly\n");
        } else {
            insights.append("   • Excellent work! Maintain your study routine\n");
            insights.append("   • Consider helping others learn\n");
            insights.append("   • Challenge yourself with advanced topics\n");
        }
        
        return insights.toString();
    }

    private double calculateTimeEfficiency(ExamAttempt attempt) {
        double idealTime = attempt.getTotalQuestions() * 120; // 2 minutes per question
        return Math.max(0, 100 - Math.abs((attempt.getTimeTaken() - idealTime) / idealTime) * 100);
    }

    private Map<String, List<TrendPoint>> calculateTopicTrends(List<ExamAttempt> attempts) {
        Map<String, List<TrendPoint>> topicTrends = new HashMap<>();
        
        // Group attempts by topics and create trend points
        for (ExamAttempt attempt : attempts) {
            if (attempt.getTopicPerformance() != null) {
                for (Map.Entry<String, TopicPerformance> entry : attempt.getTopicPerformance().entrySet()) {
                    String topic = entry.getKey();
                    TopicPerformance performance = entry.getValue();
                    
                    topicTrends.computeIfAbsent(topic, k -> new ArrayList<>())
                        .add(new TrendPoint(
                            LocalDateTime.parse(attempt.getAttemptedAt()),
                            performance.getAccuracy(),
                            topic));
                }
            }
        }
        
        return topicTrends;
    }

    private String analyzeTrends(PerformanceTrends trends) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("📈 Trend Analysis:\n\n");
        
        // Analyze score trend
        if (trends.getScoreOverTime().size() >= 2) {
            List<TrendPoint> scores = trends.getScoreOverTime();
            double firstScore = scores.get(0).getValue();
            double lastScore = scores.get(scores.size() - 1).getValue();
            double improvement = lastScore - firstScore;
            
            if (improvement > 5) {
                analysis.append("🚀 Your scores are improving! Keep up the great work.\n");
            } else if (improvement < -5) {
                analysis.append("⚠️ Your scores have declined recently. Consider reviewing your study approach.\n");
            } else {
                analysis.append("📊 Your performance has been stable.\n");
            }
        }
        
        // Analyze time efficiency trend
        if (trends.getTimeEfficiencyOverTime().size() >= 2) {
            List<TrendPoint> timeEfficiency = trends.getTimeEfficiencyOverTime();
            double firstEff = timeEfficiency.get(0).getValue();
            double lastEff = timeEfficiency.get(timeEfficiency.size() - 1).getValue();
            
            if (lastEff > firstEff + 10) {
                analysis.append("⏰ Your time management is improving!\n");
            } else if (lastEff < firstEff - 10) {
                analysis.append("🐌 Focus on improving your time management.\n");
            }
        }
        
        return analysis.toString();
    }
}