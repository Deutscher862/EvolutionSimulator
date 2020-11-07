package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnimalTest {
    RectangularMap map = new RectangularMap(4, 4);
    Animal cat = new Animal(map, new Vector2d(2, 2));
    Animal dog = new Animal(map, new Vector2d(0, 0));
    Animal goat = new Animal(map, new Vector2d(4, 4));
    Animal duck = new Animal(map, new Vector2d(4, 0));
    Animal cow = new Animal(map, new Vector2d(0, 4));


    MoveDirection f = MoveDirection.FORWARD;
    MoveDirection b = MoveDirection.BACKWARD;
    MoveDirection l = MoveDirection.LEFT;
    MoveDirection r = MoveDirection.RIGHT;

    @Test
    public void orientationTest() {
        assertEquals(cat.getOrientation(), MapDirection.NORTH);
        cat.move(r);
        assertEquals(cat.getOrientation(), MapDirection.EAST);
        cat.move(r);
        assertEquals(cat.getOrientation(), MapDirection.SOUTH);
        cat.move(r);
        assertEquals(cat.getOrientation(), MapDirection.WEST);
        cat.move(r);
    }

    @Test
    public void moveTest() {
        cat.move(f);
        assertEquals(cat.getPosition(), new Vector2d(2, 3));
        cat.move(b);
        assertEquals(cat.getPosition(), new Vector2d(2, 2));

        dog.move(b);
        assertEquals(dog.getPosition(), new Vector2d(0, 0));
        dog.move(l);
        dog.move(f);
        assertEquals(dog.getPosition(), new Vector2d(0, 0));
        dog.move(l);
        dog.move(f);
        assertEquals(dog.getPosition(), new Vector2d(0, 0));
        dog.move(l);
        dog.move(f);
        assertEquals(dog.getPosition(), new Vector2d(1, 0));

        goat.move(f);
        assertEquals(goat.getPosition(), new Vector2d(4, 4));
        goat.move(r);
        goat.move(f);
        assertEquals(goat.getPosition(), new Vector2d(4, 4));
        goat.move(r);
        goat.move(f);
        assertEquals(goat.getPosition(), new Vector2d(4, 3));

        duck.move(f);
        assertEquals(duck.getPosition(), new Vector2d(4, 1));
        duck.move(r);
        duck.move(f);
        assertEquals(duck.getPosition(), new Vector2d(4, 1));

        cow.move(f);
        assertEquals(cow.getPosition(), new Vector2d(0, 4));
        cow.move(b);
        assertEquals(cow.getPosition(), new Vector2d(0, 3));
        cow.move(l);
        cow.move(f);
        assertEquals(cow.getPosition(), new Vector2d(0, 3));
    }


}