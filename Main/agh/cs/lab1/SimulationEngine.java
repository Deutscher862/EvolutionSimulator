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
import java.util.*;

public class SimulationEngine implements IEngine, IEnergyRunOutObserver {
    private final TorusMap map;
    private final Stage stage;
    private final int stageNumber;
    private final Statistics statistics;
    private final ArrayList<Animal> listOfAnimals = new ArrayList<>();
    private final ArrayList<Animal> animalsToRemove = new ArrayList<>();
    private final Random rand = new Random();
    private int age = 0;
    private final MapVizualizerFX vizualizer;
    private final float appMapSize = 800;
    private final int appStatsSize = 500;
    private int ageFollowNumber;
    private boolean paused = false;
    private boolean ended = false;
    private Animal selectedAnimal = null;

    public SimulationEngine(int numberOfAnimals, Stage stage, int stageNumber,  int startEnergy, int moveEnergy, int grassEnergy, Vector2d size, double jungleRatio) {
        this.map = new TorusMap(size, grassEnergy, jungleRatio);
        this.stage = stage;
        this.stageNumber = stageNumber;
        this.statistics = new Statistics(this.map);
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
            newAnimal.addObserver(this);
            this.listOfAnimals.add(newAnimal);
            this.statistics.addToHashmap(newAnimal);
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
            while(!this.paused && !this.ended) {
                this.age += 1;
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
        for(Animal currentAnimal : this.listOfAnimals){
            currentAnimal.move();
        }
        this.removeDeadAnimals();
        this.map.grassEating();
        this.reproduce();
        this.statistics.countAverages(this.listOfAnimals);
    }

    public void reproduce(){
        //iteruję po wszystkich polach z mapy, na których znajdują się zwierzęta
        ArrayList<Animal> childrenToPlace = new ArrayList<>();
        for (Map.Entry<Vector2d, List<Animal>> vector2dListEntry : this.map.getMapOfAnimals().entrySet()) {
            List<Animal> currentList = vector2dListEntry.getValue();
            if (currentList.size() >= 2) {
                Animal strongerParent;
                Animal weakerParent;
                //wybieranie rodziców
                int j = 1;
                while (j < currentList.size() && currentList.get(j).getEnergy() == currentList.get(0).getEnergy())
                    j += 1;
                if (j == 1) {
                    strongerParent = currentList.get(0);
                    int k = 2;
                    while (k < currentList.size() && currentList.get(k).getEnergy() == currentList.get(1).getEnergy())
                        k += 1;
                    if (k == 2) weakerParent = currentList.get(1);
                    else {
                        weakerParent = currentList.get(rand.nextInt(k - 1) + 1);
                    }
                } else {
                    strongerParent = currentList.get(rand.nextInt(j));
                    weakerParent = currentList.get(rand.nextInt(j));
                    while (strongerParent.equals(weakerParent))
                        weakerParent = currentList.get(rand.nextInt(j));
                }

                //sprawdzam czy zwierzęta mają wystarczająco dużo energii do rozmnażania
                if (strongerParent.getEnergy() > strongerParent.getStartEnergy() / 2 && weakerParent.getEnergy() > weakerParent.getStartEnergy() / 2) {
                    //szukam czy dookoła rodziców jest jakieś wolne pole
                    ArrayList<Vector2d> freeSpace = new ArrayList<>();
                    Vector2d nearestPosition;
                    MapDirection lookForFreeSpace = strongerParent.getOrientation();
                    for (int i = 0; i < 8; i++) {
                        nearestPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).getBackToMap(this.map.getUpperRight());
                        if (!this.map.isOccupied(nearestPosition)) {
                            freeSpace.add(nearestPosition);
                        }
                        lookForFreeSpace = lookForFreeSpace.next();
                    }
                    //jeśli istnieje jakieś puste miejsce, to je losuję i dodaję do metody reproduce
                    Vector2d childPosition;
                    if (freeSpace.size() > 0)
                        childPosition = freeSpace.get(rand.nextInt(freeSpace.size()));
                        //jeśli nie, to losuje zajęte miejsce
                    else {
                        int spin = rand.nextInt(8);
                        for (int i = 0; i < spin; i++)
                            lookForFreeSpace = lookForFreeSpace.next();
                        childPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).getBackToMap(this.map.getUpperRight());
                    }
                    Animal child = strongerParent.reproduce(weakerParent, childPosition);
                    childrenToPlace.add(child);
                }
            }
        }
        //gdy wszystkie dzieci zostają stworzone, dodaję je do mapy
        for(Animal child : childrenToPlace) {
            this.map.place(child);
            child.addObserver(this);
            this.listOfAnimals.add(child);
            this.statistics.addToHashmap(child);
        }
    }

    @Override
    public void EnergyRunOut(Animal animal) {
        animal.deadAge = this.statistics.getAge();
        this.animalsToRemove.add(animal);
    }

    public void removeDeadAnimals() {
        //usuwam wszystkie martwe zwierzęta z symulacji
        for(Animal animal : this.animalsToRemove){
            this.listOfAnimals.remove(animal);
            this.statistics.removeFromHashmap(animal);
            animal = null;
        }
        this.animalsToRemove.clear();
    }

    public Animal getSelectedAnimal() {
        return selectedAnimal;
    }

    public int getAgeFollowNumber() {
        return ageFollowNumber;
    }

    public int getAge() {
        return age;
    }

    public void followAnimal(){
        TextInputDialog dialog = new TextInputDialog("100");
        dialog.setTitle("Age Number Dialog");
        dialog.setHeaderText("Enter a number of ages to follow the animal");
        dialog.setContentText("Please enter a number:");
        Optional<String> result = dialog.showAndWait();
        //zapisuje epoke do której śledzić zwierzę
        result.ifPresent(age -> this.ageFollowNumber = Integer.parseInt(age) + this.statistics.getAge());

        //ustawiam wszystkie zwierzęta na default
        for(Animal animal : this.listOfAnimals){
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
        //metoda szukająca zwierząt o najsilniejszym genotypie
        for(Animal animal : this.listOfAnimals){
            if(animal.getGenes().equals(this.statistics.getCurrentStrongestGenotype())){
                Vector2d position = animal.getPosition();
                this.vizualizer.grid[position.x][position.y].setColor(Color.YELLOW);
            }
        }
    }

    public String countSelectedAnimalStatistics() {
        //metoda licząca statystyki po skończeniu śledzenia zwierzęcia
        int countChildren = 0;
        int countDescendants = 0;
        String deadAt;
        if(this.selectedAnimal.getDeadAge() == -1) deadAt = "-";
        else deadAt = String.valueOf(this.selectedAnimal.getDeadAge());

        for(Animal animal : this.listOfAnimals){
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

    public String getCurrentStatistics(){
        return this.statistics.toString();
    }

    public String getGeneralStatistics(){
        return this.statistics.getStatisticsOfAllTime();
    }

    public void saveAndExit() throws IOException {
        try{
            FileWriter writer = new FileWriter("SimulationOutput" + this.stageNumber + ".txt");
            writer.write(this.statistics.getStatisticsOfAllTime());
            writer.close();
            this.stage.close();
            this.ended = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        this.stage.close();
        this.ended = true;
    }

    public void pause() {
        this.paused = !this.paused;
    }
}