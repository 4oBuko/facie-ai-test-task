package com.chornobuk.trades_api.controllers;

import com.chornobuk.trades_api.services.TradesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TradesController {

    private final TradesService service;

    @PostMapping(value = "/enrich", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> enrichTrades(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("file's empty!");
        }
        return ResponseEntity.ok(service.enrich(file));
    }
}
