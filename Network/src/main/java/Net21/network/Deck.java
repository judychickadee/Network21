package Net21.network;

import java.util.*;

public class Deck {
    private List<Card> cards;
    private Random random = new Random();

    public Deck() {
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        
         cards = new ArrayList<>();
        for (String suit : suits) {
        for (int j = 0; j < ranks.length; j++) {
      String path = "/images/" + suit + "/" + (j+1) + ".jpg";
            cards.add(new Card(suit, ranks[j], values[j], path));
        }
        }
    }

    public synchronized Card drawCard() {
        if (cards.isEmpty()) {
            initializeDeck(); 
            shuffle();
        }
        return cards.remove(0);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }
}