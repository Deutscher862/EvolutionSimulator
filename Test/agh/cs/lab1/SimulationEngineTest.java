package agh.cs.lab1;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SimulationEngineTest {
    RectangularMap largeMap = new RectangularMap(10, 5);
    String[] moves = {"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
    MoveDirection[] directions = new OptionsParser().parse(moves);

    Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(3, 4)};

    IEngine engine = new SimulationEngine(directions, largeMap, positions);

    @Test
    public void runTest() {
        engine.run();
        assertTrue(largeMap.isOccupied(new Vector2d(3, 5)));
        assertTrue(largeMap.isOccupied(new Vector2d(2, 0)));
        assertFalse(largeMap.isOccupied(new Vector2d(2, 2)));
        assertFalse(largeMap.isOccupied(new Vector2d(3, 4)));
    }

}
