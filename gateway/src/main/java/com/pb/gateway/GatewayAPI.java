package com.pb.gateway;

import com.pb.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 */
@RestController
public class GatewayAPI {
    private final static String GATEWAY_TYPE = "gateway";
    @Autowired
    GatewayController controller;

    @RequestMapping(value = "/tablestate/{id}/{datatype}", method = RequestMethod.POST)
    public String index(@PathVariable String id, @PathVariable String datatype, @RequestBody String body) {
        ApiResponse validation = controller.setSnapshot(id, datatype, body);
        return validation.toString();
    }

}
