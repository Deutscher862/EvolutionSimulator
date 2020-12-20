package agh.cs.lab1;

import java.util.ArrayList;

public class Animal {
    private final TorusMap map;
    private final ArrayList<IEnergyRunOutObserver> observers = new ArrayList<>();
    private final ArrayList<Vector2d> positionHistory = new ArrayList<>();
    private final Genotype genes;
    private final Animal firstParent;
    private final Animal secondParent;
    private final int startEnergy;
    private final int moveEnergy;
    private int deadAge = -1;
    //typ zwierzęcia - wykorzystywany podczas śledzenia jego historii
    private AnimalType type;
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private int lifeLength = 0;
    private int aliveChildren = 0;
    private int energy;

    public Animal(TorusMap map, int startEnergy, int moveEnergy, Animal firstParent, Animal secondParent, Vector2d position, AnimalType type){
        this.map = map;
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.position = position;
        this.type = type;
        if (firstParent == null)
            this.genes = new Genotype();
        else this.genes = new Genotype(firstParent.getGenes(), secondParent.getGenes());
    }

    public MapDirection getOrientation() {
        return this.orientation;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public String toString() { return this.orientation.toString(); }

    public Genotype getGenes() {
        return this.genes;
    }

    public int getStartEnergy() { return startEnergy; }

    public int getEnergy() { return this.energy; }

    public int getMoveEnergy() { return moveEnergy; }

    public int getLifeLength() { return lifeLength; }

    public int getAliveChildren() { return aliveChildren; }

    public int getDeadAge() {
        return deadAge;
    }

    public void setDeadAge(int deadAge) {
        this.deadAge = deadAge;
    }

    public void setType(AnimalType type){
        this.type = type;
    }

    public AnimalType getType() {
        return type;
    }

    public void move() {
        //losuję gen obrotu
        int rotate = this.genes.randomDirection();
        for (int i = 0; i < rotate; i++){
            this.orientation = this.orientation.next();
        }
        Vector2d oldPosition = this.position;
        Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
        //dzięki metodzie getBackToMap zwierzę zawsze zostaje na mapie
        this.position = newPosition.goBackToMap(this.map.getUpperRight());

        this.energy -= this.moveEnergy;
        this.lifeLength += 1;
        this.map.positionChanged(oldPosition, this.position, this);

        //gdy zwierzę umarło przekazuję informację o tym do obserwatorów i rodziców
        if(this.energy <= 0) {
            informAboutDeath();
            if(this.firstParent != null)
                this.firstParent.aliveChildren -= 1;
            if(this.secondParent != null)
                this.secondParent.aliveChildren -= 1;
        }
    }

    public void eat(int grassEnergy){
        this.energy += grassEnergy;
    }

    //silniejszemu rodzicowi przekazuję drugiego i pozycję do umieszczenia dziecka na mapie
    public Animal reproduce(Animal secondParent, Vector2d childPosition){
        //rodzice tracą energię
        this.energy -= this.energy/4;
        secondParent.energy -= secondParent.energy/4;

        //jeśli uruchomione jest śledzenie zwierząt, nowe zwierzęta oznaczane są jako dzieci lub potomkowie
        AnimalType childType;
        if(this.type == AnimalType.SELECTED || secondParent.type == AnimalType.SELECTED) childType = AnimalType.CHILD;
        else if(this.type == AnimalType.CHILD || secondParent.type == AnimalType.CHILD || this.type == AnimalType.DESCENDANT || secondParent.type == AnimalType.DESCENDANT)  childType = AnimalType.DESCENDANT;
        else childType = AnimalType.DEFAULT;

        Animal child = new Animal(this.map,(this.energy+secondParent.energy)/4, this.moveEnergy, this, secondParent, childPosition, childType);
        this.aliveChildren += 1;
        secondParent.aliveChildren += 1;
        return child;
    }

    public void addObserver(IEnergyRunOutObserver observer){
        this.observers.add(observer);
    }

    private void informAboutDeath(){
        for(IEnergyRunOutObserver observer : this.observers){
            observer.EnergyRunOut(this);
        }
    }
}