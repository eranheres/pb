package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.dao.GameOp;
import com.pb.dao.HandId;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
    Logger logger = LoggerFactory.getLogger(PlayerAPI.class.getName());

    @RequestMapping(value = "/play/{id}/{betround}", method = RequestMethod.POST)
    public ResponseEntity<String> index(@PathVariable String id, @PathVariable String betround) throws IOException {
        logger.info(id+" - Request for play betround "+betround);
        try {
            GameOp op = controller.play(HandId.of(id), betround);
            logger.info(id+" - Request for play response betround"+betround+":"+op.toString());
            return new ResponseEntity<>(ApiResponse(op), HttpStatus.OK);
        } catch (HandValidationException ex) {
            String val = "Snapshot failed validation:" + ex.getReason();
            logger.info("Error in request for play response "+id+" "+betround+":"+val);
            return new ResponseEntity<>(val, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler({SerializationException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Snapshot failed validation")
    @ExceptionHandler(HandValidationException.class)
    public void invalidSnapshot() {
    }

    private String ApiResponse(GameOp op) {
        return "{ \"status\" : \"ok\", \"action\":\""+op.getOp()+"\", \"raise\":"+String.valueOf(op.getAmount())+"}";
    }

}
