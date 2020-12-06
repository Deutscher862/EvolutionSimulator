package agh.cs.lab1;

public class World {

    public static void main(String[] args) {
        try {
            int numberOfAnimals = 1;
            int startEnergy = 30;
            int moveEnergy = 1;
            int grassEnergy  =4;
            Vector2d upperRight = new Vector2d(4, 4);
            int numberOfGrass = 10;
            float jungleRatio = 0.3f;
            int ages = 1000;
            SimulationEngine engine = new SimulationEngine(numberOfAnimals, startEnergy, moveEnergy, grassEnergy, upperRight, numberOfGrass, jungleRatio, ages);

            engine.run();
        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}