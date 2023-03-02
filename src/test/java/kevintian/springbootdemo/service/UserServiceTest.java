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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void saveTest() {
        this.userRepository.deleteAll();

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

        this.userRepository.deleteAll();
    }

    @Test
    void validatePasswordTest() {
        List<String> rightPwList = Arrays.asList("password1", "12345678", "12345678901234567890");
        rightPwList.forEach(s -> Assertions.assertTrue(this.userService.validatePassword(s)));

        List<String> wrongPwList = Arrays.asList("pw12345", "pw12345678pw12345678o", null);
        wrongPwList.forEach(s -> Assertions.assertFalse(this.userService.validatePassword(s)));
    }

    @Test
    void validateEmailTest() {
        List<String> rightEmailList = Arrays.asList("t@t.c", "tian@gmail.com", "a@a.c.s");
        rightEmailList.forEach(s -> Assertions.assertTrue(this.userService.validateEmail(s)));

        List<String> wrongEmailList = Arrays.asList("hello", "h@h", "h@.c", "h@", "hel@ac.com.", "hello.com", null);
        wrongEmailList.forEach(s -> Assertions.assertFalse(this.userService.validateEmail(s)));
    }
}