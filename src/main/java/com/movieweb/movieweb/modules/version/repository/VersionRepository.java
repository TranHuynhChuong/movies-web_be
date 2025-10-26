package com.movieweb.movieweb.modules.version.repository;

import com.movieweb.movieweb.modules.version.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> {
    boolean existsByName(String name);
    List<Version> findAllByNameIn(List<String> names);
}