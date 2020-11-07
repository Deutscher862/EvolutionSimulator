package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class RectangularMapTest {
    RectangularMap defaultMap = new RectangularMap(4, 4);
    RectangularMap oneLineMap = new RectangularMap(0, 6);

    String[] moves1 = {"f", "b", "r", "l"};
    String[] moves2 = {"f", "f", "f", "r", "f"};

    MoveDirection[] directions1 = new OptionsParser().parse(moves1);
    MoveDirection[] directions2 = new OptionsParser().parse(moves2);

    Vector2d v1 = new Vector2d(0, 0);
    Vector2d v2 = new Vector2d(2, 2);
    Vector2d v3 = new Vector2d(0, 2);

    Animal a1 = new Animal(defaultMap, v1);
    Animal a2 = new Animal(defaultMap, v2);
    Animal a3 = new Animal(oneLineMap, v3);

    @Test
    public void isOccupiedTest() {
        //nie sprawdzam wektorów o współrzędnych spoza mapy, ponieważ takie wektory nie zostaną przekazane do metody
        assertFalse(defaultMap.isOccupied(v1));
        defaultMap.mapOfAnimals[0][0] = a1;
        assertTrue(defaultMap.isOccupied(v1));
        defaultMap.mapOfAnimals[0][0] = null;
    }

    @Test
    public void canMoveToTest() {
        assertTrue(defaultMap.canMoveTo(v1));
        defaultMap.mapOfAnimals[0][0] = a1;
        assertFalse(defaultMap.canMoveTo(v1));
        defaultMap.mapOfAnimals[0][0] = null;
        assertFalse(defaultMap.canMoveTo(new Vector2d(-1, -1)));
    }

    @Test
    public void objectAtTest() {
        //nie sprawdzam wektorów o współrzędnych spoza mapy, ponieważ takie wektory nie zostaną przekazane do metody
        assertEquals(defaultMap.objectAt(v1), null);
        defaultMap.mapOfAnimals[0][0] = a1;
        assertEquals(defaultMap.objectAt(v1), a1);
        defaultMap.mapOfAnimals[0][0] = null;
    }

    @Test
    public void placeTest() {
        assertTrue(defaultMap.place(a1));
        assertFalse(defaultMap.place(a1));
        Animal a3 = new Animal(defaultMap, new Vector2d(-1, -1));
        assertFalse(defaultMap.place(a3));
    }

    @Test
    public void runTest() {
        defaultMap.place(a1);
        defaultMap.place(a2);
        defaultMap.run(directions1);
        assertFalse(defaultMap.isOccupied(v1));
        assertTrue(defaultMap.isOccupied(new Vector2d(0, 1)));
        assertTrue(defaultMap.isOccupied(new Vector2d(2, 1)));

        oneLineMap.place(a3);
        oneLineMap.run(directions2);
        assertTrue(oneLineMap.isOccupied(new Vector2d(0, 5)));
        assertFalse(oneLineMap.isOccupied(v3));
    }
}
