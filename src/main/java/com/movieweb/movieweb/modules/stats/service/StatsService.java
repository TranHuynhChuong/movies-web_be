package com.movieweb.movieweb.modules.stats.service;

import com.movieweb.movieweb.modules.country.repository.CountryRepository;
import com.movieweb.movieweb.modules.genre.repository.GenreRepository;
import com.movieweb.movieweb.modules.movie.repository.MovieRepository;
import com.movieweb.movieweb.modules.server.repository.ServerRepository;
import com.movieweb.movieweb.modules.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class StatsService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final VersionRepository versionRepository;
    private final ServerRepository serverRepository;
    private final JdbcTemplate jdbcTemplate;



}