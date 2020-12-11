package agh.cs.lab1;

import java.util.*;
import java.util.stream.Collectors;

public class Statistics {
    protected int numberOfGrass;
    protected int numberOfAnimals;
    protected int numberOfDeadAnimals;
    protected float sumOfLifeLengths;
    private float averageEnergy;
    private float averageLifeLength;
    private float averageNumberOfChildren;
    private Genotype strongestGenotype;

    public Statistics(){
        this.numberOfAnimals = 0;
        this.numberOfGrass = 0;
        this.numberOfDeadAnimals = 0;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "numberOfGrass=" + numberOfGrass +
                ", numberOfAnimals=" + numberOfAnimals +
                ", numberOfDeadAnimals=" + numberOfDeadAnimals +
                ", sumOfLifeLengths=" + sumOfLifeLengths +
                ", averageEnergy=" + averageEnergy +
                ", averageLifeLength=" + averageLifeLength +
                ", averageNumberOfChildren=" + averageNumberOfChildren +
                ", strongestGenotype=" + strongestGenotype +
                '}';
    }

    public void countAverages(ArrayList<Animal> list){
        float averageEnergy = 0;
        float averageNumberOfChildren = 0;
        Map<Genotype, Integer> genesMap = new HashMap<>();
        for(Animal animal : list){
            averageEnergy += animal.getEnergy();
            averageNumberOfChildren += animal.getNumberOfChildren();
            Genotype animalGenes = animal.getGenes();
            if(genesMap.get(animalGenes) == null) genesMap.put(animalGenes, 1);
            else genesMap.replace(animalGenes, genesMap.get(animalGenes) + 1);
        }
        if(this.numberOfAnimals > 0) {
            this.averageEnergy = averageEnergy / this.numberOfAnimals;
            this.averageNumberOfChildren = averageNumberOfChildren / this.numberOfAnimals;
        }
        else {
            this.averageEnergy = 0;
            this.averageNumberOfChildren = 0;
        }
        if(genesMap.size() > 0){
            genesMap = genesMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            Map.Entry<Genotype,Integer> entry = genesMap.entrySet().iterator().next();
            this.strongestGenotype = entry.getKey();
        }
        else this.strongestGenotype = null;

        countAverageLifeLength();
    }

    public void countAverageLifeLength(){
        if(this.numberOfDeadAnimals > 0)
            this.averageLifeLength = this.sumOfLifeLengths/this.numberOfDeadAnimals;
    }
}
