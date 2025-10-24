package com.movieweb.movieweb.modules.genre.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.genre.dto.GenreDto;
import com.movieweb.movieweb.modules.genre.entity.Genre;
import com.movieweb.movieweb.modules.genre.service.GenreService;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import com.movieweb.movieweb.modules.auth.annotations.RoleRequired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PublicEndpoint
    @GetMapping
    public ResponseEntity<ApiResponse<List<Genre>>> getAll() {
        List<Genre> genres = genreService.getAll();
        return ResponseEntity.ok(ResponseHelper.success(genres));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> getById(@PathVariable String id) {
        Genre genre = genreService.getById(id);
        return ResponseEntity.ok(ResponseHelper.success(genre));
    }

    @RoleRequired("ADMIN")
    @PostMapping
    public ResponseEntity<ApiResponse<Genre>> create(@RequestBody GenreDto dto) {
        Genre created = genreService.create(dto);
        return ResponseEntity.ok(ResponseHelper.success(created));
    }

    @RoleRequired("ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> update(@PathVariable String id, @RequestBody GenreDto dto) {
        Genre updated = genreService.update(id, dto);
        return ResponseEntity.ok(ResponseHelper.success(updated));
    }

    @RoleRequired("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> delete(@PathVariable String id) {
        genreService.delete(id);
        return ResponseEntity.ok(ResponseHelper.success());
    }
}
