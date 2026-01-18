package com.florianthom.lumen;

import com.florianthom.petstore.javaClient.api.PetApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final LumenLogger llogger = new LumenLogger(HomeController.class);
    private final PetApi petApi;

    public HomeController(PetApi petApi) {
        this.petApi = petApi;
    }

    @GetMapping("")
    public ResponseEntity<String> index() {
        logger.error("my error");
        logger.makeLoggingEventBuilder(Level.ERROR).addKeyValue("testkey", "testvalue").log("log message1");
        logger.atError().addKeyValue("error key", "error value").log("log message2");
        llogger.validated("myInvoiceId");
        return ResponseEntity.ok("<h1>ping1</h1>");
    }

    @GetMapping("testwebclient")
    public ResponseEntity<String> testWebClient() {
        petApi.getPetById(0L).block();
        return ResponseEntity.ok("<h1>testwebclient</h1>");
    }


}
