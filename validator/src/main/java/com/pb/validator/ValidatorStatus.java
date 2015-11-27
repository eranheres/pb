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
    private String description;
}
