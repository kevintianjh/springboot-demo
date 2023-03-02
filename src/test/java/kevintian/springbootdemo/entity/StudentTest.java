package kevintian.springbootdemo.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StudentTest {

    @Test
    void setterTest() {
        String firstName = " KeviN ";

        Student student = new Student();
        student.setFirstName(firstName);
        Assertions.assertEquals(firstName.trim(), student.getFirstName());
        student.setFirstName(null);
        Assertions.assertNull(student.getFirstName());

        String lastName = " tIaN ";
        student.setLastName(lastName);
        Assertions.assertEquals(lastName.trim(), student.getLastName());
        student.setLastName(null);
        Assertions.assertNull(student.getLastName());

        String email = " tIaNJhEnHaOKevin@gmail.cOM ";
        student.setEmail(email);
        Assertions.assertEquals(email.trim().toLowerCase(), student.getEmail());
        student.setEmail(null);
        Assertions.assertNull(student.getEmail());
    }

}
