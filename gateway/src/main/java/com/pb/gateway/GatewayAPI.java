package com.pb.gateway;

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
public class GatewayAPI {
    @Autowired
    GatewayController controller;

    @RequestMapping(value = "/tablestate/{id}/{datatype}", method = RequestMethod.POST)
    public ResponseEntity<String> index(@PathVariable String id,
                                        @PathVariable String datatype,
                                        @RequestBody String body) throws IOException {
        controller.setSnapshot(id, datatype, body);
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @ExceptionHandler({SerializationException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Snapshot is invalid")
    @ExceptionHandler(HandValidationException.class)
    public String invalidSnapshot() {
        return "bla bla";
    }
}
