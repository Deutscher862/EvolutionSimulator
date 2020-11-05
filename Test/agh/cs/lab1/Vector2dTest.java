package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class Vector2dTest {
    Vector2d v1 = new Vector2d(1, 2);
    Vector2d v11 = new Vector2d(1, 2);
    Vector2d v2 = new Vector2d(-1, -2);
    Vector2d v3 = new Vector2d(0, 0);
    Vector2d v4 = new Vector2d(1234, -4321);
    Vector2d v5 = new Vector2d(-10, 0);
    Vector2d v6 = new Vector2d(0, 16);
    Vector2d v7 = new Vector2d(-2345, 5432);

    @Test
    public void equalsTest() {
        assertFalse(v1.equals(v2));
        assertTrue(v1.equals(v1));
        assertTrue(v1.equals(v11));
        assertFalse(v2.equals(v3));
    }

    @Test
    public void toStringTest() {
        assertEquals(v1.toString(), "(1, 2)");
        assertEquals(v2.toString(), "(-1, -2)");
        assertEquals(v3.toString(), "(0, 0)");
        assertEquals(v4.toString(), "(1234, -4321)");
    }

    @Test
    public void precedesTest() {
        assertTrue(v2.precedes(v1));
        assertFalse(v1.precedes(v2));
        assertTrue(v1.precedes(v1));
        assertFalse(v3.precedes(v4));
        assertTrue(v3.precedes(v6));
        assertTrue(v5.precedes(v6));
    }

    @Test
    public void followsTest() {
        assertFalse(v2.follows(v1));
        assertTrue(v1.follows(v2));
        assertTrue(v1.follows(v1));
        assertFalse(v3.follows(v4));
        assertFalse(v3.follows(v6));
        assertFalse(v5.follows(v6));
    }

    @Test
    public void upperRightTest() {
        assertEquals(v1.upperRight(v1), v1);
        assertEquals(v2.upperRight(v1), v1);
        assertEquals(v2.upperRight(v3), v3);
        assertEquals(v4.upperRight(v5), new Vector2d(1234, 0));
        assertEquals(v5.upperRight(v3), new Vector2d(0, 0));
        assertEquals(v6.upperRight(v7), new Vector2d(0, 5432));
    }

    @Test
    public void lowerLeftTest() {
        assertEquals(v1.lowerLeft(v1), v1);
        assertEquals(v1.lowerLeft(v2), v2);
        assertEquals(v3.lowerLeft(v2), v2);
        assertEquals(v4.lowerLeft(v3), new Vector2d(0, -4321));
        assertEquals(v5.lowerLeft(v3), new Vector2d(-10, 0));
        assertEquals(v6.lowerLeft(v7), new Vector2d(-2345, 16));
    }

    @Test
    public void addTest() {
        assertEquals(v1.add(v1), new Vector2d(2, 4));
        assertEquals(v2.add(v1), v3);
        assertEquals(v3.add(v4), v4);
        assertEquals(v5.add(v6), new Vector2d(-10, 16));
        assertEquals(v7.add(v1), new Vector2d(-2344, 5434));
    }

    @Test
    public void subtractTest() {
        assertEquals(v1.subtract(v1), v3);
        assertEquals(v2.subtract(v1), new Vector2d(-2, -4));
        assertEquals(v3.subtract(v4), new Vector2d(-1234, 4321));
        assertEquals(v4.subtract(v5), new Vector2d(1244, -4321));
        assertEquals(v6.subtract(v5), new Vector2d(10, 16));
    }

    @Test
    public void oppositeTest() {
        assertEquals(v1.opposite(), new Vector2d(-1, -2));
        assertEquals(v2.opposite(), new Vector2d(1, 2));
        assertEquals(v3.opposite(), v3);
        assertEquals(v5.opposite(), new Vector2d(10, 0));
        assertEquals(v6.opposite(), new Vector2d(0, -16));
    }
}
