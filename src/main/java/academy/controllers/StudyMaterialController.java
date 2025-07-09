package academy.controllers;

import academy.models.SyllabusTopic;
import academy.repositories.SyllabusTopicRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-material")
public class StudyMaterialController {

    private final SyllabusTopicRepository topicRepo;

    public StudyMaterialController(SyllabusTopicRepository topicRepo) {
        this.topicRepo = topicRepo;
    }

    @GetMapping("/{courseName}")
    public List<SyllabusTopic> getAllStudyMaterial(@PathVariable String courseName) {
        return topicRepo.findByCourseName(courseName);
    }

    @PostMapping("/{courseName}")
    public List<SyllabusTopic> saveStudyMaterials(
            @PathVariable String courseName,
            @RequestBody List<SyllabusTopic> topics
    ) {
        // Assign courseName to all incoming topics
        for (SyllabusTopic topic : topics) {
            topic.setCourseName(courseName);
            if (topic.getMaterials() != null) {
                topic.getMaterials().forEach(material -> material.setTopic(topic));
            }
        }
        return topicRepo.saveAll(topics);
    }
}
enum MaterialCategory {
    TOPIC_PDF,
    PRACTICE_PAPER
}
