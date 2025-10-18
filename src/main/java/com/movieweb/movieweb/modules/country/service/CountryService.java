package com.movieweb.movieweb.modules.country.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.modules.country.dto.CountryDto;
import com.movieweb.movieweb.modules.country.entity.Country;
import com.movieweb.movieweb.modules.country.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    public List<Country> getAll() {
        return repository.findAll();
    }

    public Country getById(String id) {
        return repository.findById(id).orElseThrow(NotFoundException::new);
    }


    public Country create(CountryDto dto) {
        String name = dto.getName();

        if (repository.existsByName(name)) {
            throw new ConflictException();
        }

        Country country = Country.builder()
                .id(generateId(dto.getName()))
                .name(dto.getName())
                .build();

        return repository.save(country);
    }

    public Country update(String id, CountryDto dto) {
        Country existing = repository.findById(id)
                .orElseThrow(NotFoundException::new);

        String newName = dto.getName();

        if (!existing.getName().equalsIgnoreCase(newName) &&
                repository.existsByName(newName)) {
            throw new ConflictException();
        }

        existing.setName(dto.getName());

        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    private String generateId(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        String slug = normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug;
    }

}