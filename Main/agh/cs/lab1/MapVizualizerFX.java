package agh.cs.lab1;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MapVizualizerFX {
    private final Tile[][] grid;
    private final Pane root;
    private final int width;
    private final int height;
    private final TorusMap map;
    private Text statistics;

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d size) {
        this.root = new Pane();
        this.width = size.x;
        this.height = size.y;
        this.map = map;
        this.grid = new Tile[width][height];

        for(int x = 0; x < this.width; x++){
            for( int y = 0; y < this.height; y++){
                Tile tile = new Tile(tileSize, x, y, Color.WHITE);
                this.grid[x][y] = tile;
                this.root.getChildren().add(tile);
            }
        }
        this.statistics = new Text();
        this.statistics.setWrappingWidth(200);
        this.statistics.setTranslateX(550);
        this.statistics.setTranslateY((30));
        this.root.getChildren().add(this.statistics);
    }

    public void outputMap(){
        for(int i = 0; i < this.width; i++){
            for(int j = 0; j < this.height; j++){
                Vector2d position = new Vector2d(i, j);
                Object object = this.map.objectAt(position);
                if(object instanceof Animal)
                    grid[i][j].setFill(Color.BROWN);
                else if(object instanceof Grass)
                    this.grid[i][j].setFill(Color.GREEN);
                else this.grid[i][j].setFill(Color.LIGHTGREEN);
            }
        }
        this.statistics.setText(this.map.stats.toString());
    }

    public Pane getRoot() {
        return root;
    }
}