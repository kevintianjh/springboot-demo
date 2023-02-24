package kevintian.springbootdemo.service;

import java.util.Collections;
import java.util.Date;
import java.util.Map; 
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service; 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; 
import io.jsonwebtoken.security.Keys;

@Service 
public class JwtService { 
	
	private String secret = null;
	private Environment env;
	
	@Autowired
	public JwtService(Environment env) {
		this.env = env;
	}
	
	public String generateToken(Integer userId, String roles) {
		Map<String, String> claims = Collections.singletonMap("roles", roles);
		
		return
				Jwts.builder() 
				.setClaims(claims)
				.setSubject(String.valueOf(userId))
				.setExpiration(new Date(System.currentTimeMillis() + 3600000))
				.signWith(key(), SignatureAlgorithm.HS256) 
				.compact();
	}
	
	public Claims parse(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	private SecretKey key() {
		if(this.secret == null) {
			this.secret = this.env.getProperty("JwtService.secret");
		}
		
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
	}
}
