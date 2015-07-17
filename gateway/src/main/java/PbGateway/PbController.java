package PbGateway;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

@RestController
public class PbController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public PbQueryResult query(@RequestParam(value="name", defaultValue="World") String name) {
        PbQueryResult res = new PbQueryResult(counter.incrementAndGet(),
                            String.format(template, name));
        return res;
    }

    @RequestMapping(value = "/tabledata", method = RequestMethod.POST)
    public String response(@RequestBody final PbTableData req) {
        PbTableDataRes res = new PbTableDataRes(counter.incrementAndGet(),
                            "res", "res1");
        return "hello";
    }
}
