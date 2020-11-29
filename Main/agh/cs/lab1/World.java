package agh.cs.lab1;

public class World {

    public static void main(String[] args) {
        try {
            //MoveDirection[] directions = new OptionsParser().parse(args);
            String[] moves = {"f" ,"b" ,"r", "r", "f", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f" ,"f", "f", "f"};
            MoveDirection[] directions = new OptionsParser().parse(moves);
            AbstractWorldMap map = new GrassField( 5);
            Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(0, 2)};
            IEngine engine = new SimulationEngine(directions, map, positions);
            System.out.println(map.toString());
            engine.run();
            System.out.println(map.toString());
        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}