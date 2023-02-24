package kevintian.springbootdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
import kevintian.springbootdemo.controller.AuthenticationFilter;

@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
												   AuthenticationProvider authenticationProvider,
												   AuthenticationFilter authenticationFilter) throws Exception {
		return http
			.csrf()
			.disable()
			.authorizeHttpRequests()
			.requestMatchers("/login", "/register", "/test/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}