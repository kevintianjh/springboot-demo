package kevintian.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import kevintian.springbootdemo.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	
}
