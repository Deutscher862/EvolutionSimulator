package agh.cs.lab1;

public class World {

    public static void main(String[] args) {
        try {
            int numberOfAnimals = 10;
            int startEnergy = 30;
            int moveEnergy = 1;
            int grassEnergy  =2;
            Vector2d upperRight = new Vector2d(2, 2);
            float jungleRatio = 0.3f;
            int ages = 10;
            SimulationEngine engine = new SimulationEngine(numberOfAnimals, startEnergy, moveEnergy, grassEnergy, upperRight, jungleRatio, ages);

            engine.run();
        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}