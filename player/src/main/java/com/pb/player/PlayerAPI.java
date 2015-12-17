package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.dao.HandId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
@RestController
public class PlayerAPI {
    @Autowired
    PlayerController controller;

    @RequestMapping(value = "/play/{id}/{betround}", method = RequestMethod.GET)
    public ResponseEntity<String> index(@PathVariable String id, @PathVariable String betround) throws IOException {
        try {
            controller.play(HandId.of(id), betround);
        } catch (HandValidationException ex) {
            String val = "Snapshot failed validation:" + ex.getReason();
            return new ResponseEntity<>(val, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler({SerializationException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Snapshot failed validation")
    @ExceptionHandler(HandValidationException.class)
    public void invalidSnapshot() {
    }
}
