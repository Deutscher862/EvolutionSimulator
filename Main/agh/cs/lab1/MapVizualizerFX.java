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
    private final Button followAnimal;

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
        this.animalStatistics.setTranslateY((340));
        this.animalStatistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.animalStatistics);

        Button startStopButton = new Button("Stop");
        startStopButton.setTranslateX(950);
        startStopButton.setTranslateY(750);
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

        this.followAnimal = new Button("Follow Selected Animal");
        this.followAnimal.setTranslateX(930);
        this.followAnimal.setTranslateY(610);
        this.followAnimal.setMinSize(100, 50);
        this.followAnimal.setVisible(false);
        this.followAnimal.setOnAction(event -> followAnimal());

        this.root.getChildren().add(followAnimal);


        Button selectStrongestGenotypeAnimals = new Button("Show Strongest Animals");
        selectStrongestGenotypeAnimals.setTranslateX(930);
        selectStrongestGenotypeAnimals.setTranslateY(680);
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
                    if(object.equals(this.selectedAnimal)) this.grid[i][j].setColor(Color.MAGENTA);
                    else fillAnimalTile((Animal) object);
                }
                else if(object instanceof Grass)
                    this.grid[i][j].setColor(Color.GREEN);
            }
        }
        this.followAnimal.setVisible(false);
        this.mapStatistics.setText(this.map.stats.toString());
        if(this.selectedAnimal != null && this.selectedAnimal.type == AnimalType.SELECTED)
            this.animalStatistics.setText(countSelectedAnimalStatistics());
        else this.animalStatistics.setText("");
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
        if(this.stop && animal instanceof Animal){
            if(this.selectedAnimal != null)
                fillAnimalTile(this.selectedAnimal);
            this.followAnimal.setVisible(true);
            this.selectedAnimal = (Animal) animal;
            grid[position.x][position.y].setColor(Color.MAGENTA);
            this.animalStatistics.setText("Selected Animal Genotype= \n" + ((Animal) animal).getGenes().toString());
        }
    }

    private String countSelectedAnimalStatistics() {
        int countChildren = 0;
        int countDescendants = 0;
        String diedAt = "-";
        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            if (animal.getType() == AnimalType.CHILD) countChildren += 1;
            else if (animal.getType() == AnimalType.DESCENDANT) countDescendants += 1;
        }

        return "Selected Animal Statistics:" +
                "\nGenotype= " + this.selectedAnimal.getGenes()+
                "\nAlive Children= " + countChildren +
                "\nAlive Descendants= " + countDescendants +
                "\nDied At= " + this.selectedAnimal.getDeadAge();

    }

    public void followAnimal(){
        //ustawiam wszystkie zwierzęta na default
        ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();
        for(Animal animal : listOfAnimals){
            animal.type = AnimalType.DEFAULT;
        }
        this.selectedAnimal.type = AnimalType.SELECTED;
        this.followAnimal.setVisible(false);
        this.animalStatistics.setText(countSelectedAnimalStatistics());
    }
}