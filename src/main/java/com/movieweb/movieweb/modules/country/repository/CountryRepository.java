package com.movieweb.movieweb.modules.country.repository;

import com.movieweb.movieweb.modules.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    boolean existsByName(String name);
}