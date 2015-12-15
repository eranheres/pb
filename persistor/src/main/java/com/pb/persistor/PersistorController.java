package com.pb.persistor;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
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
public class PersistorController {

    @Autowired
    PBDataSource dataSource;

    @Autowired
    ValidationQuery query;

    public void complete(String id) throws IOException {
        ValidationQuery.validatorRes res = query.validateHand(id);
        if (!res.getValidation().toUpperCase().equals("OK"))
            throw new HandValidationException(res.getReason());
    }
}
