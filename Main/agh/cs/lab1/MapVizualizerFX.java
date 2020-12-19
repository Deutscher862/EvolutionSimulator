package agh.cs.lab1;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

public class MapVizualizerFX {
    protected final Tile[][] grid;
    private final Pane root;
    private final Vector2d size;
    private final TorusMap map;
    private Text mapStatistics;
    protected Text animalStatistics;
    private Text generalStatistics;
    private boolean showGeneralStatistics = false;
    protected final Button followAnimal;
    private final Button startStopButton;
    protected final SimulationEngine engine;

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d size, SimulationEngine engine) {
        this.root = new Pane();
        this.size = size;
        this.map = map;
        this.grid = new Tile[size.x][size.y];
        this.engine = engine;

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
        this.mapStatistics.setTranslateX(820);
        this.mapStatistics.setTranslateY((30));
        this.mapStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.mapStatistics);

        //statystyki poszczególnego zwierzęcia
        this.animalStatistics = new Text();
        this.animalStatistics.setWrappingWidth(200);
        this.animalStatistics.setTranslateX(820);
        this.animalStatistics.setTranslateY((340));
        this.animalStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.animalStatistics);

        //ogólne statystyki mapy
        this.generalStatistics = new Text();
        this.generalStatistics.setWrappingWidth(200);
        this.generalStatistics.setTranslateX(1070);
        this.generalStatistics.setTranslateY((30));
        this.generalStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.generalStatistics);

        //przycisk pauzy
        this.startStopButton = new Button("Start/Stop");
        this.startStopButton.setTranslateX(970);
        this.startStopButton.setTranslateY(750);
        this.startStopButton.setMinSize(100, 50);
        this.startStopButton.setOnAction(event -> this.engine.paused = !this.engine.paused);
        this.root.getChildren().add(this.startStopButton);

        //przycisk śledzenia zwierzęcia
        this.followAnimal = new Button("Follow selected animal");
        this.followAnimal.setTranslateX(970);
        this.followAnimal.setTranslateY(680);
        this.followAnimal.setMinSize(100, 50);
        this.followAnimal.setVisible(false);
        this.followAnimal.setOnAction(event -> engine.followAnimal());
        this.root.getChildren().add(followAnimal);

        //przycisk wyświetlenia najsilniejszych zwierząt
        Button selectStrongestGenotypeAnimals = new Button("Show strongest animals");
        selectStrongestGenotypeAnimals.setTranslateX(820);
        selectStrongestGenotypeAnimals.setTranslateY(680);
        selectStrongestGenotypeAnimals.setMinSize(100, 50);
        selectStrongestGenotypeAnimals.setOnAction(event ->this.engine.selectStrongestGenes());
        this.root.getChildren().add(selectStrongestGenotypeAnimals);

        //przycisk do wyświetlania ogólnych statystyk
        Button showGeneralStatistics = new Button("Show general statistics");
        showGeneralStatistics.setTranslateX(820);
        showGeneralStatistics.setTranslateY(750);
        showGeneralStatistics.setMinSize(100, 50);
        showGeneralStatistics.setOnAction(event -> {
            if(this.showGeneralStatistics){
                this.showGeneralStatistics = false;
                showGeneralStatistics.setText("Show general statistics");
                this.generalStatistics.setText("");
            }
            else{
                this.showGeneralStatistics = true;
                showGeneralStatistics.setText("Hide general statistics");
                this.generalStatistics.setText(this.map.stats.getStatisticsOfAllTime());
            }
        });
        this.root.getChildren().add(showGeneralStatistics);

        //przycisk do zapisania statystyk do pliku tekstowego
        Button saveExit = new Button("Save and exit");
        saveExit.setTranslateX(1080);
        saveExit.setTranslateY(750);
        saveExit.setMinSize(100, 50);
        saveExit.setOnAction(event -> {
            try {
                this.engine.saveAndExit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.root.getChildren().add(saveExit);

        //przycisk do wyłączenia symulacji
        Button exit = new Button("Exit without saving");
        exit.setTranslateX(1190);
        exit.setTranslateY(750);
        exit.setMinSize(100, 50);
        exit.setOnAction(event -> this.engine.exit());
        this.root.getChildren().add(exit);
    }

    public void drawScene(){
        for(int i = 0; i < this.size.x; i++){
            for(int j = 0; j < this.size.y; j++){
                Vector2d position = new Vector2d(i, j);
                Object object = this.map.objectAt(position);
                this.grid[i][j].setColor(Color.LIGHTGREEN);
                if(object instanceof Animal)
                    if (object.equals(this.engine.getSelectedAnimal()) && this.engine.getSelectedAnimal().getType() == AnimalType.SELECTED)
                        this.grid[i][j].setColor(Color.MAGENTA);
                    else fillAnimalTile((Animal) object);
                else if(object instanceof Grass)
                    this.grid[i][j].setColor(Color.GREEN);
            }
        }
        this.followAnimal.setVisible(false);
        this.mapStatistics.setText(this.map.stats.toString());
        if(this.engine.getSelectedAnimal() == null || this.engine.getSelectedAnimal().getType() != AnimalType.SELECTED)
            this.animalStatistics.setText("No animal is being followed");
        if(showGeneralStatistics){
            this.generalStatistics.setText(this.map.stats.getStatisticsOfAllTime());
        }
        if(this.map.stats.getAge() == this.engine.getAgeFollowNumber()) this.animalStatistics.setText(this.engine.countSelectedAnimalStatistics());
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
}