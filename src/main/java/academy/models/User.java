package academy.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true, unique = true)
	private String username;
	
	@Column()
	private String name;
	
	@Column()
	private String profileurl;
	
	  // ✅ Purchased courses
    @ManyToMany
    @JoinTable(
        name = "user_courses",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "course_slug")
    )
    private List<Course> purchasedCourses = new ArrayList<>();
    
	
    @Size(min = 10, max = 15, message = "Phone number must be between 10 to 15 digits")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain only digits")
    @Column(nullable = true, unique = true)
    private String phonenum;
	
	
	@Column()
	private String state;

	@Column(nullable = true)
	private String password;

	@Email
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = true)
	private String role;

	@Column(nullable = true)
	private boolean enabled = false;

	private String otp;
	private LocalDateTime otpExpiry;

	private String bio;
	private String timezone;
	private String availability;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();



	

	public List<Course> getPurchasedCourses() {
		return purchasedCourses;
	}

	public void setPurchasedCourses(List<Course> purchasedCourses) {
		this.purchasedCourses = purchasedCourses;
	}

	public User(Long id, String username, String name, String profileurl, List<Course> purchasedCourses,
			@Size(min = 10, max = 15, message = "Phone number must be between 10 to 15 digits") @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain only digits") String phonenum,
			String state, String password, @Email String email, String role, boolean enabled, String otp,
			LocalDateTime otpExpiry, String bio, String timezone, String availability, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.profileurl = profileurl;
		this.purchasedCourses = purchasedCourses;
		this.phonenum = phonenum;
		this.state = state;
		this.password = password;
		this.email = email;
		this.role = role;
		this.enabled = enabled;
		this.otp = otp;
		this.otpExpiry = otpExpiry;
		this.bio = bio;
		this.timezone = timezone;
		this.availability = availability;
		this.createdAt = createdAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileurl() {
		return profileurl;
	}

	public void setProfileurl(String profileurl) {
		this.profileurl = profileurl;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public User() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpExpiry() {
		return otpExpiry;
	}

	public void setOtpExpiry(LocalDateTime otpExpiry) {
		this.otpExpiry = otpExpiry;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}