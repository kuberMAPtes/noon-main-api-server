package com.kube.noon.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheckRestController {

    @GetMapping("/healthCheck")
    public ResponseEntity<Void> healthCheck() {

        return new ResponseEntity<>(HttpStatus.OK);
        
    }

}
