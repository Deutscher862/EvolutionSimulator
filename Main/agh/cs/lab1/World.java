package agh.cs.lab1;

import javafx.application.Application;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class World extends Application {

    public static void main(String[] args){ launch(args); }

    @Override
    public void start(Stage stage) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject inputData = (JSONObject) jsonParser.parse(new FileReader("./src/Resources/InputData.json"));
            int numberOfAnimals = 50;
            int startEnergy = Integer.parseInt(inputData.get("startEnergy").toString());
            int moveEnergy = Integer.parseInt(inputData.get("moveEnergy").toString());
            int grassEnergy  = Integer.parseInt(inputData.get("plantEnergy").toString());
            Vector2d mapSize = new Vector2d(Integer.parseInt(inputData.get("width").toString()), Integer.parseInt(inputData.get("height").toString()));
            double jungleRatio = Double.parseDouble(inputData.get("jungleRatio").toString());
            int numberOfSimulations = 1;
            for(int i = 0; i <numberOfSimulations; i++){
                Stage stage2 = new Stage();
                IEngine engine = new SimulationEngine(numberOfAnimals, stage2, i, startEnergy, moveEnergy, grassEnergy, mapSize, jungleRatio);
                engine.run();
            }

        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}