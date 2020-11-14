package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class RectangularMapTest {
    RectangularMap defaultMap = new RectangularMap(4, 4);

    Vector2d v1 = new Vector2d(0, 0);

    Animal a1 = new Animal(defaultMap, v1);

    @Test
    public void isOccupiedTest() {
        //nie sprawdzam wektorów o współrzędnych spoza mapy, ponieważ takie wektory nie zostaną przekazane do metody
        assertFalse(defaultMap.isOccupied(v1));
        defaultMap.mapOfAnimals.put(v1, a1);
        assertTrue(defaultMap.isOccupied(v1));
        defaultMap.mapOfAnimals.replace(v1, null);
    }

    @Test
    public void canMoveToTest() {
        assertTrue(defaultMap.canMoveTo(v1));
        defaultMap.mapOfAnimals.put(v1, a1);
        assertFalse(defaultMap.canMoveTo(v1));
        defaultMap.mapOfAnimals.replace(v1, null);
        assertFalse(defaultMap.canMoveTo(new Vector2d(-1, -1)));
    }

    @Test
    public void objectAtTest() {
        //nie sprawdzam wektorów o współrzędnych spoza mapy, ponieważ takie wektory nie zostaną przekazane do metody
        assertEquals(defaultMap.objectAt(v1), null);
        defaultMap.mapOfAnimals.put(v1, a1);
        assertEquals(defaultMap.objectAt(v1), a1);
        defaultMap.mapOfAnimals.replace(v1, null);
    }

    @Test
    public void placeTest() {
        assertTrue(defaultMap.place(a1));
        assertFalse(defaultMap.place(a1));
        Animal a3 = new Animal(defaultMap, new Vector2d(-1, -1));
        assertFalse(defaultMap.place(a3));
    }
}
