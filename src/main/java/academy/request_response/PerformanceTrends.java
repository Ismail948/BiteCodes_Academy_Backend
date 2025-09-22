package academy.request_response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import academy.models.ExamAttempt;
import academy.models.TopicPerformance;

//PerformanceTrends class - tracks performance changes over time
public class PerformanceTrends {
 private List<TrendPoint> scoreOverTime;
 private List<TrendPoint> accuracyOverTime;
 private List<TrendPoint> timeEfficiencyOverTime;
 private Map<String, List<TrendPoint>> topicTrends;
 private String trendAnalysis;
 
 // Additional trend metrics
 private double overallTrend; // Positive = improving, Negative = declining
 private String trendDirection; // "improving", "stable", "declining"
 private double trendConfidence; // 0-100% confidence in trend analysis
 private int dataPoints; // Number of attempts analyzed
 private LocalDateTime analysisDate;
 private String timeframe; // "7_days", "30_days", "90_days", "all_time"
 
 // Performance velocity metrics
 private double scoreVelocity; // Points per attempt improvement
 private double accuracyVelocity; // Accuracy percentage improvement per attempt
 private double consistencyScore; // How consistent performance is (0-100)
 
 // Streak information
 private int improvingStreak; // Consecutive improving attempts
 private int decliningStreak; // Consecutive declining attempts
 private int bestStreak; // Best improvement streak recorded
 private int currentStreak; // Current streak (positive = improving, negative = declining)

 // Constructors
 public PerformanceTrends() {
     this.scoreOverTime = new ArrayList<>();
     this.accuracyOverTime = new ArrayList<>();
     this.timeEfficiencyOverTime = new ArrayList<>();
     this.topicTrends = new HashMap<>();
     this.analysisDate = LocalDateTime.now();
 }

 public PerformanceTrends(List<ExamAttempt> attempts, String timeframe) {
     this();
     this.timeframe = timeframe;
     this.dataPoints = attempts.size();
     calculateTrends(attempts);
 }

 // Core trend calculation method
 private void calculateTrends(List<ExamAttempt> attempts) {
     if (attempts == null || attempts.isEmpty()) {
         this.trendAnalysis = "No data available for trend analysis";
         return;
     }

     // Sort attempts by date
     attempts.sort(Comparator.comparing(attempt -> 
         LocalDateTime.parse(attempt.getAttemptedAt())));

     // Calculate score trends
     calculateScoreTrends(attempts);
     
     // Calculate accuracy trends
     calculateAccuracyTrends(attempts);
     
     // Calculate time efficiency trends
     calculateTimeEfficiencyTrends(attempts);
     
     // Calculate topic-specific trends
     calculateTopicTrends(attempts);
     
     // Calculate velocity and consistency metrics
     calculateVelocityMetrics(attempts);
     
     // Calculate streak information
     calculateStreaks(attempts);
     
     // Generate overall trend analysis
     generateTrendAnalysis();
 }

 private void calculateScoreTrends(List<ExamAttempt> attempts) {
     scoreOverTime = attempts.stream()
         .map(attempt -> new TrendPoint(
             LocalDateTime.parse(attempt.getAttemptedAt()),
             attempt.getScore(),
             String.format("Score: %.1f%%", attempt.getScore())))
         .collect(Collectors.toList());
     
     // Calculate overall score trend using linear regression
     overallTrend = calculateLinearTrend(
         scoreOverTime.stream().mapToDouble(TrendPoint::getValue).toArray());
 }

 private void calculateAccuracyTrends(List<ExamAttempt> attempts) {
     accuracyOverTime = attempts.stream()
         .map(attempt -> {
             double accuracy = attempt.getTotalQuestions() > 0 
                 ? ((double) attempt.getCorrectAnswers() / attempt.getTotalQuestions()) * 100
                 : 0.0;
             return new TrendPoint(
                 LocalDateTime.parse(attempt.getAttemptedAt()),
                 accuracy,
                 String.format("Accuracy: %.1f%%", accuracy));
         })
         .collect(Collectors.toList());
 }

