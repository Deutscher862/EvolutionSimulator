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
            //wczytywanie danych wejściowych
            JSONParser jsonParser = new JSONParser();
            JSONObject inputData = (JSONObject) jsonParser.parse(new FileReader("./src/Resources/InputData.json"));
            int startEnergy = Integer.parseInt(inputData.get("startEnergy").toString());
            int moveEnergy = Integer.parseInt(inputData.get("moveEnergy").toString());
            int grassEnergy  = Integer.parseInt(inputData.get("plantEnergy").toString());
            Vector2d mapSize = new Vector2d(Integer.parseInt(inputData.get("width").toString()), Integer.parseInt(inputData.get("height").toString()));
            float jungleRatio = Float.parseFloat(inputData.get("jungleRatio").toString());
            int numberOfAnimals = Integer.parseInt(inputData.get("numberOfAnimals").toString());
            int numberOfSimulations = Integer.parseInt(inputData.get("numberOfSimulations").toString());
            int numberOfAges = Integer.parseInt(inputData.get("numberOfAges").toString());
            int refreshTime = Integer.parseInt(inputData.get("refreshTime").toString());
            if(numberOfAnimals < 0 || startEnergy <=0 || moveEnergy <= 0 || grassEnergy <= 0 || mapSize.x <=0 || mapSize.y<=0 || jungleRatio < 0 || numberOfSimulations <0 || numberOfAges < 0)
                throw new IllegalArgumentException("Incorrect initial data");

            //uruchamianie symulacji
            for(int stageNumber = 0; stageNumber < numberOfSimulations; stageNumber++){
                Stage stage2 = new Stage();
                stage2.setX(100*stageNumber*0.5);
                stage2.setY(100*stageNumber*0.5);
                IEngine engine = new SimulationEngine(numberOfAnimals, stage2, stageNumber+1, startEnergy, moveEnergy, grassEnergy, mapSize, jungleRatio, numberOfAges, refreshTime);
                engine.run();
            }

        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}