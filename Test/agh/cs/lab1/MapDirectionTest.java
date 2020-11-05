package agh.cs.lab1;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MapDirectionTest {
    MapDirection N = MapDirection.NORTH;
    MapDirection E = MapDirection.EAST;
    MapDirection S = MapDirection.SOUTH;
    MapDirection W = MapDirection.WEST;

    @Test
    public void nextTest(){
        assertEquals(N.next(), E);
        assertEquals(E.next(), S);
        assertEquals(S.next(), W);
        assertEquals(W.next(), N);
    }

    @Test
    public void previousTest(){
        assertEquals(N.previous(), W);
        assertEquals(E.previous(), N);
        assertEquals(S.previous(), E);
        assertEquals(W.previous(), S);
    }
}
