import org.example.MathForDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathTest {

    @Test
    public void checkRoot() {
        MathForDouble d = new MathForDouble(25);
        Assertions.assertEquals(5,d.getRoot());
    }

    @Test
    public void checkMultiplication() {
        MathForDouble d = new MathForDouble(25);
        Assertions.assertEquals(100,d.multiplyBy(4));
    }

}