 private void calculateTimeEfficiencyTrends(List<ExamAttempt> attempts) {
     timeEfficiencyOverTime = attempts.stream()
         .map(attempt -> {
             // Calculate time efficiency (lower time with maintained accuracy is better)
             double avgTimePerQuestion = attempt.getTotalQuestions() > 0 
                 ? (double) attempt.getTimeTaken() / attempt.getTotalQuestions() 
                 : 0.0;
             double idealTime = 120.0; // 2 minutes per question ideal
             double efficiency = Math.max(0, 100 - Math.abs((avgTimePerQuestion - idealTime) / idealTime) * 100);
             
             return new TrendPoint(
                 LocalDateTime.parse(attempt.getAttemptedAt()),
                 efficiency,
                 String.format("Time Efficiency: %.1f%%", efficiency));
         })
         .collect(Collectors.toList());
 }

 private void calculateTopicTrends(List<ExamAttempt> attempts) {
     topicTrends = new HashMap<>();
     
     // Group performance by topics across all attempts
     Map<String, List<TrendPoint>> topicData = new HashMap<>();
     
     for (ExamAttempt attempt : attempts) {
         if (attempt.getTopicPerformance() != null) {
             LocalDateTime attemptDate = LocalDateTime.parse(attempt.getAttemptedAt());
             
             for (Map.Entry<String, TopicPerformance> entry : attempt.getTopicPerformance().entrySet()) {
                 String topic = entry.getKey();
                 TopicPerformance performance = entry.getValue();
                 
                 topicData.computeIfAbsent(topic, k -> new ArrayList<>())
                     .add(new TrendPoint(
                         attemptDate,
                         performance.getAccuracy(),
                         String.format("%s: %.1f%%", topic, performance.getAccuracy())));
             }
         }
     }
     
     this.topicTrends = topicData;
 }

 private void calculateVelocityMetrics(List<ExamAttempt> attempts) {
     if (attempts.size() < 2) {
         this.scoreVelocity = 0.0;
         this.accuracyVelocity = 0.0;
         this.consistencyScore = 100.0; // Perfect consistency with single data point
         return;
     }

     // Calculate score velocity (improvement per attempt)
     double firstScore = attempts.get(0).getScore();
     double lastScore = attempts.get(attempts.size() - 1).getScore();
     this.scoreVelocity = (lastScore - firstScore) / (attempts.size() - 1);

     // Calculate accuracy velocity
     double firstAccuracy = ((double) attempts.get(0).getCorrectAnswers() / attempts.get(0).getTotalQuestions()) * 100;
     double lastAccuracy = ((double) attempts.get(attempts.size() - 1).getCorrectAnswers() / attempts.get(attempts.size() - 1).getTotalQuestions()) * 100;
     this.accuracyVelocity = (lastAccuracy - firstAccuracy) / (attempts.size() - 1);

     // Calculate consistency score (lower standard deviation = higher consistency)
     double[] scores = attempts.stream().mapToDouble(ExamAttempt::getScore).toArray();
     double mean = Arrays.stream(scores).average().orElse(0.0);
     double variance = Arrays.stream(scores)
         .map(score -> Math.pow(score - mean, 2))
         .average()
         .orElse(0.0);
     double stdDev = Math.sqrt(variance);
     
     // Convert to consistency score (0-100, where 100 is perfectly consistent)
     this.consistencyScore = Math.max(0, 100 - (stdDev * 2)); // Normalize standard deviation
 }

