package com.alexandervov.controller;

import com.alexandervov.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Method for download application.
     *
     * @param name application name
     * @return zipped application
     */
    @GetMapping("/application/download/{name}")
    public ResponseEntity<?> getApplicationArchive(@PathVariable final String name) {
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(new MediaType("application", "zip"))
            .body(applicationService.downloadApplication(name).getPackageContent());
    }
}
