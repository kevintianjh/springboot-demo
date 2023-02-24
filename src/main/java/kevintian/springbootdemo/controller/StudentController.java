package kevintian.springbootdemo.controller;

import java.util.Map;
import java.util.Optional; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController; 
import jakarta.validation.Valid;
import kevintian.springbootdemo.entity.Student;
import kevintian.springbootdemo.service.StudentService;

@RestController
@RequestMapping("/students") 
public class StudentController {
	
	@Autowired private StudentService studentService;
	
	@GetMapping("/read")
	public ResponseEntity<Object> read(Integer studentId) {
		Optional<Student> ret = this.studentService.getById(studentId);
		Student student = ret.isPresent()?ret.get():null;
		
		return new ResponseEntity<>(student, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> add(@Valid @RequestBody Student newStudent) {
		newStudent.setStudentId(null);
		this.studentService.save(newStudent);
		return new ResponseEntity<>("successfully added", HttpStatus.OK);
	}
	
	@PostMapping("/update")
	public ResponseEntity<String> update(@Valid @RequestBody Student student) {
		
		if(student.getStudentId() == null) {
			return new ResponseEntity<>("student id cannot be blank", HttpStatus.BAD_REQUEST);
		}
		
		if(!this.studentService.existsById(student.getStudentId())) {
			return new ResponseEntity<>("student id does not exist", HttpStatus.BAD_REQUEST);
		}
		
		this.studentService.save(student);
		
		return new ResponseEntity<>("successfully updated", HttpStatus.OK);
	}
	
	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestBody Map<String, Integer> values) {
		Integer studentId = values.get("studentId");
		
		if(studentId == null) {
			return new ResponseEntity<>("student id cannot be blank", HttpStatus.BAD_REQUEST);
		}
		
		if(!this.studentService.existsById(studentId)) {
			return new ResponseEntity<>("student id does not exist", HttpStatus.BAD_REQUEST);
		}
		
		this.studentService.delete(studentId);
		
		return new ResponseEntity<>("successfully deleted", HttpStatus.OK);
	}
}