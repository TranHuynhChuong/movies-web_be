package com.movieweb.movieweb.modules.movie.dto;

public record MovieCsvRecord(
        String title,
        String originalTitle,
        String posterPath,
        String backdropPath,
        String mediaType,
        String status,
        String runtime,
        String numberOfEpisodes,
        String releaseYear,
        String trailerPath,
        String overview,
        String actors,
        String directors,
        String country,
        String genres
) {}