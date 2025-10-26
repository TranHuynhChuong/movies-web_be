package com.movieweb.movieweb.modules.server.repository;

import com.movieweb.movieweb.modules.server.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<Server, String> {
    boolean existsByName(String name);
    List<Server> findAllByNameIn(List<String> names);
}