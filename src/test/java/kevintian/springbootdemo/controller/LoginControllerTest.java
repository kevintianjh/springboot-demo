package kevintian.springbootdemo.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders; 
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper; 
import kevintian.springbootdemo.entity.User;
import kevintian.springbootdemo.repository.UserRepository;
import kevintian.springbootdemo.service.JwtService;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Map;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LoginControllerTest {
	
	@Autowired private MockMvc mockMvc;
	@Autowired private UserRepository userRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private JwtService jwtService;
	
	@BeforeEach
	void setUp() {
		this.userRepository.deleteAll();
	}
	
	@AfterEach
	void cleanUp() {
		this.userRepository.deleteAll();
	}
	
	User registerUser() {
		User newUser = new User();
		newUser.setEmail("jane@gmail.com");
		newUser.setPassword(this.passwordEncoder.encode("password1"));
		newUser.setRoles("ROLE_USER"); 
		this.userRepository.save(newUser);
		
		return newUser;
	}
	
	@Test //Testing Login function
	void test1() throws Exception {
		User user = registerUser();
		
		String input = 
				"{\"email\":\"$email\",\"password\":\"$password\"}"
				   .replace("$email", "jane@gmaiL.CoM")
				   .replace("$password", "password1");
		
		MvcResult result =  this.mockMvc.perform(
				    MockMvcRequestBuilders.post("/login")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content(input)
				)//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		String expectedOutput = this.jwtService.generateToken(user.getUserId(), user.getRoles());
		Assertions.assertEquals(expectedOutput, result.getResponse().getContentAsString());
	}
	
	@Test //Testing Register function
	void test2() throws Exception {
		ObjectMapper objMapper = new ObjectMapper();
		
		String input = "{\"email\":\"$email\",\"password\":\"$password\"}"
				   .replace("$email", "jane@gmaiL.CoM")
				   .replace("$password", "password1");
		
		MvcResult result =  this.mockMvc.perform(
				MockMvcRequestBuilders.post("/register")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(input)
			)//.andDo(MockMvcResultHandlers.print())
			 .andReturn();
		 
		JavaType type = objMapper.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
		Map<String,Object> obj = objMapper.readValue(result.getResponse().getContentAsString(), type);
		
		Assertions.assertEquals(200, result.getResponse().getStatus());
		Assertions.assertEquals("jane@gmail.com", (String)obj.get("email"));
		Assertions.assertTrue(obj.get("userId") instanceof Integer);
		Assertions.assertEquals("ROLE_USER", (String)obj.get("roles"));
	}
}