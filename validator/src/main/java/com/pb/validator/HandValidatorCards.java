package com.pb.validator;

import com.google.common.collect.ArrayListMultimap;
import com.pb.dao.Card;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validate cards through out the entire hand
 */
@Component
public class HandValidatorCards implements HandValidator {

    public static final ValidatorStatus CARD_CHANGED_IN_HAND = new ValidatorStatus("Card changed in hand");

   private Boolean isPrevious(ArrayListMultimap<String, Card> current, ArrayListMultimap<String, Card> previous) {
       if (current.size() < previous.size())
           return false;
       for (String idx : previous.keySet()) {
           List<Card> previousCards = new ArrayList<>(previous.get(idx));
           List<Card> currentCards  = current.get(idx);
           if (currentCards == null)
               return false;
           previousCards.removeIf(Card::isEmpty);
           if (Collections.indexOfSubList(currentCards, previousCards) != 0)
               return false;
       }
       return true;
   }

    @Override
    public ValidatorStatus validate(Hand hand) {
        ArrayListMultimap<String, Card> previousCards = ArrayListMultimap.create();
        for (Snapshot snapshot : hand.getSnapshots()) {
            if (snapshot.getState().getDatatype().equals(Snapshot.VALUES.DATATYPE_POSTHAND))
                continue;
            ArrayListMultimap<String, Card> cards = ArrayListMultimap.create();
            for (Snapshot.Player player : snapshot.getPlayers()) {
                cards.putAll("player"+player.getName(), Arrays.asList(player.getCards()));
            }
            cards.putAll("board",Arrays.asList(snapshot.getCards()));
            if (!isPrevious(cards, previousCards))
                return CARD_CHANGED_IN_HAND;
            previousCards = cards;
        }
        return ValidatorStatus.OK;
    }
}