 private void calculateStreaks(List<ExamAttempt> attempts) {
     if (attempts.size() < 2) {
         this.currentStreak = 0;
         this.improvingStreak = 0;
         this.decliningStreak = 0;
         this.bestStreak = 0;
         return;
     }

     int tempImprovingStreak = 0;
     int tempDecliningStreak = 0;
     int maxImprovingStreak = 0;
     int maxDecliningStreak = 0;
     int currentStreakValue = 0;

     for (int i = 1; i < attempts.size(); i++) {
         double currentScore = attempts.get(i).getScore();
         double previousScore = attempts.get(i - 1).getScore();

         if (currentScore > previousScore) {
             // Improving
             tempImprovingStreak++;
             tempDecliningStreak = 0;
             currentStreakValue = tempImprovingStreak;
             maxImprovingStreak = Math.max(maxImprovingStreak, tempImprovingStreak);
         } else if (currentScore < previousScore) {
             // Declining
             tempDecliningStreak++;
             tempImprovingStreak = 0;
             currentStreakValue = -tempDecliningStreak;
             maxDecliningStreak = Math.max(maxDecliningStreak, tempDecliningStreak);
         } else {
             // Same score - break streak
             tempImprovingStreak = 0;
             tempDecliningStreak = 0;
             currentStreakValue = 0;
         }
     }

     this.improvingStreak = tempImprovingStreak;
     this.decliningStreak = tempDecliningStreak;
     this.bestStreak = maxImprovingStreak;
     this.currentStreak = currentStreakValue;
 }

 private void generateTrendAnalysis() {
     StringBuilder analysis = new StringBuilder();
     
     // Overall trend direction
     if (Math.abs(overallTrend) < 1.0) {
         trendDirection = "stable";
         analysis.append("📊 Your performance has been stable over time. ");
         trendConfidence = 70.0;
     } else if (overallTrend > 0) {
         trendDirection = "improving";
         analysis.append("📈 Great news! Your performance is improving over time. ");
         trendConfidence = Math.min(95.0, 60.0 + Math.abs(overallTrend) * 5);
     } else {
         trendDirection = "declining";
         analysis.append("📉 Your performance has been declining recently. ");
         trendConfidence = Math.min(95.0, 60.0 + Math.abs(overallTrend) * 5);
     }

     // Add velocity information
     if (Math.abs(scoreVelocity) > 0.5) {
         analysis.append(String.format("You're %s by an average of %.1f points per attempt. ",
             scoreVelocity > 0 ? "improving" : "declining", Math.abs(scoreVelocity)));
     }

     // Add consistency information
     if (consistencyScore > 80) {
         analysis.append("Your performance is very consistent. ");
     } else if (consistencyScore > 60) {
         analysis.append("Your performance shows moderate consistency. ");
     } else {
         analysis.append("Your performance varies significantly between attempts. ");
     }

     // Add streak information
     if (currentStreak > 2) {
         analysis.append(String.format("You're currently on a %d-attempt improvement streak! ", currentStreak));
     } else if (currentStreak < -2) {
         analysis.append(String.format("You've had %d consecutive declining attempts. ", Math.abs(currentStreak)));
     }

     // Add recommendations based on trends
     analysis.append("\n\n💡 Recommendations:\n");
     if ("improving".equals(trendDirection)) {
         analysis.append("• Keep up your current study approach - it's working!\n");
         analysis.append("• Consider setting more challenging goals\n");
         if (consistencyScore < 70) {
             analysis.append("• Focus on maintaining consistent performance\n");
         }
     } else if ("declining".equals(trendDirection)) {
         analysis.append("• Review and adjust your study strategy\n");
         analysis.append("• Consider seeking additional help or resources\n");
         analysis.append("• Focus on understanding concepts rather than memorization\n");
     } else {
         analysis.append("• Try varying your study methods to see improvement\n");
         analysis.append("• Set specific goals for your next attempts\n");
     }

     this.trendAnalysis = analysis.toString();
 }

