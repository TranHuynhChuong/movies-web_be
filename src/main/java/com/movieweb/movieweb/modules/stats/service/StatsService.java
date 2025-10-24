package com.movieweb.movieweb.modules.stats.service;

import com.movieweb.movieweb.modules.country.repository.CountryRepository;
import com.movieweb.movieweb.modules.genre.repository.GenreRepository;
import com.movieweb.movieweb.modules.movie.repository.MovieRepository;
import com.movieweb.movieweb.modules.server.repository.ServerRepository;
import com.movieweb.movieweb.modules.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final VersionRepository versionRepository;
    private final ServerRepository serverRepository;
    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getGeneralStats() {
        long movies = movieRepository.count();
        long genres = genreRepository.count();
        long countries = countryRepository.count();
        long versions = versionRepository.count();
        long servers = serverRepository.count();

        // Dung lượng thật của database
        Long usedStorageBytes = jdbcTemplate.queryForObject(
                "SELECT pg_database_size(current_database())", Long.class);
        double usedStorageMB = usedStorageBytes != null ? usedStorageBytes / 1024.0 / 1024.0 : 0.0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("movies", movies);
        stats.put("genres", genres);
        stats.put("countries", countries);
        stats.put("versions", versions);
        stats.put("servers", servers);
        stats.put("usedStorageMB", usedStorageMB);

        return stats;
    }

}