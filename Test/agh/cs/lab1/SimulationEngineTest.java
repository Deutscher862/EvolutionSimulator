package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimulationEngineTest {
    RectangularMap largeMap = new RectangularMap(10, 5);
    String[] moves = {"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
    MoveDirection[] directions = new OptionsParser().parse(moves);

    Vector2d v1 = new Vector2d(2, 2);
    Vector2d v2 = new Vector2d(3, 4);

    Vector2d[] positions = {v1, v2};

    IEngine engine = new SimulationEngine(directions, largeMap, positions);
    Animal a1 = (Animal) largeMap.objectAt(v1);
    Animal a2 = (Animal) largeMap.objectAt(v2);

    @Test
    public void runTest() {
        engine.run();

        assertEquals(largeMap.objectAt(new Vector2d(2, 0)), a1);
        assertEquals(largeMap.objectAt(new Vector2d(3, 5)), a2);
        assertEquals(largeMap.objectAt(v1), null);
        assertEquals(largeMap.objectAt(v2), null);
    }

}
