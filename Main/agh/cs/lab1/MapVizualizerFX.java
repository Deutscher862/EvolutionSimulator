package agh.cs.lab1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MapVizualizerFX {
    private final Tile[][] grid;
    private final Pane root;
    private final Vector2d size;
    private final TorusMap map;
    private Text statistics;

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d size, SimulationEngine engine) {
        this.root = new Pane();
        this.size = size;
        this.map = map;
        this.grid = new Tile[size.x][size.y];

        for(int x = 0; x < this.size.x; x++){
            for( int y = 0; y < this.size.y; y++){
                Color color;
                Tile tile;
                if (this.map.objectAt(new Vector2d(x, y)) instanceof Animal)
                    this.grid[x][y] = new Tile(tileSize, x, y, Color.BLACK);
                else this.grid[x][y] = new Tile(tileSize, x, y, Color.LIGHTGREEN);
                this.root.getChildren().add(this.grid[x][y]);
            }
        }

        this.statistics = new Text();
        this.statistics.setWrappingWidth(200);
        this.statistics.setTranslateX(850);
        this.statistics.setTranslateY((30));
        this.statistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.statistics);

        Button startStopButton = new Button("Stop");
        startStopButton.setTranslateX(950);
        startStopButton.setTranslateY(400);
        startStopButton.setMinSize(100, 50);
        startStopButton.setOnAction(event -> engine.pause());
        this.root.getChildren().add(startStopButton);
    }

    public void drawScene(){
        for(int i = 0; i < this.size.x; i++){
            for(int j = 0; j < this.size.y; j++){
                Vector2d position = new Vector2d(i, j);
                Object object = this.map.objectAt(position);
                this.grid[i][j].setColor(Color.LIGHTGREEN);
                if(object instanceof Animal)
                {
                    int animalStartEnergy = ((Animal) object).getStartEnergy();
                    int animalEnergy = ((Animal) object).getEnergy();
                    if (animalEnergy >= animalStartEnergy*3/4)
                        grid[i][j].setColor(Color.MAGENTA);
                    else if(animalEnergy >= animalStartEnergy/2)
                        grid[i][j].setColor(Color.BROWN);
                    else if(animalEnergy >= animalStartEnergy/4)
                        grid[i][j].setColor(Color.GRAY);
                    else grid[i][j].setColor(Color.LIGHTGRAY);
                }
                else if(object instanceof Grass)
                    this.grid[i][j].setColor(Color.GREEN);
            }
        }
        this.statistics.setText(this.map.stats.toString());
    }

    public Pane getRoot() {
        return root;
    }
}