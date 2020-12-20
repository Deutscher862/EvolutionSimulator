package agh.cs.lab1;

import java.util.Random;

public class Vector2d {
    public final int x;
    public final int y;
    private final Random rand  = new Random();

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public boolean precedes(Vector2d other) {
        return this.x <= other.x && this.y <= other.y;
    }

    public boolean follows(Vector2d other) {
        return this.x >= other.x && this.y >= other.y;
    }

    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(Math.max(this.x, other.x), Math.max(this.y, other.y));
    }

    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(Math.min(this.x, other.x), Math.min(this.y, other.y));
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Vector2d))
            return false;
        Vector2d that = (Vector2d) other;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash += this.x * 31;
        hash += this.y * 17;
        return hash;
    }

    public Vector2d opposite() {
        return new Vector2d(-this.x, -this.y);
    }

    public Vector2d randomVector(Vector2d other){
        //tworzenie wektora i losowych współrzędnych z podanego zakresu
        return new Vector2d(rand.nextInt(other.x-this.x) + this.x, rand.nextInt(other.y-this.y) + this.y);
    }

    public Vector2d getBackToMap(Vector2d max){
        //wektor zwracający wartość na przeciwnym krańcu mapy, załatwia to kształt torusa
        max = new Vector2d(max.x-1, max.y-1);
        if(this.follows(new Vector2d(0, 0)) && this.precedes(max)) return this;
        else if (this.x < 0) {
            if (this.y < 0) return max;
            else if( this.y > max.y) return new Vector2d(max.x,0);
            else return new Vector2d(max.x ,this.y);
        }
        else if (this.x > max.x){
            if (this.y < 0) return new Vector2d(0, max.y);
            else if(this.y > max.y) return new Vector2d(0, 0);
            else return new Vector2d(0, this.y);
        }
        else if (this.y < 0) return new Vector2d(this.x, max.y);
        else return new Vector2d(this.x, 0);
    }
}