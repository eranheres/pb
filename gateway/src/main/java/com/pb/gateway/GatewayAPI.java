package com.pb.gateway;

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

    @Autowired
    GatewayController controller;

    @RequestMapping(value = "/tablestate/{id}/{datatype}", method = RequestMethod.POST)
    public String index(@PathVariable String id, @PathVariable String datatype, @RequestBody String body) {
        return controller.setSnapshot(id, datatype, body).getDescription();
    }

}
