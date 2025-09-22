package academy.models;

import jakarta.persistence.Embeddable;

//Enhanced QuestionResult
@Embeddable
public class QuestionResult {
private String questionId;
private boolean correct;
private int userAnswer;
private int correctAnswer;
private String explanation;
private String topic;
private String difficulty;
private int timeSpent; // in seconds
private boolean flagged; // if user flagged this question

// Constructors, getters, setters...
public QuestionResult() {}

public QuestionResult(String questionId, boolean correct, int userAnswer, int correctAnswer, String explanation,
		String topic, String difficulty, int timeSpent, boolean flagged) {
	super();
	this.questionId = questionId;
	this.correct = correct;
	this.userAnswer = userAnswer;
	this.correctAnswer = correctAnswer;
	this.explanation = explanation;
	this.topic = topic;
	this.difficulty = difficulty;
	this.timeSpent = timeSpent;
	this.flagged = flagged;
}

// Getters and setters...
public String getQuestionId() { return questionId; }
public void setQuestionId(String questionId) { this.questionId = questionId; }

public boolean isCorrect() { return correct; }
public void setCorrect(boolean correct) { this.correct = correct; }

public int getUserAnswer() { return userAnswer; }
public void setUserAnswer(int userAnswer) { this.userAnswer = userAnswer; }

public int getCorrectAnswer() { return correctAnswer; }
public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

public String getExplanation() { return explanation; }
public void setExplanation(String explanation) { this.explanation = explanation; }

public String getTopic() { return topic; }
public void setTopic(String topic) { this.topic = topic; }

public String getDifficulty() { return difficulty; }
public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

public int getTimeSpent() { return timeSpent; }
public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }

public boolean isFlagged() { return flagged; }
public void setFlagged(boolean flagged) { this.flagged = flagged; }
}