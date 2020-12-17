package agh.cs.lab1;

import javafx.application.Application;
import javafx.stage.Stage;

public class World extends Application {

    public static void main(String[] args){ launch(args); }

    @Override
    public void start(Stage stage) {
        try {
            int numberOfAnimals = 100;
            int startEnergy = 30;
            int moveEnergy = 1;
            int grassEnergy  = 20;
            Vector2d upperRight = new Vector2d(50, 50);
            float jungleRatio = 0.3f;
            SimulationEngine engine = new SimulationEngine(numberOfAnimals, stage, startEnergy, moveEnergy, grassEnergy, upperRight, jungleRatio);
            engine.run();

        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}