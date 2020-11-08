package agh.cs.lab1;

import java.util.ArrayList;

public class RectangularMap implements IWorldMap {
    //arraylist będzie pomocna w czasie poruszania zwięrzętami w odpowiedniej kolejności
    public ArrayList<Animal> animals = new ArrayList<>();
    public Animal[][] mapOfAnimals;
    public int width;
    public int height;

    public RectangularMap(int width, int height) {
        //zakładam, że indeksowanie tablicy jest od 0 do przekazanej wartości włącznie
        this.width = width;
        this.height = height;
        //tworzę tablice zwierząt, by zwracać zawartość danej pozycji w O(1), na początku zawiera tylko nulle
        //z powodu powyższego założenia zwiększam szerokość i wysokość o 1
        this.mapOfAnimals = new Animal[width + 1][height + 1];
    }

    public String toString() {
        MapVisualizer visualize = new MapVisualizer(this);
        return (visualize.draw(new Vector2d(0, 0), new Vector2d(this.width, this.height)));
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        //sprawdzam czy podana współrzędna mieści się na mapie i pozycja nie jest zajęta
        return position.x >= 0 && position.y >= 0 && position.x <= this.width && position.y <= this.height && !isOccupied(position);
    }

    @Override
    public boolean place(Animal animal) {
        //jeśli zwierzę może zostać dodane do mapy, dodaję je do listy oraz do mapy zwierząt
        Vector2d position = animal.getPosition();
        if (canMoveTo(position)) {
            this.animals.add(animal);
            this.mapOfAnimals[position.x][position.y] = animal;
            return true;
        }
        return false;
    }

    @Override
    public void run(MoveDirection[] directions) {
        int index = 0;
        //dla każdego ruchu zwierzę porusza się dokładnie raz
        for (MoveDirection direction : directions) {
            Vector2d lastPosition = animals.get(index).getPosition();
            Animal currentAnimal = animals.get(index);

            currentAnimal.move(direction);

            //jeśli zwierzę się poruszyło, to aktualizuję mapę
            if (!currentAnimal.getPosition().equals(lastPosition)) {
                mapOfAnimals[lastPosition.x][lastPosition.y] = null;
                mapOfAnimals[currentAnimal.getPosition().x][currentAnimal.getPosition().y] = currentAnimal;
            }
            index += 1;
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        //w czasie O(1) zwracam prawdę jeśli pole nie jest puste
        return this.mapOfAnimals[position.x][position.y] != null;
    }

    @Override
    public Object objectAt(Vector2d position) {
        //w czasie O(1) zwracam obiekt z danej współrzędnej
        return this.mapOfAnimals[position.x][position.y];
    }
}