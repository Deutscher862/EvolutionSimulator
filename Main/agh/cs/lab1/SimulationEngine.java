package agh.cs.lab1;

public class SimulationEngine implements IEngine {
    private TorusMap map;

    public SimulationEngine(int numberOfAnimals, int startEnergy, int moveEnergy, int grassEnergy, int numberOfPeriods, Vector2d upperRight, int numberOfGrass, float jungleRatio) {
        this.map = new TorusMap(upperRight, numberOfGrass, grassEnergy, jungleRatio);
        for (int i = 0; i < numberOfAnimals; i++){
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy);
            // a jeśli zwierze nie może zostać umieszczone na mapie?
            this.map.place(newAnimal);
        }

    }

    @Override
    public void run() {
        int index = 0;
    }
}