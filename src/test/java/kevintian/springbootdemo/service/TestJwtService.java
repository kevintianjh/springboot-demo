package kevintian.springbootdemo.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestJwtService {

    @Autowired
    private JwtService jwtService;

    @Test
    void test1() {
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