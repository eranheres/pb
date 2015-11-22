package com.pb.gateway;

import org.springframework.web.bind.annotation.*;
import com.pb.model.TableState;
import com.pb.model.TableStateDao;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 */
@RestController
public class Gateway {

//    @Resource(name = "TableStateDaoRedis")
    @Resource
    TableStateDao dao;

    @RequestMapping(value = "/tablestate/{id}/{datatype}", method = RequestMethod.POST)
    public String index(@PathVariable String id, @PathVariable String datatype, @RequestBody String body) {
        String decodedBody;
        try {
            decodedBody = java.net.URLDecoder.decode(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Failed to decode body";
        }
        TableState state = new TableState(id, datatype, decodedBody);
        dao.save(state);
        return "service received data\n";
    }

}
