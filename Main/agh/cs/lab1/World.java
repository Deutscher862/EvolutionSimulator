package agh.cs.lab1;

public class World {

    public static void main(String[] args) {
        Animal zajonc = new Animal(new Vector2d(2, 2));
        zajonc.move(MoveDirection.RIGHT);
        zajonc.move(MoveDirection.RIGHT);
        zajonc.move(MoveDirection.FORWARD);
        System.out.println(zajonc.toString());
        System.out.println("Test czy dziala");
    }
}