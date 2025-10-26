package com.movieweb.movieweb.modules.version.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.version.dto.VersionDto;
import com.movieweb.movieweb.modules.version.entity.Version;
import com.movieweb.movieweb.modules.version.service.VersionService;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import com.movieweb.movieweb.modules.auth.annotations.RoleRequired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/versions")
public class VersionController {
    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @PublicEndpoint
    @GetMapping
    public ResponseEntity<ApiResponse<List<Version>>> getAll() {
        List<Version> versions = versionService.getAll();
        return ResponseEntity.ok(ResponseHelper.success(versions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Version>> getById(@PathVariable String id) {
        Version version = versionService.getById(id);
        return ResponseEntity.ok(ResponseHelper.success(version));
    }

    @RoleRequired("ADMIN")
    @PostMapping
    public ResponseEntity<ApiResponse<Version>> create(@RequestBody VersionDto dto) {
        Version created = versionService.create(dto);
        return ResponseEntity.ok(ResponseHelper.success(created));
    }

    @RoleRequired("ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Version>> update(@PathVariable String id, @RequestBody VersionDto dto) {
        Version updated = versionService.update(id, dto);
        return ResponseEntity.ok(ResponseHelper.success(updated));
    }

    @RoleRequired("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Version>> delete(@PathVariable String id) {
        versionService.delete(id);
        return ResponseEntity.ok(ResponseHelper.success());
    }

}