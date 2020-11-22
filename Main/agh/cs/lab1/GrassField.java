package agh.cs.lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GrassField extends AbstractWorldMap{
    private Map<Vector2d,Grass> mapOfGrass = new HashMap<>();
    // wektory posiadające pozycję upperRight i lowerLeft od wszystkich obiektów
    private Vector2d upperRight = new Vector2d(0, 0);
    private Vector2d lowerLeft = this.upperRight;
    // z listy skorzystam podczas akutalizowania wektora upperRight przed wizualizacją
    private final ArrayList<Animal> listOfAnimals = new ArrayList<>();

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
            grass = new Grass(v);
            //indeks zwiększa się tylko wtedy, gdy wylosowana pozycja nie jest już zajęta
            if (this.mapOfGrass.get(v) == null) {
                this.mapOfGrass.put(v, grass);
                this.upperRight = this.upperRight.upperRight(v);
                this.lowerLeft = this.lowerLeft.lowerLeft(v);
                index += 1;
            }
        }
    }
    @Override
    public Vector2d getLowerLeft(){
        return this.lowerLeft;
    }

    @Override
    public Vector2d getUpperRight(){
        return this.upperRight;
    }

    @Override
    public boolean canMoveTo(Vector2d position){
        if (super.canMoveTo(position)){
            this.upperRight = this.upperRight.upperRight(position);
            this.lowerLeft = this.lowerLeft.lowerLeft(position);
            return true;
        }
        return false;
    }


    @Override
    public boolean place(Animal animal) {
        if(super.place(animal)){
            // jeśli zwierzę zostało umieszczone na mapie dodaję je do listy
            Vector2d position = animal.getPosition();
            this.upperRight = this.upperRight.upperRight(position);
            this.lowerLeft = this.lowerLeft.lowerLeft(position);
            listOfAnimals.add(animal);
            return true;
        }
        return false;
    }

    @Override
    public Object objectAt(Vector2d position) {
        Object result = super.objectAt(position);
        if (result != null) return result;
        //jeśli na pozycji nie ma zwięrzęcia sprawdzam na niej obecność trawy
        else return this.mapOfGrass.get(position);
    }
}