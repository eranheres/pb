package com.pb.dao;

import com.google.common.collect.ImmutableBiMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class Card {
    public enum Suit {
        Spade,
        Club,
        Heart,
        Diamond
    }
    private @Getter Integer number;
    private @Getter Suit suit;
    private @Getter Boolean empty;

    private static final ImmutableBiMap<Character, Integer> numbersMapping =
            new ImmutableBiMap.Builder<Character, Integer>().
                    put('2', 2).
                    put('3', 3).
                    put('4', 4).
                    put('5', 5).
                    put('6', 6).
                    put('7', 7).
                    put('8', 8).
                    put('9', 9).
                    put('T', 10).
                    put('J', 11).
                    put('Q', 12).
                    put('K', 13).
                    put('A', 14).build();

    private static final ImmutableBiMap<Character, Suit> suitMapping =
            new ImmutableBiMap.Builder<Character, Suit>().
                    put('s', Suit.Spade).
                    put('c', Suit.Club).
                    put('h', Suit.Heart).
                    put('d', Suit.Diamond).build();

    public Card(String card) throws IllegalArgumentException {
        if (card.isEmpty() || card.equals("--")) {
            this.empty = true;
            return;
        }
        if (card.length() != 2)
            throw new IllegalArgumentException("must be 2 characters exactly");

        this.empty = false;
        this.number = numbersMapping.get(card.charAt(0));
        this.suit   = suitMapping.get(card.charAt(1));
        if (this.number == null || this.suit == null)
            throw new IllegalArgumentException("Invalid card number or suit");
    }

    public boolean isEmpty() {
        return getEmpty();
    }

    @Override
    public String toString() {
        if (this.empty)
            return "--";
        Character num = numbersMapping.inverse().get(this.number);
        Character sut = suitMapping.inverse().get(this.suit);
        StringBuilder sb = new StringBuilder(num + sut);
        return sb.append(num).append(sut).toString();
    }
}
