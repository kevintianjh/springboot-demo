package kevintian.springbootdemo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtTokenTest {
	@Autowired private JwtService jwtService;

	@Test
	void test1() {
		Integer userId = 35;
		String roles = "ROLE_USER";

		String token = this.jwtService.generateToken(userId, roles);
		JwtToken jwtToken = new JwtToken(this.jwtService);
		jwtToken.init(token);
		Assertions.assertEquals(jwtToken.getUserId(), userId);
		Assertions.assertEquals(jwtToken.getRoles(), roles);
	}
}