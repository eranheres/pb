package com.pb.gateway;

import com.pb.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pb.model.TableState;
import com.pb.model.TableStateDao;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 */
@RestController
public class GatewayAPI {
    private final static String GATEWAY_TYPE = "gateway";
    @Autowired
    GatewayController controller;

    @RequestMapping(value = "/tablestate/{id}/{datatype}", method = RequestMethod.POST)
    public String index(@PathVariable String id, @PathVariable String datatype, @RequestBody String body) {
        ValidationQuery.Ret validation = controller.setSnapshot(id, datatype, body);
        ApiResponse res = new ApiResponse(validation.getIsSuccess(), GATEWAY_TYPE, validation.getDescription());
        return res.toString();
    }

}
