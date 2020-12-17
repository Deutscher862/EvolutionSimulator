package agh.cs.lab1;

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

    public MapVizualizerFX(TorusMap map, int tileSize, Vector2d size) {
        this.root = new Pane();
        this.size = size;
        this.map = map;
        this.grid = new Tile[size.x][size.y];

        for(int x = 0; x < this.size.x; x++){
            for( int y = 0; y < this.size.y; y++){
                Tile tile = new Tile(tileSize, x, y, Color.LIGHTGREEN);
                this.grid[x][y] = tile;
                this.root.getChildren().add(tile);
            }
        }

        this.statistics = new Text();
        this.statistics.setWrappingWidth(200);
        this.statistics.setTranslateX(850);
        this.statistics.setTranslateY((30));
        this.statistics.setFont(Font.font("Verdana", 15));
        this.root.getChildren().add(this.statistics);
    }

    public void drawScene(){
        for(int i = 0; i < this.size.x; i++){
            for(int j = 0; j < this.size.y; j++){
                Vector2d position = new Vector2d(i, j);
                Object object = this.map.objectAt(position);
                this.grid[i][j].setFill(Color.LIGHTGREEN);
                if(object instanceof Animal)
                    grid[i][j].setFill(Color.BROWN);
                else if(object instanceof Grass)
                    this.grid[i][j].setFill(Color.GREEN);
            }
        }
        this.statistics.setText(this.map.stats.toString());
    }

    public Pane getRoot() {
        return root;
    }
}