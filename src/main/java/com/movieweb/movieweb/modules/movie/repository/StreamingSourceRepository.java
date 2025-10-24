package com.movieweb.movieweb.modules.movie.repository;

import com.movieweb.movieweb.modules.movie.entity.StreamingSource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamingSourceRepository extends JpaRepository<StreamingSource, Long> {

}