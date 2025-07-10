package academy.models;

import javax.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class University {
    @Id
    private String slug;

    private String name;
    private String description;
    private String location;
    private int ranking;
    private int established;
    private String website;
    private String admissionLink;

    @ElementCollection
    private List<String> examsAccepted;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Course> courses;


    // Getters & Setters
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
        if (courses != null) {
            for (Course c : courses) {
                c.setUniversity(this); // link course to this university
            }
        }
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getRanking() {
		return ranking;
	}
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	public int getEstablished() {
		return established;
	}
	public void setEstablished(int established) {
		this.established = established;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getAdmissionLink() {
		return admissionLink;
	}
	public void setAdmissionLink(String admissionLink) {
		this.admissionLink = admissionLink;
	}
	public List<String> getExamsAccepted() {
		return examsAccepted;
	}
	public void setExamsAccepted(List<String> examsAccepted) {
		this.examsAccepted = examsAccepted;
	}
	public University() {
	}
	public University(String slug, String name, String description, String location, int ranking, int established,
			String website, String admissionLink, List<String> examsAccepted, List<Course> courses) {
		super();
		this.slug = slug;
		this.name = name;
		this.description = description;
		this.location = location;
		this.ranking = ranking;
		this.established = established;
		this.website = website;
		this.admissionLink = admissionLink;
		this.examsAccepted = examsAccepted;
		this.courses = courses;
	}

    // other getters/setters omitted for brevity
}
