import org.example.Person;
import org.example.processor.DefaultStringInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersonTest {

    @Test
    public void checkSetDefaultFields() {
        Person person = new Person("Some Person", "some@per.son", "0987654321");
        DefaultStringInitializer.setDefaults(person);
        Assertions.assertEquals("John Doe", person.getName());
        Assertions.assertEquals("john@mail.com", person.getEmail());
        Assertions.assertEquals("0979836578", person.getPhone());

    }




}
