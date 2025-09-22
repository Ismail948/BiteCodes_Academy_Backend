package academy.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;  // ✅ correct import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import academy.models.ExamAttempt;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    List<ExamAttempt> findByUserId(Long userId);

    List<ExamAttempt> findByUserIdAndCourseNameOrderByAttemptedAtDesc(Long userId, String courseName);

    List<ExamAttempt> findByCourseName(String courseName);

    List<ExamAttempt> findByUserIdAndAttemptedAtAfterOrderByAttemptedAtAsc(Long userId, String attemptedAt);

    @Query("SELECT a FROM ExamAttempt a WHERE a.userId = :userId AND a.attemptedAt >= :fromDate ORDER BY a.attemptedAt ASC")
    List<ExamAttempt> findRecentAttemptsByUser(@Param("userId") Long userId, @Param("fromDate") String fromDate);

    @Query("SELECT AVG(a.score) FROM ExamAttempt a WHERE a.courseName = :courseName")
    Double findAverageScoreByCourse(@Param("courseName") String courseName);

    @Query("SELECT AVG(a.timeTaken) FROM ExamAttempt a WHERE a.courseName = :courseName")
    Double findAverageTimeByCourse(@Param("courseName") String courseName);

    // ✅ fixed Pageable usage
    @Query("SELECT a FROM ExamAttempt a WHERE a.courseName = :courseName ORDER BY a.score DESC")
    List<ExamAttempt> findTopScoresByCourse(@Param("courseName") String courseName, Pageable pageable);

    @Query("SELECT COUNT(a) FROM ExamAttempt a WHERE a.courseName = :courseName AND a.score > :score")
    Long countBetterScores(@Param("courseName") String courseName, @Param("score") Double score);

    @Query("SELECT a.userId, MAX(a.score) as bestScore, COUNT(a) as totalAttempts " +
           "FROM ExamAttempt a WHERE a.courseName = :courseName " +
           "GROUP BY a.userId ORDER BY bestScore DESC")
    List<Object[]> findLeaderboardData(@Param("courseName") String courseName);
}
