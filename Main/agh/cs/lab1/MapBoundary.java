package agh.cs.lab1;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapBoundary implements IPositionChangeObserver {
    private final SortedSet<Object> xAxisSet = new TreeSet<>((o1, o2) -> {
        Vector2d pos1 = getObjectPosition(o1);
        Vector2d pos2 = getObjectPosition(o2);
        return compare(o1, o2, pos1.x, pos2.x, pos1.y, pos2.y);
    });
     private final SortedSet<Object> yAxisSet = new TreeSet<>((o1, o2) -> {
        Vector2d pos1 = getObjectPosition(o1);
        Vector2d pos2 = getObjectPosition(o2);
        return compare(o1, o2, pos1.y, pos2.y, pos1.x, pos2.x);
    });

    private int compare(Object o1, Object o2, int mainCoord1, int mainCoord2, int secCoord1, int secCoord2){
        // w zależności, czy sortuję set po x lub y, wywołuję compare dla odpowiedniego priorytetu współrzędnych
        if (mainCoord1 < mainCoord2) return -1;
            else if (mainCoord1 == mainCoord2){
                    if (secCoord1 < secCoord2) return -1;
                    else if (secCoord1 == secCoord2){
                        if (o1 instanceof Animal) {
                            if (o2 instanceof Grass) return -1;
                            else return 0;
                        }
                        else return 1;
                    }
                    return 1;
            }
            else return 1;
    }

    private Vector2d getObjectPosition(Object object){
        if (object instanceof Animal)
            return ((Animal) object).getPosition();
        else return ((Grass) object).getPosition();
    }

    public Vector2d getLowerLeft(){
        if (this.xAxisSet.isEmpty()) return new Vector2d(0, 0);
        return getObjectPosition(this.xAxisSet.first()).lowerLeft(getObjectPosition(this.yAxisSet.first()));
    }

    public Vector2d getUpperRight(){
        if (this.xAxisSet.isEmpty()) return new Vector2d(0, 0);
        return getObjectPosition(this.xAxisSet.last()).upperRight(getObjectPosition(this.yAxisSet.last()));
    }

    public void addObject(Object object){
        this.xAxisSet.add(object);
        this.yAxisSet.add(object);
    }

    private void setNewPosition(SortedSet<Object> set, Vector2d newPosition) {
        Iterator<Object> itr = set.iterator();
        while(itr.hasNext()) {
            Object currentObject = itr.next();
            //ponieważ zwierzę nadpisało już swoją aktualną pozycję, szukam Animala z pozycją o nowych współrzędnych
            if (currentObject instanceof Animal && getObjectPosition(currentObject).equals(newPosition)) {
                //po znalezeniu usuwam zwierzę z setu i dodaję na posortowanej pozycji
                itr.remove();
                set.add(currentObject);
                break;
            }
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        setNewPosition(this.xAxisSet, newPosition);
        setNewPosition(this.yAxisSet, newPosition);
    }

}