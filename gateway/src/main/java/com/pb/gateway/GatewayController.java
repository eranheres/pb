package com.pb.gateway;

import com.pb.dao.PBDataSource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * Gateway controller
 */
@Service
@NoArgsConstructor
@AllArgsConstructor
public class GatewayController {

    @Autowired
    PBDataSource dataSource;

    @Autowired
    ValidationQuery query;

    public void setSnapshot(String id, String datatype, String body) throws IOException {
        String decodedBody = java.net.URLDecoder.decode(body, "UTF-8");
        dataSource.saveToList(id, decodedBody);
        ValidationQuery.validatorRes res = query.validateHand(id);
        if (!res.getValidation().toUpperCase().equals("OK"))
            throw new HandValidationException(res.getReason());
    }
}
