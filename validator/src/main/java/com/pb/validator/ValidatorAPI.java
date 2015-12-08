package com.pb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Validator Rest API controller
 */
@RestController
public class ValidatorAPI {
    @Autowired
    ValidatorController controller;

    @RequestMapping(value = "/validate/snapshot/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> snapshot(@PathVariable String id) throws Exception {
        try {
            ValidatorStatus status = controller.validateSnapshot(id);
            HttpStatus httpStatus = HttpStatus.OK;
            if (status.equals(ValidatorStatus.NOT_FOUND))
                httpStatus = HttpStatus.NOT_FOUND;

            return new ResponseEntity<String>(responseValue(status), httpStatus);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private String responseValue(ValidatorStatus status) {
        return String.format("{\n\"validation\":\"%s\",\n\"reason\":\"%s\"\n}",
                status.equals(ValidatorStatus.OK)?"ok":"failed",
                status.getDescription());
    }
}
