package agh.cs.lab1;

import java.util.Arrays;
import java.util.Random;

public class Genotype {
    private final Random rand  = new Random();
    private final int[] genes = new int[32];
    private int[] directions = {0, 0, 0, 0, 0, 0, 0, 0};

    public Genotype(){
        for(int i = 0; i < 32; i++) {
            this.genes[i] = rand.nextInt(8);
            this.directions[this.genes[i]] += 1;
        }
        validate();
    }

    public Genotype(Genotype strongerGenes, Genotype weakerGenes){
        crossGenes(strongerGenes.getGenes(), weakerGenes.getGenes());
    }

    public int[] getGenes(){
        return this.genes;
    }

    @Override
    public String toString() {
        String result = "";
        for(int gene : this.genes)
            result = result + gene;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Genotype){
            return Arrays.equals(this.genes, ((Genotype) o).genes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    protected int randomDirection(){
        return this.genes[rand.nextInt(32)];
    }

    private void crossGenes(int[] strongerGenes, int[] weakerGenes){
        // losuję 2 różne inty dzielące geny na 3 części
        int firstTag = rand.nextInt(32);
        int secondTag = firstTag;
        while (secondTag == firstTag)
        secondTag = rand.nextInt(32);
        if (firstTag > secondTag){
            int tmp = firstTag;
            firstTag = secondTag;
            secondTag = tmp;
        }
        //przepisuję geny od rodziców
        for (int i = 0; i < 32; i++){
            if (i <= firstTag)
                this.genes[i] = strongerGenes[i];
            else if(i < secondTag)
                this.genes[i] = weakerGenes[i];
            else this.genes[i] = strongerGenes[i];
            this.directions[this.genes[i]] += 1;
        }
        validate();
    }

    private void validate(){
        //szukam czy istnieje brakujący kierunek, jeśli tak to aktualizuję tablicę genów
        for (int i = 0; i < 8; i++){
            if (this.directions[i] == 0){
                updateDirections(i);
            }
        }
        Arrays.sort(this.genes);
    }

    private void updateDirections(int missingDirection){
        int directionToChange = rand.nextInt(8);

        //losuje kierunek który może zostać zmieniony
        while (this.directions[directionToChange] == 0)
            directionToChange = rand.nextInt(8);

        //zastępuje kierunek
        for(int i = 0; i < 32; i++){
            if (this.genes[i] == directionToChange){
                this.genes[i] = missingDirection;
                break;
            }
        }
    }
}