package com.florianthom.lumen;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HomeController {

    @GetMapping("")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("<h1>ping</h1>");
    }
}
