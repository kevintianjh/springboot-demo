package kevintian.springbootdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import kevintian.springbootdemo.entity.User;
import kevintian.springbootdemo.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired private UserRepository userRepository;
	
	public User findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	public void save(User user) {
		this.userRepository.save(user);
	}
	
	public boolean validatePassword(String password) { 
		return password != null && password.length() >= 8 && password.length() <= 20;
	}
}