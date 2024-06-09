package com.example.sbbackend.controllers;


import com.example.sbbackend.models.Filters;
import com.example.sbbackend.services.FiltersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/filters")
public class FiltersController {
    private final FiltersService filtersService;

    @GetMapping
    public ResponseEntity<List<Filters>> getAllFilters() {
        return ResponseEntity.status(200).body(filtersService.getAllFilters());
    }
    @PostMapping("/")
    public ResponseEntity<?> createNewFilter(@RequestBody Filters filterRequest) {
        return ResponseEntity.ok(filtersService.createNewFilter(filterRequest));
    }
    @DeleteMapping("/{filterid}")
    public ResponseEntity<?> deleteFilter(@PathVariable Long filterid) {

        filtersService.deleteFilter(filterid);
        return ResponseEntity.status(200).body("filter is deleted successfully");
    }


}
