package academy.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
public class StudyMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String materialName;
    private String pdfUrl;
    private String category; // "TOPIC_MATERIAL" or "PRACTICE_PAPER"

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonBackReference
    private SyllabusTopic topic;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public SyllabusTopic getTopic() {
		return topic;
	}

	public void setTopic(SyllabusTopic topic) {
		this.topic = topic;
	}

	public StudyMaterial(Long id, String materialName, String pdfUrl, String category, SyllabusTopic topic) {
		super();
		this.id = id;
		this.materialName = materialName;
		this.pdfUrl = pdfUrl;
		this.category = category;
		this.topic = topic;
	}

	public StudyMaterial() {
	}
    
    
}
