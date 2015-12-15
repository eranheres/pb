package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.PBDataSource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * Player controller
 */
@Service
@NoArgsConstructor
@AllArgsConstructor
public class PlayerController {

    @Autowired
    PBDataSource dataSource;

    public void play(String id, String betround) throws IOException {
        validatePlayValues(id, betround);
        // TODO !!! - start playing
    }

    private void validatePlayValues(String id, String betround) {
    }
}
