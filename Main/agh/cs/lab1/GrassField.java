package agh.cs.lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GrassField extends AbstractWorldMap{
    private Map<Vector2d,Grass> mapOfGrass = new HashMap<>();
    // wektory posiadające pozycję upperRight i lowerLeft od wszystkich obiektów
    private Vector2d upperRight = super.lowerLeft;
    private Vector2d lowerLeft = super.lowerLeft;
    // z listy skorzystam podczas akutalizowania wektora the Farthest przed wizualizacją
    private ArrayList<Animal> listOfAnimals = new ArrayList<>();

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
    public String toString() {
        for (Animal a : listOfAnimals){
            //przeszukuję listę zwierząt szukając najdalszych pozycji, by podać ich x oraz y podczas wizualizacji mapy
            this.upperRight = this.upperRight.upperRight(a.getPosition());
            this.lowerLeft = this.lowerLeft.lowerLeft(a.getPosition());
        }
        return super.toString(this.lowerLeft, this.upperRight);
    }

    @Override
    public boolean place(Animal animal) {
        if(super.place(animal)){
            // jeśli zwierzę zostało umieszczone na mapie dodaję je do listy
            this.upperRight = this.upperRight.upperRight(animal.getPosition());
            this.lowerLeft = this.lowerLeft.lowerLeft(animal.getPosition());
            listOfAnimals.add(animal);
            return true;
        }
        return false;
    }

    @Override
    public Object objectAt(Vector2d position) {
        //zwrócenie zwierzęcia ma priorytet nad zwróceniem trawy
        if (super.objectAt(position) != null) return this.mapOfAnimals.get(position);
        //jeśli na pozycji nie ma zwięrzęcia sprawdzam na niej obecność trawy
        else return this.mapOfGrass.get(position);
    }
}