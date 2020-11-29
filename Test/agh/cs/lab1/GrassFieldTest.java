package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.*;

public class GrassFieldTest {
    GrassField defaultMap = new GrassField(10);
    GrassField smallMap = new GrassField(2);
    GrassField bigMap = new GrassField(100);

    Vector2d v1 = new Vector2d(0, 0);

    Animal a1 = new Animal(defaultMap, v1);

    @Test
    //sprawdzam czy na mapach wygenerowała się wskazana ilośc trawy
    public void grassFieldTest(){
        int grassCounter = 0;
        int max1 = (int) Math.ceil(Math.sqrt(10*10));
        for (int i = 0; i < max1; i++){
            for(int j = 0; j < max1; j++) {
                if (defaultMap.objectAt(new Vector2d(i, j)) instanceof Grass) grassCounter += 1;
            }
        }
        assertEquals(grassCounter, 10);

        grassCounter=0;
        int max2 = (int) Math.ceil(Math.sqrt(2*10));
        for (int i = 0; i < max2; i++){
            for(int j = 0; j < max2; j++) {
                if (smallMap.objectAt(new Vector2d(i, j)) instanceof Grass) grassCounter += 1;
            }
        }
        assertEquals(grassCounter, 2);

        grassCounter=0;
        int max3 = (int) Math.ceil(Math.sqrt(100*10));
        for (int i = 0; i < max3; i++){
            for(int j = 0; j < max3; j++) {
                if (bigMap.objectAt(new Vector2d(i, j)) instanceof Grass) grassCounter += 1;
            }
        }
        assertEquals(grassCounter, 100);
    }

    @Test
    public void isOccupiedTest() {
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
        assertTrue(defaultMap.canMoveTo(new Vector2d(-10, -10)));
    }

    @Test
    public void objectAtTest() {
        assertTrue(defaultMap.objectAt(v1) == null || defaultMap.objectAt(v1) instanceof Grass);
        defaultMap.mapOfAnimals.put(v1, a1);
        assertEquals(defaultMap.objectAt(v1), a1);
        defaultMap.mapOfAnimals.replace(v1, null);
    }

    @Test
    public void placeTest() {
        assertTrue(defaultMap.place(a1));
        assertThrows(IllegalArgumentException.class, () -> defaultMap.place(a1));
        Animal a3 = new Animal(defaultMap, new Vector2d(-1, -1));
        assertTrue(defaultMap.place(a3));
    }
}
