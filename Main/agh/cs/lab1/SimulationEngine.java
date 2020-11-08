package agh.cs.lab1;

import java.util.Arrays;

public class SimulationEngine implements IEngine {
    public MoveDirection[] moves;
    public IWorldMap map;
    public int numberOfAnimals;
    public MoveDirection[] shortListOfMoves;

    public SimulationEngine(MoveDirection[] moves, IWorldMap map, Vector2d[] animalPositions) {
        this.moves = moves;
        this.map = map;
        this.numberOfAnimals = animalPositions.length;
        for (Vector2d position : animalPositions) {
            map.place(new Animal(this.map, position));
        }
    }

    @Override
    public void run() {
        // pętla for wykorzystuje mniejszą tablice MoveDirection[] o długości równej ilości zwierząt na mapie
        // lub mniejszej, gdy jest mniej ruchów, a następnie przesyła je do metody run
        for (int i = 0; i < this.moves.length; i += this.numberOfAnimals) {
            if (i + numberOfAnimals <= this.moves.length)
                this.shortListOfMoves = Arrays.copyOfRange(this.moves, i, i + this.numberOfAnimals);
            else this.shortListOfMoves = Arrays.copyOfRange(this.moves, i, this.moves.length);

            this.map.run(this.shortListOfMoves);
        }
    }
}