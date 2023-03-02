package kevintian.springbootdemo.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;

class UserTest {

    @Test
    void getAuthoritiesTest() {
        User user = new User();
        user.setRoles("ROLE_ADMIN,ROLE_USER");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        List<? extends GrantedAuthority> authoritiesList = authorities.stream().toList();

        Assertions.assertEquals(2, authorities.size());

        SimpleGrantedAuthority sga1 = (SimpleGrantedAuthority) authoritiesList.get(0);
        SimpleGrantedAuthority sga2 = (SimpleGrantedAuthority) authoritiesList.get(1);

        Assertions.assertEquals("ROLE_ADMIN", sga1.getAuthority());
        Assertions.assertEquals("ROLE_USER", sga2.getAuthority());

        user = new User();
        authorities = user.getAuthorities();

        Assertions.assertNull(authorities);
    }

    @Test
    void setEmailTest() {
        String email = " tianJhenHAOKeVin@gmaIl.cOm ";
        User user = new User();

        user.setEmail(email);
        Assertions.assertEquals(email.trim().toLowerCase(), user.getEmail());

        user.setEmail(null);
        Assertions.assertNull(user.getEmail());
    }


}
