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
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class SimulationEngine implements IEngine {
    private final TorusMap map;
    private final Stage stage;
    private final MapVizualizerFX vizualizer;
    private final float appMapSize = 800;
    private final int appStatsSize = 500;
    private int ageFollowNumber;
    protected boolean paused = false;
    private Animal selectedAnimal = null;

    public SimulationEngine(int numberOfAnimals, Stage stage, int startEnergy, int moveEnergy, int grassEnergy, Vector2d size, double jungleRatio) {
        this.map = new TorusMap(size, grassEnergy, jungleRatio);
        this.stage  =stage;
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
            while(!this.paused) {
                newDay();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.vizualizer.drawScene();
                while(this.paused)
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
        this.map.stats.countAverages();
    }

    public Animal getSelectedAnimal() {
        return selectedAnimal;
    }

    public int getAgeFollowNumber() {
        return ageFollowNumber;
    }

    public void followAnimal(){
        TextInputDialog dialog = new TextInputDialog("100");
        dialog.setTitle("Age Number Dialog");
        dialog.setHeaderText("Enter a number of ages to follow the animal");
        dialog.setContentText("Please enter a number:");
        Optional<String> result = dialog.showAndWait();
        //zapisuje epoke do której śledzić zwierzę
        result.ifPresent(age -> this.ageFollowNumber = Integer.parseInt(age) + this.map.stats.getAge());

        //ustawiam wszystkie zwierzęta na default
        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            animal.type = AnimalType.DEFAULT;
        }
        this.paused = false;
        this.selectedAnimal.type = AnimalType.SELECTED;
        this.vizualizer.followAnimal.setVisible(false);
    }

    public void selectAnimal(Vector2d position) {
        Object animal = this.map.objectAt(position);
        if(this.paused && animal instanceof Animal){
            if(this.selectedAnimal != null)
                this.vizualizer.fillAnimalTile(this.selectedAnimal);
            this.vizualizer.followAnimal.setVisible(true);
            this.selectedAnimal = (Animal) animal;
            this.vizualizer.grid[position.x][position.y].setColor(Color.MAGENTA);
            this.vizualizer.animalStatistics.setText("Selected Animal Genotype= \n" + ((Animal) animal).getGenes().toString());
        }
    }

    public void selectStrongestGenes(){
        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            if(animal.getGenes().equals(this.map.stats.getCurrentStrongestGenotype())){
                Vector2d position = animal.getPosition();
                this.vizualizer.grid[position.x][position.y].setColor(Color.YELLOW);
            }
        }
    }

    public String countSelectedAnimalStatistics() {
        int countChildren = 0;
        int countDescendants = 0;
        String deadAt;
        if(this.selectedAnimal.getDeadAge() == -1) deadAt = "-";
        else deadAt = String.valueOf(this.selectedAnimal.getDeadAge());

        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            if (animal.getType() == AnimalType.CHILD) {
                countChildren += 1;
                countDescendants += 1;
            }
            else if (animal.getType() == AnimalType.DESCENDANT) countDescendants += 1;
        }
        this.selectedAnimal.type = AnimalType.DEFAULT;
        this.paused = true;

        return "Statistics After Following:" +
                "\nGenotype= " + this.selectedAnimal.getGenes()+
                "\nAlive Children= " + countChildren +
                "\nAlive Descendants= " + countDescendants +
                "\nDied At= " + deadAt;
    }

    public void saveAndExit() throws IOException {
        try{
            FileWriter writer = new FileWriter("SimulationOutput.txt");
            writer.write(this.map.stats.getStatisticsOfAllTime());
            writer.close();
            this.stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exit() {
        this.stage.close();
    }
}