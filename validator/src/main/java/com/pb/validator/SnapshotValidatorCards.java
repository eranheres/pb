package com.pb.validator;

import com.pb.dao.Card;
import com.pb.dao.Snapshot;

import java.util.HashSet;
import java.util.Set;

/**
 * Validate the integerty of hand cards
 */
public class SnapshotValidatorCards implements SnapshotValidator {

    public final static ValidatorStatus PLAYER_CARDS_INVALID     = new ValidatorStatus("Player card is invalid");
    public final static ValidatorStatus DUPLICATE_CARDS_IN_TABLE = new ValidatorStatus("Duplicate card in table");
    public final static ValidatorStatus WRONG_AMOUNT_PUBLIC_CARDS = new ValidatorStatus("Invalid amount of public cards");
    public final static ValidatorStatus BETROUND_NOT_FIT_CARDS = new ValidatorStatus("Betround not fits public cards");

    private Integer countCards(Card[] cards) {
        Integer count = 0;
        for (Card card : cards) {
            if (!card.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private ValidatorStatus validateRoundBet(Snapshot snapshot) {
        Integer count = countCards(snapshot.getCards());
        if ((snapshot.getCards() == null) || (snapshot.getCards().length !=5) ||
            (count == 1) || (count ==2)) {
            return WRONG_AMOUNT_PUBLIC_CARDS;
        }
        if ((count == 0) && (!snapshot.getState().getBetround().equals(Snapshot.VALUES.PREFLOP))) {
            return BETROUND_NOT_FIT_CARDS;
        }
        if ((count == 3) && (!snapshot.getState().getBetround().equals(Snapshot.VALUES.FLOP))) {
            return BETROUND_NOT_FIT_CARDS;
        }
        if ((count == 4) && (!snapshot.getState().getBetround().equals(Snapshot.VALUES.TURN))) {
            return BETROUND_NOT_FIT_CARDS;
        }
        if ((count == 5) && (!snapshot.getState().getBetround().equals(Snapshot.VALUES.RIVER))) {
            return BETROUND_NOT_FIT_CARDS;
        }

        return ValidatorStatus.OK;
    }

    @Override
    public ValidatorStatus validate(Snapshot snapshot) {
        Set<Card> cards = new HashSet<Card>();
        // Test hand has 2 cards or empty
        for (Snapshot.Player player : snapshot.getPlayers()) {
            if (player.getCards().length != 2)
                return PLAYER_CARDS_INVALID;
            Card card1 = player.getCards()[0];
            Card card2 = player.getCards()[1];
            if ((card1.isEmpty() && !card2.isEmpty()) || (!card1.isEmpty() && card2.isEmpty()))
                return PLAYER_CARDS_INVALID;
            if (!card1.isEmpty() && !cards.add(card1)) {
                return DUPLICATE_CARDS_IN_TABLE;
            }
            if (!card2.isEmpty() && !cards.add(card2)) {
                return DUPLICATE_CARDS_IN_TABLE;
            }
        }
        int cardCount = 0;
        for (Card card : snapshot.getCards()) {
            if (!card.isEmpty()) {
                cardCount++;
            }
        }
        if ((cardCount != 0) && (cardCount != 3) && (cardCount !=4) && (cardCount!=5)) {
            return WRONG_AMOUNT_PUBLIC_CARDS;
        }

        for (Card card : snapshot.getCards()) {
            if (!card.isEmpty() && !cards.add(card)) {
                return DUPLICATE_CARDS_IN_TABLE;
            }
        }
        // Test that amount of public cards fits the roundbet value
        ValidatorStatus stat = validateRoundBet(snapshot);
        if (!stat.equals(ValidatorStatus.OK))
            return stat;
        return ValidatorStatus.OK;
    }
}
