package kevintian.springbootdemo.entity;

import java.util.ArrayList;
import java.util.Collection; 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user")
public class User implements UserDetails { 
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	@Column(name = "email", unique = true)
	@NotNull
	@Email
	private String email;
	
	@Column(name = "password")
	@NotBlank
	private String password;
	
	@Column(name = "roles")
	@NotBlank
	private String roles; 
	 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { 
		ArrayList<SimpleGrantedAuthority> authorities = null;
		
		if(this.roles != null) {
			String[] roleList = this.roles.split(",");
			authorities = new ArrayList<>();
			
			for(String role : roleList) {
				authorities.add(new SimpleGrantedAuthority(role));
			} 
		}
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email==null?null:email.trim().toLowerCase();
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public void setPassword(String password) {
		this.password = password;
	}  
}