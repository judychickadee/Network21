package Net21.network;

import java.util.*;

class Deck {
    private List<Card> cards;

    public Deck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"Ace","2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
        int[] values = {1,2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

        cards = new ArrayList<>();
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                String path = "images/" + suits[i] + "/" + ranks[j];
                cards.add(new Card(suits[i], ranks[j], values[j], path));
            }
        }
        Collections.shuffle(cards);
    }
    public Card drawCard() {
        return cards.remove(0);
    }
}
