package com.movieweb.movieweb.modules.movie.repository;

import com.movieweb.movieweb.modules.movie.entity.StreamingSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreamingSourceRepository extends JpaRepository<StreamingSource, Long> {

    Optional<StreamingSource> findByMovieIdAndVersionIdAndEpisodeNumberAndServerId(String movieId, String versionId, Integer episodeNumber, String serverId);
}