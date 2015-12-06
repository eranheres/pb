package com.pb.gateway;

import com.pb.model.TableState;
import com.pb.model.TableStateDao;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


/**
 * Gateway controller
 */
@Service
@NoArgsConstructor
@AllArgsConstructor
public class GatewayController {

    @Autowired
    TableStateDao tableStateDao;

    @Autowired
    ValidationQuery query;

    public ValidationQuery.Ret setSnapshot(String id, String datatype, String body) {
        String decodedBody;
        try {
            decodedBody = java.net.URLDecoder.decode(body, "UTF-8");
            TableState state = new TableState(id, datatype, decodedBody);
            tableStateDao.save(state);
            return query.validateHand(id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ValidationQuery.Ret(false, "Failed to decode body");
        }
    }
}
