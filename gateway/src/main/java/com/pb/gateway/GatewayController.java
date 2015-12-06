package com.pb.gateway;

import com.pb.api.ApiResponse;
import com.pb.dao.PBDataSource;
import com.pb.dao.SnapshotJSONSerialize;
import com.pb.dao.TableState;
import com.pb.dao.TableStateDao;
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
    PBDataSource dataSource;

    @Autowired
    ValidationQuery query;

    public ApiResponse setSnapshot(String id, String datatype, String body) {
        try {
            String decodedBody = java.net.URLDecoder.decode(body, "UTF-8");
            dataSource.saveToList(id, decodedBody);
            return query.validateHand(id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ApiResponse(false, "gateway", "Failed to decode body");
        }
    }
}
