package com.movieweb.movieweb.modules.genre.repository;

import com.movieweb.movieweb.modules.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    boolean existsByName(String name);
    List<Genre> findAllByNameIn(List<String> names);
}