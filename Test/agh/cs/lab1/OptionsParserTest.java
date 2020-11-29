package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public class OptionsParserTest {
    OptionsParser parser = new OptionsParser();

    String[] t1 = {"f", "forward"};
    String[] t2 = {"b", "b", "not_a_direction", "backward"};
    String[] t3 = {"1234", "l", "right", "left"};
    String[] t4 = {"l", "right", "left"};

    MoveDirection[] r1 = {MoveDirection.FORWARD, MoveDirection.FORWARD};
    MoveDirection[] r2 = {MoveDirection.BACKWARD, MoveDirection.BACKWARD, MoveDirection.BACKWARD};
    MoveDirection[] r3 = {MoveDirection.LEFT, MoveDirection.RIGHT, MoveDirection.LEFT};

    @Test
    public void parseTest() {
        assertArrayEquals(parser.parse(t1), r1);
        assertThrows(IllegalArgumentException.class, () -> parser.parse(t2));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(t3));
        assertArrayEquals(parser.parse(t4), r3);
    }

}