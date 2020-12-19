package agh.cs.lab1;

public enum AnimalType {
    //typ wszystkich zwierząt jest ustawiony na default, chyba że jakieś zwierze zostanie wybrane do śledzenia
    // wtedy jego typ zmienia się na selected, a jego dzieci i potomków na child i descendant
    DEFAULT,
    SELECTED,
    CHILD,
    DESCENDANT
}
