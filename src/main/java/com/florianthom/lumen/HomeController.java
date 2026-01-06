package com.florianthom.lumen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HomeController {

    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("")
    public ResponseEntity<String> index() {
        logger.error("my error");
        return ResponseEntity.ok("<h1>ping1</h1>");
    }
}
