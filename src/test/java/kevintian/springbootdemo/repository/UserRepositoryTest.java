package kevintian.springbootdemo.repository;

import kevintian.springbootdemo.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.userRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    @Disabled
    void test1() {
        User newUser = new User();
        newUser.setEmail("jane@gmail.com");
        newUser.setRoles("ROLE_USER");
        newUser.setPassword("password1");
        this.userRepository.save(newUser);

        Assertions.assertNotNull(newUser.getUserId());
        User retrievedUser = this.userRepository.findById(newUser.getUserId()).get();

        Assertions.assertEquals("jane@gmail.com", retrievedUser.getEmail());
        Assertions.assertEquals("ROLE_USER", retrievedUser.getRoles());
        Assertions.assertEquals("password1", retrievedUser.getPassword());
    }
}