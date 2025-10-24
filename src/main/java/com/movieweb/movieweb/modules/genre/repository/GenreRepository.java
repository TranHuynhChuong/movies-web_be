package com.movieweb.movieweb.modules.genre.repository;

import com.movieweb.movieweb.modules.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    boolean existsByName(String name);
}