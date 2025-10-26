package com.movieweb.movieweb.modules.movie.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import com.movieweb.movieweb.modules.auth.annotations.RoleRequired;
import com.movieweb.movieweb.modules.movie.dto.MovieDto;
import com.movieweb.movieweb.modules.movie.entity.Movie;
import com.movieweb.movieweb.modules.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    @RoleRequired("ADMIN")
    @PostMapping
    public ResponseEntity<ApiResponse<Movie>> create(@RequestBody MovieDto dto) {
        Movie created = movieService.createMovie(dto);
        return ResponseEntity.ok(ResponseHelper.success(created));
    }

    @RoleRequired("ADMIN")
    @PostMapping("/import-movies")
    public ResponseEntity<ApiResponse<String>> importMovies(@RequestParam("file") MultipartFile file) {
            movieService.importMoviesFromCsv(file);
            return ResponseEntity.ok(ResponseHelper.success());
    }


    @RoleRequired("ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> update(@PathVariable String id, @RequestBody MovieDto dto) {
        Movie updated = movieService.updateMovie(id, dto);
        return ResponseEntity.ok(ResponseHelper.success(updated));
    }

    @PublicEndpoint
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        Map<String, Object> movie = movieService.getById(id);
        return ResponseEntity.ok(ResponseHelper.success(movie));
    }

    @PublicEndpoint
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchMovies(
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) String countryId,
            @RequestParam(required = false) String mediaType,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "show") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "32") int limit
    ) {
        Map<String, Object> movieList = movieService.searchMovies(genreId, countryId, mediaType, title, releaseYear, sortBy, sortOrder,status, page, limit);
        return ResponseEntity.ok(ResponseHelper.success(movieList));
    }

}