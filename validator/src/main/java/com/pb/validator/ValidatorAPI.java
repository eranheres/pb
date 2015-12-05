package com.pb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Validator Rest API controller
 */
@RestController
public class ValidatorAPI {
    @Autowired
    ValidatorController controller;

    @RequestMapping(value = "/validate/snapshot/{id}", method = RequestMethod.GET)
    public String index(@PathVariable String id) {
        try {
            ValidatorStatus status = controller.validateSnapshot(id);
            return status.getDescription();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
