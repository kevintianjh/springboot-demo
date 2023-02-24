package kevintian.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import kevintian.springbootdemo.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	public User findByEmail(String email); 
}