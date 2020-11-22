package agh.cs.lab1;

import java.util.ArrayList;

public class SimulationEngine implements IEngine {
    private MoveDirection[] moves;
    private IWorldMap map;
    private ArrayList<Animal> listOfAnimals = new ArrayList<>();

    public SimulationEngine(MoveDirection[] moves, IWorldMap map, Vector2d[] animalPositions) {
        this.moves = moves;
        this.map = map;
        for (Vector2d position : animalPositions) {
            Animal a = new Animal(this.map, position);
            if(this.map.place(a))
                this.listOfAnimals.add(a);
        }
    }

    @Override
    public void run() {
        int index = 0;
        for(MoveDirection direction : moves){
            Animal currentAnimal = listOfAnimals.get(index);
            currentAnimal.move(direction);
            System.out.println(direction +" " +index);
            System.out.println(this.map.toString());
            index += 1;
            if (index == listOfAnimals.size()) index =0;
        }
    }
}