package agh.cs.lab1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GrassField extends AbstractWorldMap{
    private final Map<Vector2d,Grass> mapOfGrass = new HashMap<>();

    public GrassField(int numberOfGrass){
        Random rand = new Random();
        int max = (int) Math.ceil(Math.sqrt(numberOfGrass*10));
        int random_x;
        int random_y;
        Grass grass;
        Vector2d v;
        int index = 0;
        //pętla powtarza się, dopóki na mapie nie będzie odpowiedniej liczby trawy
        while (index < numberOfGrass){
            random_x = rand.nextInt(max);
            random_y = rand.nextInt(max);
            v = new Vector2d(random_x, random_y);
            grass = new Grass(v, 5);
            //indeks zwiększa się tylko wtedy, gdy wylosowana pozycja nie jest już zajęta
            if (this.mapOfGrass.get(v) == null) {
                this.mapOfGrass.put(v, grass);
                this.mBoundary.addObject(grass);
                index += 1;
            }
        }
    }

    @Override
    public Object objectAt(Vector2d position) {
        Object result = super.objectAt(position);
        if (result != null) return result;
        //jeśli na pozycji nie ma zwięrzęcia sprawdzam na niej obecność trawy
        else return this.mapOfGrass.get(position);
    }

    @Override
    public void EnergyRunOut(Animal animal) {

    }
}