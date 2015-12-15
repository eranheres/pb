package com.pb.persistor;

import com.pb.api.HandValidationException;
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
public class PersistorAPI {
    @Autowired
    PersistorController controller;

    @RequestMapping(value = "/complete/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> index(@PathVariable String id) throws IOException {
        try {
            controller.complete(id);
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
