package agh.cs.lab1;

public class World {

    public static void main(String[] args) {
        try {
            Genotype g = new Genotype();
            Genotype b = new Genotype();
            Genotype z = new Genotype(g, b);
            System.out.println(g.toString());
            System.out.println(b.toString());
            System.out.println(z.toString());

        } catch (Exception e) {
            System.out.println("Program forced to stop due to exceptions");
            e.printStackTrace();
            System.exit(1);
        }
    }
}