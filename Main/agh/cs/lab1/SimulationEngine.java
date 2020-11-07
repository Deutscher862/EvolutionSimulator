package agh.cs.lab1;

public class SimulationEngine implements IEngine {
    public MoveDirection[] moves;
    public IWorldMap map;

    public SimulationEngine(MoveDirection[] moves, IWorldMap map, Vector2d[] animalPositions) {
        this.moves = moves;
        this.map = map;
        for (Vector2d position : animalPositions) {
            map.place(new Animal(this.map, position));
        }
    }

    @Override
    public void run() {
        //korzystam ze stworzonej już metody run w klasie implementującej interfejs IWorldMap
        this.map.run(this.moves);
    }
}