 // Utility method for linear regression trend calculation
 private double calculateLinearTrend(double[] values) {
     if (values.length < 2) return 0.0;

     int n = values.length;
     double sumX = n * (n + 1) / 2.0; // Sum of indices (1, 2, 3, ...)
     double sumY = Arrays.stream(values).sum();
     double sumXY = 0.0;
     double sumXX = n * (n + 1) * (2 * n + 1) / 6.0; // Sum of squares of indices

     for (int i = 0; i < n; i++) {
         sumXY += (i + 1) * values[i];
     }

     // Calculate slope of trend line
     return (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
 }

 // Getters and Setters
 public List<TrendPoint> getScoreOverTime() { return scoreOverTime; }
 public void setScoreOverTime(List<TrendPoint> scoreOverTime) { this.scoreOverTime = scoreOverTime; }

 public List<TrendPoint> getAccuracyOverTime() { return accuracyOverTime; }
 public void setAccuracyOverTime(List<TrendPoint> accuracyOverTime) { this.accuracyOverTime = accuracyOverTime; }

 public List<TrendPoint> getTimeEfficiencyOverTime() { return timeEfficiencyOverTime; }
 public void setTimeEfficiencyOverTime(List<TrendPoint> timeEfficiencyOverTime) { 
     this.timeEfficiencyOverTime = timeEfficiencyOverTime; 
 }

 public Map<String, List<TrendPoint>> getTopicTrends() { return topicTrends; }
 public void setTopicTrends(Map<String, List<TrendPoint>> topicTrends) { this.topicTrends = topicTrends; }

 public String getTrendAnalysis() { return trendAnalysis; }
 public void setTrendAnalysis(String trendAnalysis) { this.trendAnalysis = trendAnalysis; }

 public double getOverallTrend() { return overallTrend; }
 public void setOverallTrend(double overallTrend) { this.overallTrend = overallTrend; }

 public String getTrendDirection() { return trendDirection; }
 public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }

 public double getTrendConfidence() { return trendConfidence; }
 public void setTrendConfidence(double trendConfidence) { this.trendConfidence = trendConfidence; }

 public int getDataPoints() { return dataPoints; }
 public void setDataPoints(int dataPoints) { this.dataPoints = dataPoints; }

 public LocalDateTime getAnalysisDate() { return analysisDate; }
 public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }

 public String getTimeframe() { return timeframe; }
 public void setTimeframe(String timeframe) { this.timeframe = timeframe; }

 public double getScoreVelocity() { return scoreVelocity; }
 public void setScoreVelocity(double scoreVelocity) { this.scoreVelocity = scoreVelocity; }

 public double getAccuracyVelocity() { return accuracyVelocity; }
 public void setAccuracyVelocity(double accuracyVelocity) { this.accuracyVelocity = accuracyVelocity; }

 public double getConsistencyScore() { return consistencyScore; }
 public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }

 public int getImprovingStreak() { return improvingStreak; }
 public void setImprovingStreak(int improvingStreak) { this.improvingStreak = improvingStreak; }

 public int getDecliningStreak() { return decliningStreak; }
 public void setDecliningStreak(int decliningStreak) { this.decliningStreak = decliningStreak; }

 public int getBestStreak() { return bestStreak; }
 public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }

 public int getCurrentStreak() { return currentStreak; }
 public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

 // Utility methods for trend interpretation
 public boolean isImproving() {
     return "improving".equals(trendDirection);
 }

 public boolean isStable() {
     return "stable".equals(trendDirection);
 }

 public boolean isDeclining() {
     return "declining".equals(trendDirection);
 }

 public String getPerformanceSummary() {
     return String.format("Performance is %s with %.1f%% confidence. " +
                        "Score velocity: %.2f points per attempt. " +
                        "Consistency: %.1f%%. Current streak: %d attempts.",
                        trendDirection, trendConfidence, scoreVelocity, 
                        consistencyScore, Math.abs(currentStreak));
 }

 public List<String> getKeyInsights() {
     List<String> insights = new ArrayList<>();
     
     if (isImproving() && currentStreak > 2) {
         insights.add("🔥 You're on a great improvement streak!");
     }
     
     if (consistencyScore > 85) {
         insights.add("🎯 Your performance is highly consistent");
     } else if (consistencyScore < 50) {
         insights.add("⚠️ Consider focusing on consistency");
     }
     
     if (Math.abs(scoreVelocity) > 2.0) {
         insights.add(String.format("📊 Strong %s trend of %.1f points per attempt", 
             scoreVelocity > 0 ? "upward" : "downward", Math.abs(scoreVelocity)));
     }
     
     if (bestStreak > 5) {
         insights.add(String.format("🏆 Your best improvement streak was %d attempts", bestStreak));
     }
     
     return insights;
 }
}