package kevintian.springbootdemo.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import kevintian.springbootdemo.entity.Student;
import kevintian.springbootdemo.repository.StudentRepository;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StudentControllerTest {
	@Autowired private StudentRepository studentRepository; 
	@Autowired private MockMvc mockMvc; 
	
	@BeforeEach
	void setUp() {
		this.studentRepository.deleteAll(); 
	}
	
	@AfterEach
	void cleanUp() {
		this.studentRepository.deleteAll();
	}
	
	//Test insert new Student
	@Test
	@WithMockUser
	void test1() throws Exception {
		
		String input = "{\"firstName\": \" Kevin  \",\"lastName\": \" Tian\",\"email\": \" tianJhenHaOKeViN@gmail.COM\"}";
		
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/students/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(input)
                ).andDo(MockMvcResultHandlers.print())
				;
		
		java.util.List<Student> studentList = this.studentRepository.findAll();
		Assertions.assertEquals(1, studentList.size());
		
		Assertions.assertEquals("Kevin", studentList.get(0).getFirstName());
		Assertions.assertEquals("Tian", studentList.get(0).getLastName());
		Assertions.assertEquals("tianjhenhaokevin@gmail.com", studentList.get(0).getEmail());
	}
	
	//Test update Student
	@Test
	@WithMockUser
	void test2() throws Exception {
		
		Student student = new Student();
		student.setFirstName("K");
		student.setLastName("T");
		student.setEmail("tian@mail.com"); 
		this.studentRepository.save(student); 
		
		String input = 
				"{\"studentId\": \"$studentId\",\"firstName\": \" Kevin  \",\"lastName\": \" Tian\",\"email\": \" tianJhenHaOKeViN@gmail.COM\"}"
				 .replace("$studentId", student.getStudentId()+"");

		mockMvc.perform(
				  MockMvcRequestBuilders.post("/students/update")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(input)
                 ).andReturn();
		  
		Student updatedStudent = this.studentRepository.findById(student.getStudentId()).get();
		Assertions.assertEquals("Kevin", updatedStudent.getFirstName());
		Assertions.assertEquals("Tian", updatedStudent.getLastName());
		Assertions.assertEquals("tianjhenhaokevin@gmail.com", updatedStudent.getEmail());
	}
	
	//Test read Student
	@Test
	@WithMockUser
	void test3() throws Exception {
		
		ObjectMapper objMapper = new ObjectMapper();
		Student student = new Student();
		student.setFirstName("Jane");
		student.setLastName("Tan");
		student.setEmail("jane@gmail.com"); 
		this.studentRepository.save(student); 
		
		MvcResult result = mockMvc.perform(
				 MockMvcRequestBuilders.get("/students/read?studentId=" + student.getStudentId())
                ).andReturn();
		
		Student retrieveStudent = objMapper.readValue(result.getResponse().getContentAsString(), Student.class);
		Assertions.assertEquals(student.getStudentId(), retrieveStudent.getStudentId());
		Assertions.assertEquals(student.getFirstName(), retrieveStudent.getFirstName());
		Assertions.assertEquals(student.getLastName(), retrieveStudent.getLastName());
		Assertions.assertEquals(student.getEmail(), retrieveStudent.getEmail());
	}
	
	@Test
	@WithMockUser
	void test4() throws Exception {
		
		Student student = new Student();
		student.setFirstName("Jane");
		student.setLastName("Tan");
		student.setEmail("jane@gmail.com"); 
		this.studentRepository.save(student);
		
		String input = 
				"{\"studentId\":\"$studentId\"}".replace("$studentId", student.getStudentId()+"");
		 
		mockMvc.perform(
				  MockMvcRequestBuilders.post("/students/delete")
				 .contentType(MediaType.APPLICATION_JSON)
                 .content(input)
               ).andReturn(); 
		
		Optional<Student> retrievedStudent = this.studentRepository.findById(student.getStudentId());
		Assertions.assertTrue(retrievedStudent.isEmpty());
	}
}