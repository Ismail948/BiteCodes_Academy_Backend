package academy.request_response;

public class QuestionAnalysis {
    private String questionId;
    private String questionText;
    private String topic;
    private String difficulty;
    private boolean userCorrect;
    private int userAnswer;
    private int correctAnswer;
    private String[] options;
    private String explanation;
    private int timeSpent;
    private double topicAverageAccuracy;
    private String improvementTip;

    public QuestionAnalysis() {}

    // Getters and setters
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public boolean isUserCorrect() { return userCorrect; }
    public void setUserCorrect(boolean userCorrect) { this.userCorrect = userCorrect; }

    public int getUserAnswer() { return userAnswer; }
    public void setUserAnswer(int userAnswer) { this.userAnswer = userAnswer; }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public int getTimeSpent() { return timeSpent; }
    public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }

    public double getTopicAverageAccuracy() { return topicAverageAccuracy; }
    public void setTopicAverageAccuracy(double topicAverageAccuracy) { this.topicAverageAccuracy = topicAverageAccuracy; }

    public String getImprovementTip() { return improvementTip; }
    public void setImprovementTip(String improvementTip) { this.improvementTip = improvementTip; }
}