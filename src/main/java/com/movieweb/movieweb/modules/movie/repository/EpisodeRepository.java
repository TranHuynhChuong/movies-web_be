package com.movieweb.movieweb.modules.movie.repository;

import com.movieweb.movieweb.modules.movie.entity.Episode;
import com.movieweb.movieweb.modules.movie.entity.EpisodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, EpisodeId> {

    Optional<Episode> findByMovieIdAndVersionIdAndEpisodeName(String movieId, String versionId, String episodeName);
}