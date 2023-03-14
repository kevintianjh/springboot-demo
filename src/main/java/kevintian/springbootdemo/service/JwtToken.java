package kevintian.springbootdemo.service;
 
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope; 
import io.jsonwebtoken.Claims;

@Component
@RequestScope
public class JwtToken {
	
	private JwtService jwtService;
	private Claims claims;
	
	@Autowired
	public JwtToken(JwtService jwtService) { 
		this.jwtService = jwtService;
	}
	
	public void init(String token) {
		this.claims = this.jwtService.parse(token);
	}
	
	public Integer getUserId() {
		return Integer.parseInt(this.claims.getSubject());
	}
	
	public String getRoles() {
		return this.claims.get("roles", String.class);
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("This object is destroyed!");
	}
}