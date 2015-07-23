package PbValidator;

public class PbValidatorInitializer {
    static public PbValidator init() {
        PbValidator validator = new PbValidator();
        validator.addValidation(basicValidation());
        return validator;
    }

    static private PbValidation basicValidation() {
        return (previous, current) -> {
            if (current.cards.size() != 2)
                return "Hand does not have 2 cards";
            try {
                Card c1 = new Card(current.cards.get(0));
                Card c2 = new Card(current.cards.get(1));
                if (c1.equals(c2))
                    return "Hand cards are equal";
            } catch (IllegalArgumentException ex) {
                return ex.getMessage();
            }
            return PbValidator.STATUS_OK;
        };
    }
}
