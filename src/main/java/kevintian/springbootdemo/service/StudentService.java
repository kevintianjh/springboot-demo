package kevintian.springbootdemo.service;

import java.util.Optional; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import kevintian.springbootdemo.entity.Student;
import kevintian.springbootdemo.repository.StudentRepository;

@Service
public class StudentService { 
	
	@Autowired private StudentRepository studentRepository;
	
	public Optional<Student> getById(Integer studentId) {
		return this.studentRepository.findById(studentId);
	}
	
	public void save(Student student) {
		this.studentRepository.save(student);
	}
	
	public boolean existsById(Integer studentId) {
		return this.studentRepository.existsById(studentId);
	}
	
	public void delete(Integer studentId) {
		this.studentRepository.delete(new Student(studentId));
	}
}