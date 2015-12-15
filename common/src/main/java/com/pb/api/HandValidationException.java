package com.pb.api;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class HandValidationException extends IOException {
    @Getter @Setter String reason;
    public HandValidationException(String reason) {
        this.reason = reason;
    }
}
