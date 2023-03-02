package kevintian.springbootdemo.controller;
 
import java.util.Map; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController; 
import kevintian.springbootdemo.entity.User;
import kevintian.springbootdemo.service.JwtService;
import kevintian.springbootdemo.service.UserService;

@RestController
public class LoginController {
	
	private PasswordEncoder passwordEncoder;
	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JwtService jwtService; 
	
	@Autowired
	public LoginController(PasswordEncoder passwordEncoder,
						   UserService userService,
						   AuthenticationManager authenticationManager,
						   JwtService jwtService) {
		
		this.passwordEncoder = passwordEncoder;
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Map<String,String> params) { 
		
		String email = params.get("email");
		String password = params.get("password");

		if(email == null) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}

		email = email.trim().toLowerCase();
		
		if(password == null) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}
		
		this.authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(email, password)
		);
		
		User user = this.userService.findByEmail(email);
		String token = this.jwtService.generateToken(user.getUserId(), user.getRoles());
		
		return new ResponseEntity<>(token, HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody Map<String,String> params) {
		
		String email = params.get("email");
		String password = params.get("password");

		if(email == null) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}

		email = email.trim().toLowerCase();

		if(!this.userService.validateEmail(email)) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}
		
		if(password == null) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}

		if(!this.userService.validatePassword(password)) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}
		
		User newUser = new User();
		newUser.setEmail(email);
		newUser.setPassword(this.passwordEncoder.encode(password));
		newUser.setRoles("ROLE_USER");
		
		this.userService.save(newUser);
		newUser.setPassword(null);
		
		return new ResponseEntity<>(newUser, HttpStatus.OK);
	} 
}
