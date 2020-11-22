package agh.cs.lab1;

public class World {

    public static void main(String[] args) {
        //MoveDirection[] directions = new OptionsParser().parse(args);
        String[] moves = {"b", "b", "r", "f", "f", "f"};
        MoveDirection[] directions1 = new OptionsParser().parse(moves);
        AbstractWorldMap map = new GrassField(5);
        Vector2d[] positions = {new Vector2d(0, 0)};
        IEngine engine = new SimulationEngine(directions1, map, positions);
        System.out.println(map.toString());
        engine.run();
        System.out.println(map.toString());
    }
}