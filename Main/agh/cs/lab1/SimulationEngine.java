package agh.cs.lab1;

/*
W konstruktorze pojawia się pierwsza trawa
Następuje pętla, a w niej:
1.Wszystkie zwierzęta ruszają się i tracą energię
2.Następuje przejście po całej mapie:
    - sprawdzenie czy jakieś zwierzę umarło - jest usuwane z listy i hashmapy
    - sprawdzenie czy jakaś trawa i zwierzę jest na jednym polu - jeśli tak to trawa jest usuwana z hashmapy i zwierze odzyskuje energię
    - sprawdzenie czy kilka zwierząt nie stoi na jednej pozycji i czy mogą się rozmnażać - następuje stworzenie nowego zwierzęta
3. Rośnie nowa trawa
 */

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SimulationEngine implements IEngine {
    private TorusMap map;
    private int ages;
    private final MapVizualizerFX vizualizer;
    private final float appMapSize = 500;
    private final int appStatsSize = 300;

    public SimulationEngine(int numberOfAnimals, Stage stage, int startEnergy, int moveEnergy, int grassEnergy, Vector2d size, float jungleRatio, int ages) {
        this.map = new TorusMap(size, grassEnergy, jungleRatio);
        this.ages = ages;
        //jeśli zwierząt jest więcej niż miejsc na mapie, zmniejszam ilość zwierząt
        int torusMapSize = (size.x)*(size.y);
        if (numberOfAnimals > torusMapSize) numberOfAnimals = torusMapSize;

        for (int i = 0; i < numberOfAnimals; i++){
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy);
            // jeśli pozycja na mapie jest już zajęta, generuje mu nowy wektor
            while(this.map.getMapOfAnimals().get(newAnimal.getPosition()) != null)
                newAnimal.generateNewPosition();
            this.map.place(newAnimal);
        }
        int tileSize;
        if(size.x < size.y){
            tileSize = (int) Math.floor(appMapSize / size.y);
        }
        else tileSize = (int) Math.floor(appMapSize / size.x);
        this.vizualizer = new MapVizualizerFX(this.map, tileSize, size);

        stage.setScene(new Scene(vizualizer.getRoot(), appMapSize+appStatsSize, appMapSize));
        stage.show();

    }

    @Override
    public void run() {
        //System.out.println(this.map.toString());
        new Thread (() ->{
            while(true) {
                this.map.growGrass();
                this.vizualizer.outputMap();
                //poruszam zwierzętami z mapy
                ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
                for(Animal currentAnimal : listOfAnimals){
                    currentAnimal.move();
                }
                this.map.removeDeadAnimals();
                this.map.grassEating();
                this.map.reproduce();
                this.map.stats.countAverages(this.map.getListOfAnimals());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
         {
            //System.out.println(this.map.toString());
            //System.out.println(this.map.stats.toString());
        }
    }
}