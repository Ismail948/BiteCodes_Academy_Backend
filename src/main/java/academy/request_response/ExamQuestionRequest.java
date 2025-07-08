package academy.request_response;


import java.util.List;

import academy.models.ExamQuestion;


public class ExamQuestionRequest {
    private List<ExamQuestion> questions;

    public List<ExamQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ExamQuestion> questions) {
        this.questions = questions;
    }
}
