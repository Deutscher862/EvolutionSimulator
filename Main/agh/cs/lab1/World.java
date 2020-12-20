package agh.cs.lab1;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Optional;

public class World extends Application {

    public static void main(String[] args){ launch(args); }

    @Override
    public void start(Stage stage) {
        try {
            //wczytywanie danych wejściowych
            JSONParser jsonParser = new JSONParser();
            JSONObject inputData = (JSONObject) jsonParser.parse(new FileReader("./src/Resources/InputData.json"));
            int numberOfAnimals = Integer.parseInt(inputData.get("numberOfAnimals").toString());
            int startEnergy = Integer.parseInt(inputData.get("startEnergy").toString());
            int moveEnergy = Integer.parseInt(inputData.get("moveEnergy").toString());
            int grassEnergy  = Integer.parseInt(inputData.get("plantEnergy").toString());
            Vector2d mapSize = new Vector2d(Integer.parseInt(inputData.get("width").toString()), Integer.parseInt(inputData.get("height").toString()));
            double jungleRatio = Double.parseDouble(inputData.get("jungleRatio").toString());
            if(numberOfAnimals < 0 || startEnergy <=0 || moveEnergy <= 0 || grassEnergy <= 0 || mapSize.x <=0 || mapSize.y<=0 || jungleRatio < 0)
                throw new IllegalArgumentException("Incorrect initial data");

            //wybieranie ilości symulacji
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Simulation Number");
            alert.setHeaderText("Choose the number of simultaneous simulations.");
            alert.setContentText("Choose your option.");

            ButtonType buttonOne = new ButtonType("One");
            ButtonType buttonTwo = new ButtonType("Two");
            ButtonType buttonCancel = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonOne, buttonTwo, buttonCancel);
            Optional<ButtonType> result = alert.showAndWait();

            int numberOfSimulations = 0;
            if (result.get() == buttonOne){
                numberOfSimulations = 1;
            } else if (result.get() == buttonTwo) {
                numberOfSimulations = 2;
            }

            //uruchamianie symulacji
            for(int stageNumber = 0; stageNumber < numberOfSimulations; stageNumber++){
                Stage stage2 = new Stage();
                stage2.setX(100*stageNumber*0.5);
                stage2.setY(100*stageNumber*0.5);
                IEngine engine = new SimulationEngine(numberOfAnimals, stage2, stageNumber+1, startEnergy, moveEnergy, grassEnergy, mapSize, jungleRatio);
                engine.run();
            }

        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}