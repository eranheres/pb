package PbValidator;

import java.util.ArrayList;
import java.util.List;

public class PbValidator {
    public static final String STATUS_OK = "OK";
    private List<PbValidation> validations;
    public PbValidator() {
        validations = new ArrayList<>();
    }

    public void addValidation(PbValidation validation) {
        validations.add(validation);
    }

    public String validateAll(PbTableData previous, PbTableData current) {
        for (PbValidation validation : validations) {
            String res = validation.validate(previous, current);
            if (!res.equals(STATUS_OK))
                return res;
        }
        return STATUS_OK;
    }
}
