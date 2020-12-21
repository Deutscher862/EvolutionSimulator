package agh.cs.lab1;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

public class MapVizualizerFX {
    private final Tile[][] grid;
    private final Button followAnimal;
    private final SimulationEngine engine;
    private final Pane root;
    private final Vector2d mapSize;
    private final TorusMap map;
    private final Button startStopButton;
    private Text animalStatistics;
    private Text mapStatistics;
    private Text generalStatistics;
    private Text mapSizeWarning;
    private boolean showGeneralStatistics = false;

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d mapSize, SimulationEngine engine) {
        this.root = new Pane();
        this.mapSize = mapSize;
        this.map = map;
        this.engine = engine;

        //nie wyświetlam mapy, jeśli jej rozmiar jest za duży
        if(mapSize.x > 50 || mapSize.y > 50) {
            this.grid = null;
            this.mapSizeWarning = new Text();
            this.mapSizeWarning.setText("Map size to large, displaying only statistics");
            this.mapSizeWarning.setFont(Font.font("Verdana", 15));
            this.mapSizeWarning.setTranslateX(100);
            this.mapSizeWarning.setTranslateY(400);
            this.root.getChildren().add(this.mapSizeWarning);
        }
        else {
            this.grid = new Tile[mapSize.x][mapSize.y];
            this.mapSizeWarning = null;

            //tworzę siatkę do wyświetlania pól
            for (int x = 0; x < this.mapSize.x; x++) {
                for (int y = 0; y < this.mapSize.y; y++) {
                    Tile tile;
                    if (this.map.objectAt(new Vector2d(x, y)) instanceof Animal)
                        this.grid[x][y] = new Tile(tileSize, new Vector2d(x, y), Color.BLACK, this);
                    else this.grid[x][y] = new Tile(tileSize, new Vector2d(x, y), Color.LIGHTGREEN, this);
                    this.root.getChildren().add(this.grid[x][y]);
                }
            }
        }


        //aktualne statystyki mapy
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

        //przycisk pauzy/wznowienia
        this.startStopButton = new Button("Start/Stop");
        this.startStopButton.setTranslateX(970);
        this.startStopButton.setTranslateY(750);
        this.startStopButton.setMinSize(100, 50);
        this.startStopButton.setOnAction(event -> this.engine.pause());
        this.root.getChildren().add(this.startStopButton);

        //przycisk śledzenia zwierzęcia
        if(this.grid != null){
            this.followAnimal = new Button("Follow selected animal");
            this.followAnimal.setTranslateX(970);
            this.followAnimal.setTranslateY(680);
            this.followAnimal.setMinSize(100, 50);
            this.followAnimal.setVisible(false);
            this.followAnimal.setOnAction(event -> engine.followAnimal());
            this.root.getChildren().add(followAnimal);
        }
        else this.followAnimal = null;

        //przycisk wyświetlenia najsilniejszych zwierząt
        if(this.grid != null) {
            Button selectStrongestGenotypeAnimals = new Button("Show strongest animals");
            selectStrongestGenotypeAnimals.setTranslateX(820);
            selectStrongestGenotypeAnimals.setTranslateY(680);
            selectStrongestGenotypeAnimals.setMinSize(100, 50);
            selectStrongestGenotypeAnimals.setOnAction(event -> this.engine.selectStrongestGenes());
            this.root.getChildren().add(selectStrongestGenotypeAnimals);
        }

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
                this.generalStatistics.setText(this.engine.getGeneralStatistics());
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

    public void setTileColor(Vector2d position, Color color){
        this.grid[position.x][position.y].setColor(color);
    }

    public void setAnimalStatistics(String animalStatistics) {
        this.animalStatistics.setText(animalStatistics);
    }

    public void setFollowAnimalVisibility(){
        this.followAnimal.setVisible(!this.followAnimal.isVisible());
    }

    public Pane getRoot() {
        return root;
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public void drawScene(){
        //pętla kolorująca płytki
        if(this.grid != null) {
            for (int i = 0; i < this.mapSize.x; i++) {
                for (int j = 0; j < this.mapSize.y; j++) {
                    Vector2d position = new Vector2d(i, j);
                    Object object = this.map.objectAt(position);
                    setTileColor(position, Color.LIGHTGREEN);
                    if (object instanceof Animal)
                        if (object.equals(this.engine.getSelectedAnimal()) && this.engine.getSelectedAnimal().getType() == AnimalType.SELECTED)
                            setTileColor(position, Color.MAGENTA);
                        else fillAnimalTile((Animal) object);
                    else if (object instanceof Grass)
                        setTileColor(position, Color.GREEN);
                }
            }
        }
        //aktualizowanie wyświetlanych statystyk
        if(this.followAnimal != null)
            this.followAnimal.setVisible(false);
        this.mapStatistics.setText(this.engine.getCurrentStatistics());
        if(this.engine.getSelectedAnimal() == null || this.engine.getSelectedAnimal().getType() != AnimalType.SELECTED)
            this.animalStatistics.setText("No animal is being followed");
        if(showGeneralStatistics){
            this.generalStatistics.setText(this.engine.getGeneralStatistics());
        }
        if(this.engine.getAge() == this.engine.getAgeFollowNumber())
            this.animalStatistics.setText(this.engine.countSelectedAnimalStatistics());
    }

    public void fillAnimalTile(Animal animal){
        //kolorowanie płytki ze zwierzęciem w zależności od jego energii
        Vector2d position = animal.getPosition();
        int animalStartEnergy = animal.getStartEnergy();
        int animalEnergy = animal.getEnergy();
        if (animalEnergy >= animalStartEnergy*3/4)
            setTileColor(position, Color.BLACK);
        else if(animalEnergy >= animalStartEnergy/2)
            setTileColor(position, Color.DARKGRAY);
        else if(animalEnergy >= animalStartEnergy/4)
            setTileColor(position, Color.GRAY);
        else setTileColor(position, Color.LIGHTGRAY);
    }

    public void animalSelected(Vector2d position) {
        //przekazanie do engine informacji i wskazaniu zwierzęcia
        this.engine.selectAnimal(position);
    }
}