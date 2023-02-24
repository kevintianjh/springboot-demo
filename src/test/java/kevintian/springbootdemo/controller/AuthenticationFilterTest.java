package kevintian.springbootdemo.controller;
 
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.core.context.SecurityContextHolder; 
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import kevintian.springbootdemo.entity.User;
import kevintian.springbootdemo.service.JwtService;
import kevintian.springbootdemo.service.JwtToken;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationFilterTest {
	
	private Integer userId = null;
	private String roles = null;
	
	private AuthenticationFilter authenticationFilter = null;
	@Autowired private JwtService jwtService;

	private HttpServletRequest mockHttpServletRequest = null;  
	private FilterChain mockFilterChain = null; 
	
	@BeforeEach 
	void setUp() {
		//Test data
		userId = 20;
		roles = "ROLE_ADMIN,ROLE_USER";

		//Creating mock JwtToken and then AuthenticationFilter
		JwtToken jwtToken = new JwtToken(this.jwtService);
		this.authenticationFilter = new AuthenticationFilter(jwtToken);
		
		//Creating mock HttpServletRequest
		this.mockHttpServletRequest = Mockito.mock(HttpServletRequest.class); 
		
		//Creating mock FilterChain
		this.mockFilterChain = Mockito.mock(FilterChain.class);  
    }
	
	//Test the valid authentication token flow
	@Test 
    void test1() throws Exception {
		String auth = this.jwtService.generateToken(this.userId, this.roles);
		Mockito.when(this.mockHttpServletRequest.getHeader("Authorization")).thenReturn(auth);
		
    	try {
    		this.authenticationFilter.doFilterInternal(this.mockHttpServletRequest, null, this.mockFilterChain);
    	}
    	catch(Exception e) {
    		Assertions.fail("Should not incur an exception");
    	}

		Mockito.verify(this.mockFilterChain, Mockito.times(1)).doFilter(this.mockHttpServletRequest, null);
    	 
    	UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    	User user = (User)token.getPrincipal();
    	
    	Assertions.assertEquals(user.getUserId(), this.userId);
    	Assertions.assertEquals(user.getRoles(), this.roles);
    }
	
	//Test the wrong authentication token flow
	@Test
	void test2() {
		String auth = "wrong token";
		Mockito.when(this.mockHttpServletRequest.getHeader("Authorization")).thenReturn(auth);

		try {
    		this.authenticationFilter.doFilterInternal(this.mockHttpServletRequest, null, this.mockFilterChain);
    	
    		Assertions.fail("Exception should occur");
		}
    	catch(Exception e) {} 
	}

	//Test no authentication token flow
	@Test
	void test3() {
		Mockito.when(this.mockHttpServletRequest.getHeader("Authorization")).thenReturn(null);

		try {
			this.authenticationFilter.doFilterInternal(this.mockHttpServletRequest, null, this.mockFilterChain);

			Mockito.verify(this.mockFilterChain, Mockito.times(1)).doFilter(this.mockHttpServletRequest, null);
		}
		catch(Exception e) {
			Assertions.fail("Exception should not occur");
		}
	}
} 