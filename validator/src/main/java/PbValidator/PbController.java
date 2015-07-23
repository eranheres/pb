package PbValidator;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

@RestController
public class PbController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/tabledata", method = RequestMethod.POST)
    public String response(@RequestBody final PbTableData req) {
        return "hello";
    }
}
