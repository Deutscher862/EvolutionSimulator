package agh.cs.lab1;

import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Optional;

public class MapVizualizerFX {
    private final Tile[][] grid;
    private final Pane root;
    private final Vector2d size;
    private final TorusMap map;
    private Text mapStatistics;
    private Text animalStatistics;
    private Text generalStatistics;
    protected boolean paused = false;
    private boolean showGeneralStatistics = false;
    private Animal selectedAnimal = null;
    private final Button followAnimal;
    private final Button startStopButton;
    private int ageFollowNumber;

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d size, SimulationEngine engine) {
        this.root = new Pane();
        this.size = size;
        this.map = map;
        this.grid = new Tile[size.x][size.y];

        //tworzę siatkę do wyświetalania pól
        for(int x = 0; x < this.size.x; x++){
            for( int y = 0; y < this.size.y; y++){
                Tile tile;
                if (this.map.objectAt(new Vector2d(x, y)) instanceof Animal)
                    this.grid[x][y] = new Tile(tileSize, new Vector2d(x, y), Color.BLACK, this);
                else this.grid[x][y] = new Tile(tileSize, new Vector2d(x, y), Color.LIGHTGREEN, this);
                this.root.getChildren().add(this.grid[x][y]);
            }
        }
        //aktulane statystyki mapy
        this.mapStatistics = new Text();
        this.mapStatistics.setWrappingWidth(200);
        this.mapStatistics.setTranslateX(850);
        this.mapStatistics.setTranslateY((30));
        this.mapStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.mapStatistics);

        //statystyki poszczególnego zwierzęcia
        this.animalStatistics = new Text();
        this.animalStatistics.setWrappingWidth(200);
        this.animalStatistics.setTranslateX(850);
        this.animalStatistics.setTranslateY((340));
        this.animalStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.animalStatistics);

        //ogólne statystyki mapy
        this.generalStatistics = new Text();
        this.generalStatistics.setWrappingWidth(200);
        this.generalStatistics.setTranslateX(850);
        this.generalStatistics.setTranslateY((540));
        this.generalStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.generalStatistics);

        //przycisk pauzy
        this.startStopButton = new Button("Start/Stop");
        this.startStopButton.setTranslateX(1000);
        this.startStopButton.setTranslateY(750);
        this.startStopButton.setMinSize(100, 50);
        this.startStopButton.setOnAction(event -> this.paused = !this.paused);
        this.root.getChildren().add(this.startStopButton);

        //przycisk śledzenia zwierzęcia
        this.followAnimal = new Button("Follow Selected Animal");
        this.followAnimal.setTranslateX(1000);
        this.followAnimal.setTranslateY(680);
        this.followAnimal.setMinSize(100, 50);
        this.followAnimal.setVisible(false);
        this.followAnimal.setOnAction(event -> followAnimal());
        this.root.getChildren().add(followAnimal);

        //przycisk wyświetlenia najsilniejszych zwierząt
        Button selectStrongestGenotypeAnimals = new Button("Show Strongest Animals");
        selectStrongestGenotypeAnimals.setTranslateX(850);
        selectStrongestGenotypeAnimals.setTranslateY(680);
        selectStrongestGenotypeAnimals.setMinSize(100, 50);
        selectStrongestGenotypeAnimals.setOnAction(event ->selectStrongestGenes());
        this.root.getChildren().add(selectStrongestGenotypeAnimals);

        //przycisk do wyświetlania ogólnych statystyk
        Button showGeneralStatistics = new Button("Show General Statistics");
        showGeneralStatistics.setTranslateX(850);
        showGeneralStatistics.setTranslateY(750);
        showGeneralStatistics.setMinSize(100, 50);
        showGeneralStatistics.setOnAction(event -> {
            if(this.showGeneralStatistics){
                this.showGeneralStatistics = false;
                showGeneralStatistics.setText("Show General Statistics");
                this.generalStatistics.setText("");
            }
            else{
                this.showGeneralStatistics = true;
                showGeneralStatistics.setText("Hide General Statistics");
                this.generalStatistics.setText(this.map.stats.getStatisticsOfAllTime());
            }
        });
        this.root.getChildren().add(showGeneralStatistics);
    }

    public void drawScene(){
        for(int i = 0; i < this.size.x; i++){
            for(int j = 0; j < this.size.y; j++){
                Vector2d position = new Vector2d(i, j);
                Object object = this.map.objectAt(position);
                this.grid[i][j].setColor(Color.LIGHTGREEN);
                if(object instanceof Animal)
                    fillAnimalTile((Animal) object);
                else if(object instanceof Grass)
                    this.grid[i][j].setColor(Color.GREEN);
            }
        }
        this.followAnimal.setVisible(false);
        this.mapStatistics.setText(this.map.stats.toString());
        if(this.selectedAnimal == null || this.selectedAnimal.getType() != AnimalType.SELECTED)
            this.animalStatistics.setText("");
        if(showGeneralStatistics){
            this.generalStatistics.setText(this.map.stats.getStatisticsOfAllTime());
        }
        if(this.map.stats.getAge() == this.ageFollowNumber) countSelectedAnimalStatistics();
    }

    public void fillAnimalTile(Animal animal){
        Vector2d position = animal.getPosition();
        int animalStartEnergy = animal.getStartEnergy();
        int animalEnergy = animal.getEnergy();
        if (animalEnergy >= animalStartEnergy*3/4)
            grid[position.x][position.y].setColor(Color.BLACK);
        else if(animalEnergy >= animalStartEnergy/2)
            grid[position.x][position.y].setColor(Color.BROWN);
        else if(animalEnergy >= animalStartEnergy/4)
            grid[position.x][position.y].setColor(Color.GRAY);
        else grid[position.x][position.y].setColor(Color.LIGHTGRAY);
    }

    public Pane getRoot() {
        return root;
    }

    public void selectStrongestGenes(){
        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            if(animal.getGenes().equals(this.map.stats.getCurrentStrongestGenotype())){
                Vector2d position = animal.getPosition();
                grid[position.x][position.y].setColor(Color.YELLOW);
            }
        }
    }

    public void selectAnimal(Vector2d position) {
        Object animal = this.map.objectAt(position);
        if(this.paused && animal instanceof Animal){
            if(this.selectedAnimal != null)
                fillAnimalTile(this.selectedAnimal);
            this.followAnimal.setVisible(true);
            this.selectedAnimal = (Animal) animal;
            grid[position.x][position.y].setColor(Color.MAGENTA);
            this.animalStatistics.setText("Selected Animal Genotype= \n" + ((Animal) animal).getGenes().toString());
        }
    }

    private void countSelectedAnimalStatistics() {
        int countChildren = 0;
        int countDescendants = 0;
        String deadAt;
        if(this.selectedAnimal.getDeadAge() == 0) deadAt = "-";
        else deadAt = String.valueOf(this.selectedAnimal.getDeadAge());

        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            if (animal.getType() == AnimalType.CHILD) {
                countChildren += 1;
                countDescendants += 1;
            }
            else if (animal.getType() == AnimalType.DESCENDANT) countDescendants += 1;
        }
        this.paused = true;

        this.animalStatistics.setText( "Statistics After Following:" +
                "\nGenotype= " + this.selectedAnimal.getGenes()+
                "\nAlive Children= " + countChildren +
                "\nAlive Descendants= " + countDescendants +
                "\nDied At= " + this.selectedAnimal.getDeadAge());
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
        this.selectedAnimal.type = AnimalType.SELECTED;
        this.followAnimal.setVisible(false);
    }
}