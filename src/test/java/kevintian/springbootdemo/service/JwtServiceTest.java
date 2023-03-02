package kevintian.springbootdemo.service;
   
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.jsonwebtoken.Claims;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtServiceTest {
	
	@Autowired private JwtService jwtService;

    @Test 
    void generateTokenTest1() {
    	//test data
    	String userIdStr = "16";
    	String roles = "ROLE_USER,ROLE_ADMIN";
    	
    	//test generate token
    	String token = this.jwtService.generateToken(Integer.parseInt(userIdStr), roles); 
    	Assertions.assertNotNull(token);
    	
    	//test parse token
    	Claims claims = this.jwtService.parse(token); 
    	Assertions.assertEquals(userIdStr, claims.getSubject());
    	Assertions.assertEquals(roles, claims.get("roles"));
    }
}