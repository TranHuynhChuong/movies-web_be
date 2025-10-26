package com.movieweb.movieweb.modules.movie.repository;

import com.movieweb.movieweb.modules.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;



@Repository
public interface MovieRepository extends JpaRepository<Movie, String>, JpaSpecificationExecutor<Movie> {

}