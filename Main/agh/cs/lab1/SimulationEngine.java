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
    private final MapVizualizerFX vizualizer;
    private final float appMapSize = 800;
    private final int appStatsSize = 400;
    private boolean isOn = true;

    public SimulationEngine(int numberOfAnimals, Stage stage, int startEnergy, int moveEnergy, int grassEnergy, Vector2d size, float jungleRatio) {
        this.map = new TorusMap(size, grassEnergy, jungleRatio);
        //jeśli zwierząt jest więcej niż miejsc na mapie, zmniejszam ilość zwierząt
        int torusMapSize = (size.x)*(size.y);
        if (numberOfAnimals > torusMapSize) numberOfAnimals = torusMapSize;

        for (int i = 0; i < numberOfAnimals; i++){
            Vector2d lowerLeft = new Vector2d(0, 0);
            Vector2d newPosition = lowerLeft.randomVector(size);
            // jeśli pozycja na mapie jest już zajęta, generuje mu nowy wektor
            while(this.map.isOccupied(newPosition))
                newPosition = lowerLeft.randomVector(size);
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy, null, null, newPosition, AnimalType.DEFAULT);
            this.map.place(newAnimal);
        }
        int tileSize;
        if(size.x < size.y){
            tileSize = (int) Math.floor(appMapSize / size.y);
        }
        else tileSize = (int) Math.floor(appMapSize / size.x);
        this.vizualizer = new MapVizualizerFX(this.map, tileSize, size, this);
        stage.setTitle("Evolution Generator");
        stage.setScene(new Scene(vizualizer.getRoot(), appMapSize+appStatsSize+20, appMapSize + 20));
        stage.show();
    }

    @Override
    public void run() {
        new Thread (() ->{
            //this.vizualizer.refresh();
            while(!this.vizualizer.stop) {
                newDay();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.vizualizer.drawScene();
                while(this.vizualizer.stop)
                    Thread.onSpinWait();
            }
        }).start();
    }

    private void newDay(){
        this.map.growGrass();
        //poruszam zwierzętami z mapy
        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal currentAnimal : listOfAnimals){
            currentAnimal.move();
        }
        this.map.removeDeadAnimals();
        this.map.grassEating();
        this.map.reproduce();
        this.map.stats.countAverages(this.map.getListOfAnimals());
    }
}