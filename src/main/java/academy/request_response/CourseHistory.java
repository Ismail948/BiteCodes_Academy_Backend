package academy.request_response;

import java.util.List;

import academy.models.ExamAttempt;

public class CourseHistory {
    private String courseName;
    private List<ExamAttempt> attempts;
    private ScoreProgression scoreProgression;
    private TopicAnalysis topicAnalysis;
    private TimeAnalysis timeAnalysis;
    private String improvementSuggestions;

    // Constructors, getters, setters...
    public CourseHistory() {}

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public List<ExamAttempt> getAttempts() { return attempts; }
    public void setAttempts(List<ExamAttempt> attempts) { this.attempts = attempts; }

    public ScoreProgression getScoreProgression() { return scoreProgression; }
    public void setScoreProgression(ScoreProgression scoreProgression) { this.scoreProgression = scoreProgression; }

    public TopicAnalysis getTopicAnalysis() { return topicAnalysis; }
    public void setTopicAnalysis(TopicAnalysis topicAnalysis) { this.topicAnalysis = topicAnalysis; }

    public TimeAnalysis getTimeAnalysis() { return timeAnalysis; }
    public void setTimeAnalysis(TimeAnalysis timeAnalysis) { this.timeAnalysis = timeAnalysis; }

    public String getImprovementSuggestions() { return improvementSuggestions; }
    public void setImprovementSuggestions(String improvementSuggestions) { this.improvementSuggestions = improvementSuggestions; }
}
