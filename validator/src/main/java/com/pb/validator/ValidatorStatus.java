package com.pb.validator;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Map;

@lombok.Getter
public class ValidatorStatus {
    public final static ValidatorStatus OK = new ValidatorStatus("OK");
    public final static ValidatorStatus NOT_FOUND = new ValidatorStatus("Not found");
    public final static ValidatorStatus INTERNAL_ERROR = new ValidatorStatus("Internal error");

    private String description;
    private Map<String, Object> args;

    public ValidatorStatus(String description) {
        this.description = description;
    }
    public ValidatorStatus args(Map<String, Object> args) {
        this.args = args;
        return this;
    }
    public ValidatorStatus args(String str, Object value) {
        this.args(ImmutableMap.of(str, value));
        return this;
    }
    public ValidatorStatus args(String str1, Object value1, String str2, Object value2) {
        this.args(ImmutableMap.of(str1, value1, str2, value2));
        return this;
    }

    @Override
    public String toString() {
        return "ValidatorStatus{" +
                "description='" + description + '\'' +
                ", args=" + String.valueOf(args) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidatorStatus that = (ValidatorStatus) o;

        return description.equals(that.description);

    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
