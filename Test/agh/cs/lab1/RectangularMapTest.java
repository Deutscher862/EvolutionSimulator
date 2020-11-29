package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.*;

public class RectangularMapTest {
    RectangularMap defaultMap = new RectangularMap(4, 4);

    Vector2d v1 = new Vector2d(0, 0);

    Animal a1 = new Animal(defaultMap, v1);

    @Test
    public void isOccupiedTest() {
        assertFalse(defaultMap.isOccupied(new Vector2d(-1, -1)));
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
        assertEquals(defaultMap.objectAt(v1), null);
        defaultMap.mapOfAnimals.put(v1, a1);
        assertEquals(defaultMap.objectAt(v1), a1);
        defaultMap.mapOfAnimals.replace(v1, null);
    }

    @Test
    public void placeTest() {
        assertTrue(defaultMap.place(a1));
        assertThrows(IllegalArgumentException.class, () -> defaultMap.place(a1));
        Animal a3 = new Animal(defaultMap, new Vector2d(-1, -1));
        assertThrows(IllegalArgumentException.class, () -> defaultMap.place(a3));
    }
}