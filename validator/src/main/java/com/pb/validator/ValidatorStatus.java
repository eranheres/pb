package com.pb.validator;

/**
 * Created by eranh on 11/26/15.
 */
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.ToString
@lombok.EqualsAndHashCode
public class ValidatorStatus {
    public final static ValidatorStatus OK = new ValidatorStatus("OK");
    public final static ValidatorStatus NOT_FOUND = new ValidatorStatus("Not found");
    public final static ValidatorStatus INTERNAL_ERROR = new ValidatorStatus("Internal error");
    private String description;
}
