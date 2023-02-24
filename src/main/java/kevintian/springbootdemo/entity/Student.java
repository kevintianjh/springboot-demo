package kevintian.springbootdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "student")
public class Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer studentId;
	
	@Column(name = "first_name")
	@NotBlank
	private String firstName;
	
	@Column(name = "last_name")
	@NotBlank
	private String lastName;
	
	@Column(name = "email")
	@Email 
	@NotNull
	private String email;
	
	public Student() {}
	
	public Student(Integer studentId) {
		setStudentId(studentId);
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName==null?null:firstName.trim();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName==null?null:lastName.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email==null?null:email.trim().toLowerCase();
	}  
}