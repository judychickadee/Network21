package Network21;

import java.util.*;

class Card {
    String suit;
    String rank;
    int value;
    String path;
    
    public Card(String suit, String rank, int value, String path) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
        this.path = path;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public int getValue(){
        return value;
    }
}
