package agh.cs.lab1;

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

        //pętla dodająca pierwsze zwierzęta na mapę
        for (int i = 0; i < numberOfAnimals; i++){
            Vector2d lowerLeft = new Vector2d(0, 0);
            Vector2d newPosition = lowerLeft.randomVector(size);
            // jeśli pozycja na mapie jest już zajęta, generuje nowy wektor
            while(this.map.isOccupied(newPosition))
                newPosition = lowerLeft.randomVector(size);
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy, null, null, newPosition, AnimalType.DEFAULT);
            this.map.place(newAnimal);
            newAnimal.addObserver(this);
            this.listOfAnimals.add(newAnimal);
            this.statistics.addToHashmap(newAnimal);
        }

        //tworzenie wizualizacji
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

    public Animal getSelectedAnimal() {
        return selectedAnimal;
    }

    public int getAgeFollowNumber() {
        return ageFollowNumber;
    }

    public int getAge() { return this.statistics.getAge();}

    public String getCurrentStatistics(){
        return this.statistics.toString();
    }

    public String getGeneralStatistics(){
        return this.statistics.getStatisticsOfAllTime();
    }

    @Override
    public void run() {
        //główna pętla obsługująca symulację
        new Thread (() ->{
            while(!this.ended) {
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
        //wszystkie następujące po sobie wydarzenia na mapie
        this.map.growGrass();
        for(Animal currentAnimal : this.listOfAnimals){
            currentAnimal.move();
        }
        this.removeDeadAnimals();
        this.map.grassEating();
        this.reproduce();
        this.statistics.countAverages(this.listOfAnimals);
    }

    private void reproduce(){
        //iteruję po wszystkich polach z mapy, na których znajdują się zwierzęta
        ArrayList<Animal> childrenToPlace = new ArrayList<>();
        for (Map.Entry<Vector2d, List<Animal>> vector2dListEntry : this.map.getMapOfAnimals().entrySet()) {
            List<Animal> currentList = vector2dListEntry.getValue();
            //wybieram tylko pola na których są conajmniej 2 zwierzęta
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
                        nearestPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).goBackToMap(this.map.getUpperRight());
                        if (!this.map.isOccupied(nearestPosition)) {
                            //wszystkie sąsiednie puste pola dodaję do listy
                            freeSpace.add(nearestPosition);
                        }
                        lookForFreeSpace = lookForFreeSpace.next();
                    }
                    //jeśli lista wolnych miejsc nie jest pusta, losuję z niej pozycję
                    Vector2d childPosition;
                    if (freeSpace.size() > 0)
                        childPosition = freeSpace.get(rand.nextInt(freeSpace.size()));
                    //jeśli nie, to losuje zajęte miejsce
                    else {
                        int spin = rand.nextInt(8);
                        for (int i = 0; i < spin; i++)
                            lookForFreeSpace = lookForFreeSpace.next();
                        childPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).goBackToMap(this.map.getUpperRight());
                    }
                    //tworzę nowe dziecko, przekazując mu rodziców i pozycję na mapie
                    Animal child = strongerParent.reproduce(weakerParent, childPosition);
                    childrenToPlace.add(child);
                }
            }
        }
        //gdy wszystkie dzieci zostają stworzone, dodaję je do mapy i statystyk
        for(Animal child : childrenToPlace) {
            this.map.place(child);
            child.addObserver(this);
            this.listOfAnimals.add(child);
            this.statistics.addToHashmap(child);
        }
    }

    @Override
    public void EnergyRunOut(Animal animal) {
        animal.setDeadAge(this.statistics.getAge());
        this.animalsToRemove.add(animal);
    }

    private void removeDeadAnimals() {
        //usuwam wszystkie martwe zwierzęta z symulacji
        for(Animal animal : this.animalsToRemove){
            this.listOfAnimals.remove(animal);
            this.statistics.removeFromHashmap(animal);
            animal = null;
        }
        this.animalsToRemove.clear();
    }

    protected void followAnimal(){
        //metoda rozpoczynąjąca proces śledzenia historii zwierzęcia
        TextInputDialog dialog = new TextInputDialog("100");
        dialog.setTitle("Age Number Dialog");
        dialog.setHeaderText("Enter a number of ages to follow the animal");
        dialog.setContentText("Please enter a number:");
        Optional<String> result = dialog.showAndWait();
        //zapisuje epoke do której śledzić zwierzę
        result.ifPresent(age -> this.ageFollowNumber = Integer.parseInt(age) + this.statistics.getAge());

        //ustawiam wszystkie typ wszystkich zwierząt na default,
        for(Animal animal : this.listOfAnimals){
            animal.setType(AnimalType.DEFAULT);
        }
        this.paused = false;
        this.selectedAnimal.setType(AnimalType.SELECTED);
        this.vizualizer.setFollowAnimalVisibility();
    }

    protected void selectAnimal(Vector2d position) {
        //metoda uruchamiana w momencie kliknięcia na konkretne zwierzę w wizualizacji
        Object animal = this.map.objectAt(position);
        if(this.paused && animal instanceof Animal){
            if(this.selectedAnimal != null)
                this.vizualizer.fillAnimalTile(this.selectedAnimal);
            //po wybraniu zwierzęcia przycisk śledzenia staje się widoczny
            this.vizualizer.setFollowAnimalVisibility();
            this.selectedAnimal = (Animal) animal;
            this.vizualizer.setTileColor(position, Color.MAGENTA);
            this.vizualizer.setAnimalStatistics("Selected Animal Genotype= \n" + ((Animal) animal).getGenes().toString());
        }
    }

    protected void selectStrongestGenes(){
        //metoda szukająca zwierząt o najsilniejszym genotypie
        for(Animal animal : this.listOfAnimals){
            if(animal.getGenes().equals(this.statistics.getCurrentStrongestGenotype())){
                Vector2d position = animal.getPosition();
                this.vizualizer.setTileColor(position, Color.YELLOW);
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
        this.paused = true;

        return "Statistics After Following:" +
                "\nGenotype= " + this.selectedAnimal.getGenes()+
                "\nAlive Children= " + countChildren +
                "\nAlive Descendants= " + countDescendants +
                "\nDied At= " + deadAt;
    }

    protected void saveAndExit() throws IOException {
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

    protected void exit() {
        this.stage.close();
        this.ended = true;
    }

    protected void pause() {
        this.paused = !this.paused;
    }
}