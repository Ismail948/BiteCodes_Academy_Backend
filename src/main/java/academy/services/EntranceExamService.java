package academy.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import academy.models.EntranceExam;
import academy.repositories.EntranceExamRepository;

@Service
public class EntranceExamService {

    @Autowired
    private EntranceExamRepository repo;

    // Cache all exams
    @Cacheable(value = "exams")
    public List<EntranceExam> getAllExams() {
        return repo.findAll();
    }

    // Cache featured exams
    @Cacheable(value = "featuredExams")
    public List<EntranceExam> getFeaturedExams() {
        return repo.findByFeatured(true);
    }

    // Cache individual exam
    @Cacheable(value = "exam", key = "#id")
    public Optional<EntranceExam> getExamById(String id) {
        return repo.findById(id);
    }

    // Save new exams and clear relevant caches
    @CacheEvict(value = {"exams", "featuredExams"}, allEntries = true)
    public List<EntranceExam> addExams(List<EntranceExam> exams) {
        return repo.saveAll(exams);
    }

    // Delete exam and clear caches
    @CacheEvict(value = {"exams", "featuredExams", "exam"}, allEntries = true)
    public void deleteExam(String id) {
        repo.deleteById(id);
    }
}
