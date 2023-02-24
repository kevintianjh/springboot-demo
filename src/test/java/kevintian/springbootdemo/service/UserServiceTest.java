package kevintian.springbootdemo.service;

import kevintian.springbootdemo.entity.User;
import kevintian.springbootdemo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.userRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    void test1() {
        User user = this.userService.findByEmail("tian@gmail.com");
        Assertions.assertNull(user);

        User addUser = new User();
        addUser.setEmail("tian@gmail.com");
        addUser.setPassword(this.passwordEncoder.encode("password1"));
        addUser.setRoles("ROLE_USER");
        this.userRepository.save(addUser);

        user = this.userService.findByEmail("tian@gmail.com");
        Assertions.assertEquals("tian@gmail.com", user.getEmail());
        Assertions.assertEquals("ROLE_USER", user.getRoles());

    }
}