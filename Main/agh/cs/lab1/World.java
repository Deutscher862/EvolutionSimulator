package agh.cs.lab1;

import javafx.application.Application;
import javafx.stage.Stage;

public class World extends Application {

    public static void main(String[] args){ launch(args); }

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Generator ewolucyjny");
            int numberOfAnimals = 0;
            int startEnergy = 30;
            int moveEnergy = 1;
            int grassEnergy  =20;
            Vector2d upperRight = new Vector2d(50, 50);
            float jungleRatio = 0.1f;
            int ages = 1000;
            SimulationEngine engine = new SimulationEngine(numberOfAnimals, stage, startEnergy, moveEnergy, grassEnergy, upperRight, jungleRatio, ages);
            engine.run();

        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}