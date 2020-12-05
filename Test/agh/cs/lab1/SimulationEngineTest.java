package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimulationEngineTest {
    /*
    RectangularMap largeMap = new RectangularMap(10, 5);
    GrassField grassMap = new GrassField(20);
    GrassField grassMap2 = new GrassField(5);

    String[] moves1 = {"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
    String[] moves2 = {"f", "b", "f", "b", "r", "r", "l", "f", "l", "b", "f", "r", "b", "f", "f"};
    String[] moves3 = {"f", "b", "r", "f", "f", "l", "b"};

    OptionsParser parser = new OptionsParser();
    MoveDirection[] directions1 = parser.parse(moves1);
    MoveDirection[] directions2 = parser.parse(moves2);
    MoveDirection[] directions3 = parser.parse(moves3);

    Vector2d v1 = new Vector2d(2, 2);
    Vector2d v2 = new Vector2d(3, 4);
    Vector2d v3 = new Vector2d(5, 5);
    Vector2d v4 = new Vector2d(-2, -5);
    Vector2d v5 = new Vector2d(0, 0);

    Vector2d[] positions1 = {v1, v2};
    Vector2d[] positions2 = {v1, v2, v3};
    Vector2d[] positions3 = {v4, v5};

    IEngine engine1 = new SimulationEngine(directions1, largeMap, positions1);
    Animal a1 = (Animal) largeMap.objectAt(v1);
    Animal a2 = (Animal) largeMap.objectAt(v2);

    IEngine engine2 = new SimulationEngine(directions2, grassMap, positions2);
    Animal a3 = (Animal) grassMap.objectAt(v1);
    Animal a4 = (Animal) grassMap.objectAt(v2);
    Animal a5 = (Animal) grassMap.objectAt(v3);

    IEngine engine3 = new SimulationEngine(directions3, grassMap2, positions3);
    Animal a6 = (Animal) grassMap2.objectAt(v4);
    Animal a7 = (Animal) grassMap2.objectAt(v5);

    @Test
    public void runTest() {
        engine1.run();
        assertEquals(largeMap.objectAt(new Vector2d(2, 0)), a1);
        assertEquals(largeMap.objectAt(new Vector2d(3, 4)), a2);
        assertEquals(largeMap.objectAt(v1), null);

        engine2.run();
        assertEquals(grassMap.objectAt(new Vector2d(4, 2)), a3);
        assertEquals(grassMap.objectAt(new Vector2d(6, 3)), a4);
        assertEquals(grassMap.objectAt(new Vector2d(6, 6)), a5);

        engine3.run();
        assertEquals(grassMap2.objectAt(new Vector2d(-2, -4)), a6);
        assertEquals(grassMap2.objectAt(v5), a7);
    }
    */
}
