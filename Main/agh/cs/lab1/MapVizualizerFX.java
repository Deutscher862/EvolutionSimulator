package agh.cs.lab1;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MapVizualizerFX {
    private final Tile[][] grid;
    private final Pane root;
    private final Vector2d size;
    private final TorusMap map;
    private Text mapStatistics;
    private Text animalStatistics;
    protected boolean stop = false;
    private Animal selectedAnimal = null;

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d size, SimulationEngine engine) {
        this.root = new Pane();
        this.size = size;
        this.map = map;
        this.grid = new Tile[size.x][size.y];

        for(int x = 0; x < this.size.x; x++){
            for( int y = 0; y < this.size.y; y++){
                Tile tile;
                if (this.map.objectAt(new Vector2d(x, y)) instanceof Animal)
                    this.grid[x][y] = new Tile(tileSize, new Vector2d(x, y), Color.BLACK, this);
                else this.grid[x][y] = new Tile(tileSize, new Vector2d(x, y), Color.LIGHTGREEN, this);
                this.root.getChildren().add(this.grid[x][y]);
            }
        }

        this.mapStatistics = new Text();
        this.mapStatistics.setWrappingWidth(200);
        this.mapStatistics.setTranslateX(850);
        this.mapStatistics.setTranslateY((30));
        this.mapStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.mapStatistics);

        this.animalStatistics = new Text();
        this.animalStatistics.setWrappingWidth(200);
        this.animalStatistics.setTranslateX(850);
        this.animalStatistics.setTranslateY((400));
        this.animalStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.animalStatistics);


        Button startStopButton = new Button("Stop");
        startStopButton.setTranslateX(950);
        startStopButton.setTranslateY(700);
        startStopButton.setMinSize(100, 50);
        startStopButton.setOnAction(event -> {
            if(this.stop){
                this.stop = false;
                startStopButton.setText("Stop");
            }
            else{
                this.stop = true;
                startStopButton.setText("Start");
            }
        });
        this.root.getChildren().add(startStopButton);

        Button selectStrongestGenotypeAnimals = new Button("Show Strongest Animals");
        selectStrongestGenotypeAnimals.setTranslateX(930);
        selectStrongestGenotypeAnimals.setTranslateY(600);
        selectStrongestGenotypeAnimals.setMinSize(100, 50);
        selectStrongestGenotypeAnimals.setOnAction(event ->selectStrongestGenes());
        this.root.getChildren().add(selectStrongestGenotypeAnimals);
    }

    public void drawScene(){
        for(int i = 0; i < this.size.x; i++){
            for(int j = 0; j < this.size.y; j++){
                Vector2d position = new Vector2d(i, j);
                Object object = this.map.objectAt(position);
                this.grid[i][j].setColor(Color.LIGHTGREEN);
                if(object instanceof Animal){
                    if(object.equals(this.selectedAnimal)) grid[i][j].setColor(Color.MAGENTA);
                    else fillAnimalTile((Animal) object);
                }
                else if(object instanceof Grass)
                    this.grid[i][j].setColor(Color.GREEN);
            }
        }
        this.mapStatistics.setText(this.map.stats.toString());
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
                grid[position.x][position.y].setColor(Color.MAGENTA);
            }
        }
    }

    public void selectAnimal(Vector2d position) {
        Object animal = this.map.objectAt(position);
        if(animal instanceof Animal){
            if(this.selectedAnimal != null)
                fillAnimalTile(this.selectedAnimal);
            this.selectedAnimal = (Animal) animal;
            grid[position.x][position.y].setColor(Color.MAGENTA);
            this.animalStatistics.setText("Genotyp zaznaczonego zwierzęcia:\n" + ((Animal) animal).getGenes().toString());
        }
    }
}