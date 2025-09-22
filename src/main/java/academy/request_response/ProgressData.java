package academy.request_response;

import java.util.List;

//Missing ProgressData class
public class ProgressData {
 private List<TrendPoint> scoreProgression;
 private List<TrendPoint> accuracyProgression;
 private List<TrendPoint> timeProgression;
 private double improvementRate;
 private String progressStatus; // "improving", "stable", "declining"
 private int totalDaysActive;
 private int streakDays;

 public ProgressData() {}

 // Getters and setters
 public List<TrendPoint> getScoreProgression() { return scoreProgression; }
 public void setScoreProgression(List<TrendPoint> scoreProgression) { this.scoreProgression = scoreProgression; }

 public List<TrendPoint> getAccuracyProgression() { return accuracyProgression; }
 public void setAccuracyProgression(List<TrendPoint> accuracyProgression) { this.accuracyProgression = accuracyProgression; }

 public List<TrendPoint> getTimeProgression() { return timeProgression; }
 public void setTimeProgression(List<TrendPoint> timeProgression) { this.timeProgression = timeProgression; }

 public double getImprovementRate() { return improvementRate; }
 public void setImprovementRate(double improvementRate) { this.improvementRate = improvementRate; }

 public String getProgressStatus() { return progressStatus; }
 public void setProgressStatus(String progressStatus) { this.progressStatus = progressStatus; }

 public int getTotalDaysActive() { return totalDaysActive; }
 public void setTotalDaysActive(int totalDaysActive) { this.totalDaysActive = totalDaysActive; }

 public int getStreakDays() { return streakDays; }
 public void setStreakDays(int streakDays) { this.streakDays = streakDays; }
}
