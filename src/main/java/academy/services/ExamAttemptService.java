package academy.services;

import org.springframework.stereotype.Service;

import academy.models.ExamAttempt;
import academy.repositories.ExamAttemptRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;

    public Optional<ExamAttempt> getAttemptById(Long attemptId) {
        return examAttemptRepository.findById(attemptId);
    }
    
    public ExamAttemptService(ExamAttemptRepository examAttemptRepository) {
        this.examAttemptRepository = examAttemptRepository;
    }

    public List<ExamAttempt> getAttemptsByUser(Long userId) {
        return examAttemptRepository.findByUserId(userId);
    }

    public ExamAttempt saveAttempt(ExamAttempt attempt) {
        return examAttemptRepository.save(attempt);
    }
}
