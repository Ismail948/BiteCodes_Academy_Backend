package academy.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Course {
    @Id
    private String slug;

    private String name;
	private String description;
    private String examSlug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_slug", referencedColumnName = "slug")
    @JsonBackReference
    private University university;
    private Double price; // ✅ Add price



    // Constructors
    public Course() {}


    public Course(String slug, String name, String description, String examSlug, University university, Double price) {
		super();
		this.slug = slug;
		this.name = name;
		this.description = description;
		this.examSlug = examSlug;
		this.university = university;
		this.price = price;
	}


	public Double getPrice() {
		return price;
	}


	public void setPrice(Double price) {
		this.price = price;
	}


	// Getters & Setters
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExamSlug() { return examSlug; }
    public void setExamSlug(String examSlug) { this.examSlug = examSlug; }

    public University getUniversity() { return university; }
    public void setUniversity(University university) { this.university = university; }
}
