package kevintian.springbootdemo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kevintian.springbootdemo.entity.User;
import kevintian.springbootdemo.service.JwtToken;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
	
	private JwtToken jwtToken; 
	
	@Autowired
	public AuthenticationFilter(JwtToken jwtToken) {
		this.jwtToken = jwtToken;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authorization = request.getHeader("Authorization");
		
		if(authorization == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		this.jwtToken.init(authorization);
		
		User user = new User();
		user.setUserId(this.jwtToken.getUserId());
		user.setRoles(this.jwtToken.getRoles());
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
		
		filterChain.doFilter(request, response);
	} 
}