package agh.cs.lab1;

public class SimulationEngine implements IEngine {
    private TorusMap map;
    private int ages;

    public SimulationEngine(int numberOfAnimals, int startEnergy, int moveEnergy, int grassEnergy, Vector2d upperRight, int numberOfGrass, float jungleRatio, int ages) {
        this.map = new TorusMap(upperRight, numberOfGrass, grassEnergy, jungleRatio);
        this.ages = ages;
        for (int i = 0; i < numberOfAnimals; i++){
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy);
            // a jeśli zwierze nie może zostać umieszczone na mapie?
            this.map.place(newAnimal);
        }

    }

    @Override
    public void run() {
        System.out.println(this.map.toString());
        for (int i = 0; i < this.ages; i++) {
            this.map.move();
            System.out.println(this.map.toString());
        }
    }
}