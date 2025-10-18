package com.movieweb.movieweb.modules.country.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.country.dto.CountryDto;
import com.movieweb.movieweb.modules.country.entity.Country;
import com.movieweb.movieweb.modules.country.service.CountryService;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import com.movieweb.movieweb.modules.auth.annotations.RoleRequired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quoc-gia")
public class CountryController {
    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }
    @PublicEndpoint()
    @GetMapping
    public ResponseEntity<ApiResponse<List<Country>>> getAll() {
        List<Country> countries = service.getAll();
        return ResponseEntity.ok(ResponseHelper.success(countries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Country>> getById(@PathVariable String id) {
        Country country = service.getById(id);
        return ResponseEntity.ok(ResponseHelper.success(country));
    }

    @RoleRequired("ADMIN")
    @PostMapping
    public ResponseEntity<ApiResponse<Country>> create(@RequestBody CountryDto dto) {
        Country created = service.create(dto);
        return ResponseEntity.ok(ResponseHelper.success(created));
    }

    @RoleRequired("ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Country>> update(@PathVariable String id, @RequestBody CountryDto dto) {
        Country updated = service.update(id, dto);
        return ResponseEntity.ok(ResponseHelper.success(updated));
    }

    @RoleRequired("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Country>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ResponseHelper.success());
    }
}