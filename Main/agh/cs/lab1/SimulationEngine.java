package agh.cs.lab1;

public class SimulationEngine implements IEngine {
    private TorusMap map;
    private int ages;

    public SimulationEngine(int numberOfAnimals, int startEnergy, int moveEnergy, int grassEnergy, Vector2d upperRight, int numberOfGrass, float jungleRatio, int ages) {
        this.map = new TorusMap(upperRight, numberOfGrass, grassEnergy, jungleRatio);
        this.ages = ages;
        boolean added = false;
        for (int i = 0; i < numberOfAnimals; i++){
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy, i);
            // jeśli zwierze nie może zostać umieszczone na mapie generuje mu nowy wektor
            while(!added){
                if(this.map.place(newAnimal)) added = true;
                else newAnimal.generateNewPosition();
            }
            added = false;
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