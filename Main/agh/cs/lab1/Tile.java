package agh.cs.lab1;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane{
    private final int size;
    private final Rectangle rectangle;
    private final int x;
    private final int y;

    public Tile(int size, int x, int y, Color color){
        this.size = size;
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(size-1, size -1);
        this.rectangle.setStroke(Color.BLACK);
        this.rectangle.setFill(color);
        this.getChildren().add(rectangle);
        this.setTranslateX(x*size);
        this.setTranslateY(y*size);
    }

    public void setFill(Color color) {this.rectangle.setFill(color);}
}
