package com.pb.validator;

import com.pb.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Validator Rest API controller
 */
@RestController
public class ValidatorAPI {
    private final static String API_TYPE = "validator";
    @Autowired
    ValidatorController controller;

    @RequestMapping(value = "/validate/snapshot/{id}", method = RequestMethod.GET)
    public String snapshot(@PathVariable String id) {
        try {
            ValidatorStatus status = controller.validateSnapshot(id);
            ApiResponse response = new ApiResponse(
                    status.equals(ValidatorStatus.OK),
                    API_TYPE,
                    status.getDescription());
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse(false, API_TYPE, e.toString()).toString();
        }
    }
}
