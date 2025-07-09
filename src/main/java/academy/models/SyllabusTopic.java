package academy.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class SyllabusTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;
    private String title;
    private Integer weightage; // e.g., marks or percentage
    private Integer totalMarks;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StudyMaterial> materials = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getWeightage() {
		return weightage;
	}

	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}

	public Integer getTotalMarks() {
		return totalMarks;
	}

	public void setTotalMarks(Integer totalMarks) {
		this.totalMarks = totalMarks;
	}

	public List<StudyMaterial> getMaterials() {
		return materials;
	}

	public void setMaterials(List<StudyMaterial> materials) {
		this.materials = materials;
	}

	public SyllabusTopic(Long id, String courseName, String title, Integer weightage, Integer totalMarks,
			List<StudyMaterial> materials) {
		super();
		this.id = id;
		this.courseName = courseName;
		this.title = title;
		this.weightage = weightage;
		this.totalMarks = totalMarks;
		this.materials = materials;
	}

	public SyllabusTopic() {
		// TODO Auto-generated constructor stub
	}
    
    
}
