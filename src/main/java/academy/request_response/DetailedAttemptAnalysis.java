package academy.request_response;

import java.util.List;
import java.util.Map;

import academy.models.ExamAttempt;
import academy.models.TopicPerformance;

public class DetailedAttemptAnalysis {
    private ExamAttempt attempt;
    private List<QuestionAnalysis> questionAnalyses;
    private Map<String, TopicPerformance> topicBreakdown;
    private TimeDistribution timeDistribution;
    private ComparisonData comparisonData;
    private String aiAnalysis;
    private List<String> keyInsights;

    // Constructors, getters, setters...
    public DetailedAttemptAnalysis() {}

	public ExamAttempt getAttempt() {
		return attempt;
	}

	public void setAttempt(ExamAttempt attempt) {
		this.attempt = attempt;
	}

	public List<QuestionAnalysis> getQuestionAnalyses() {
		return questionAnalyses;
	}

	public void setQuestionAnalyses(List<QuestionAnalysis> questionAnalyses) {
		this.questionAnalyses = questionAnalyses;
	}

	public Map<String, TopicPerformance> getTopicBreakdown() {
		return topicBreakdown;
	}

	public void setTopicBreakdown(Map<String, TopicPerformance> topicBreakdown) {
		this.topicBreakdown = topicBreakdown;
	}

	public TimeDistribution getTimeDistribution() {
		return timeDistribution;
	}

	public void setTimeDistribution(TimeDistribution timeDistribution) {
		this.timeDistribution = timeDistribution;
	}

	public ComparisonData getComparisonData() {
		return comparisonData;
	}

	public void setComparisonData(ComparisonData comparisonData) {
		this.comparisonData = comparisonData;
	}

	public String getAiAnalysis() {
		return aiAnalysis;
	}

	public void setAiAnalysis(String aiAnalysis) {
		this.aiAnalysis = aiAnalysis;
	}

	public List<String> getKeyInsights() {
		return keyInsights;
	}

	public void setKeyInsights(List<String> keyInsights) {
		this.keyInsights = keyInsights;
	}

	public DetailedAttemptAnalysis(ExamAttempt attempt, List<QuestionAnalysis> questionAnalyses,
			Map<String, TopicPerformance> topicBreakdown, TimeDistribution timeDistribution,
			ComparisonData comparisonData, String aiAnalysis, List<String> keyInsights) {
		super();
		this.attempt = attempt;
		this.questionAnalyses = questionAnalyses;
		this.topicBreakdown = topicBreakdown;
		this.timeDistribution = timeDistribution;
		this.comparisonData = comparisonData;
		this.aiAnalysis = aiAnalysis;
		this.keyInsights = keyInsights;
	}
    
    // Add all getters and setters...
}
