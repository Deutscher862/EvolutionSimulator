package agh.cs.lab1;

import java.util.*;
import java.util.stream.Collectors;

public class Statistics {
    protected int numberOfGrass = 0;
    protected int numberOfAnimals = 0;
    protected int numberOfDeadAnimals = 0;
    protected float sumOfLifeLengths;
    private float averageEnergy;
    private float averageLifeLength;
    private float averageNumberOfChildren;
    //hashmapa genotypów - klucz-genotyp, wartości - ilość występowania takich samych genów
    private final Map<Genotype, Integer> genesMap = new HashMap<>();
    private Genotype strongestGenotype;
    private int strongestGenotypeAmount = 0;

    public Statistics() {
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

    public void countAverages(ArrayList<Animal> list) {
        float averageEnergy = 0;
        float averageNumberOfChildren = 0;

        for (Animal animal : list) {
            averageEnergy += animal.getEnergy();
            averageNumberOfChildren += animal.getAliveChildren();
        }
        if (this.numberOfAnimals > 0) {
            this.averageEnergy = averageEnergy / this.numberOfAnimals;
            this.averageNumberOfChildren = averageNumberOfChildren / this.numberOfAnimals;
        } else {
            this.averageEnergy = 0;
            this.averageNumberOfChildren = 0;
        }
        if (genesMap.size() > 0) {
            //sortuję hashmapę genotypów po ilości ich występowania
            //System.out.println(this.genesMap.toString());
            LinkedHashMap<Genotype, Integer> sortedGenes = this.genesMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            //System.out.println(sortedGenes.toString());
            Map.Entry<Genotype, Integer> entry = sortedGenes.entrySet().iterator().next();
            if(entry.getValue() > this.strongestGenotypeAmount) {
                this.strongestGenotype = entry.getKey();
                this.strongestGenotypeAmount = entry.getValue();
            }
        }

        countAverageLifeLength();
    }

    public void addToHashmap(Animal animal){
        Genotype animalGenes = animal.getGenes();
        if (this.genesMap.get(animalGenes) == null) this.genesMap.put(animalGenes, 1);
        else this.genesMap.replace(animalGenes, this.genesMap.get(animalGenes) + 1);
    }

    public void removeFromHashmap(Animal animal){
        Genotype animalGenes = animal.getGenes();
        this.genesMap.replace(animalGenes, this.genesMap.get(animalGenes) - 1);
        if(this.genesMap.get(animalGenes) == 0) this.genesMap.remove(animalGenes);
    }

    public void countAverageLifeLength() {
        if (this.numberOfDeadAnimals > 0)
            this.averageLifeLength = this.sumOfLifeLengths / this.numberOfDeadAnimals;
    }
}
