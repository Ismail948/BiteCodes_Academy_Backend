package academy.request_response;

import java.util.List;

import academy.models.ExamQuestion;

public class SectionWiseQuestionResponse {
    private String sectionName;
    private List<ExamQuestion> questions;

    public SectionWiseQuestionResponse(String sectionName, List<ExamQuestion> questions) {
        this.sectionName = sectionName;
        this.questions = questions;
    }

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public List<ExamQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<ExamQuestion> questions) {
		this.questions = questions;
	}


	public SectionWiseQuestionResponse() {
	}

    // Getters and setters
}
