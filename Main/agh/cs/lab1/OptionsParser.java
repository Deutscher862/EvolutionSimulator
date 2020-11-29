package agh.cs.lab1;

import java.util.ArrayList;

public class OptionsParser {
    public MoveDirection[] parse(String[] args) throws IllegalArgumentException {
        ArrayList<MoveDirection> arr = new ArrayList<>();

        for (String s : args) {
            switch (s) {
                case "f", "forward" -> arr.add(MoveDirection.FORWARD);
                case "b", "backward" -> arr.add(MoveDirection.BACKWARD);
                case "l", "left" -> arr.add(MoveDirection.LEFT);
                case "r", "right" -> arr.add(MoveDirection.RIGHT);
                default -> throw new IllegalArgumentException(s + " is not legal move specification");
            }
        }
        return arr.toArray(new MoveDirection[0]);
    }
}