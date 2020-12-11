package agh.cs.lab1;

/*
W konstruktorze pojawia się pierwsza trawa
Następuje pętla, a w niej:
1.Wszystkie zwierzęta ruszają się i tracą energię
2.Następuje przejście po całej mapie:
    - sprawdzenie czy jakieś zwierzę umarło - jest usuwane z listy i hashmapy
    - sprawdzenie czy jakaś trawa i zwierzę jest na jednym polu - jeśli tak to trawa jest usuwana z hashmapy i zwierze odzyskuje energię
    - sprawdzenie czy kilka zwierząt nie stoi na jednej pozycji i czy mogą się rozmnażać - następuje stworzenie nowego zwierzęta
3. Rośnie nowa trawa
 */

import java.util.ArrayList;

public class SimulationEngine implements IEngine {
    private TorusMap map;
    private int ages;

    public SimulationEngine(int numberOfAnimals, int startEnergy, int moveEnergy, int grassEnergy, Vector2d upperRight, float jungleRatio, int ages) {
        this.map = new TorusMap(upperRight, grassEnergy, jungleRatio);
        this.ages = ages;
        //jeśli zwierząt jest więcej niż miejsc na mapie, zmniejszam ilość zwierząt
        int mapSize = (upperRight.x + 1)*(upperRight.y + 1);
        if (numberOfAnimals > mapSize) numberOfAnimals = mapSize;

        for (int i = 0; i < numberOfAnimals; i++){
            Animal newAnimal = new Animal(this.map, startEnergy, moveEnergy);
            // jeśli pozycja na mapie jest już zajęta, generuje mu nowy wektor
            while(this.map.getMapOfAnimals().get(newAnimal.getPosition()) != null)
                newAnimal.generateNewPosition();
            this.map.place(newAnimal);
        }
    }

    @Override
    public void run() {
        System.out.println(this.map.toString());
        for (int i = 0; i < this.ages; i++) {
            //this.map.move();
            this.map.growGrass();
            ArrayList<Animal> listOfAnimals = this.map.getListOfAnimals();

            for(Animal currentAnimal : listOfAnimals){
                currentAnimal.move();
            }

            this.map.removeDeadAnimals();
            this.map.grassEating();
            this.map.reproduce();
            /*
            Set<Vector2d> animalPositions = this.map.getMapOfAnimals().keySet();
            for(Vector2d currentPosition : animalPositions){

                for(Animal animal : this.map.getMapOfAnimals().get(currentPosition)){
                    System.out.println(animal.getEnergy());
                }
                System.out.println(" ");

            }
*/
            System.out.println(this.map.toString());
        }
    }
}