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

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository repository) {
        this.countryRepository = repository;
    }

    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    public Country getById(String id) {
        return countryRepository.findById(id).orElseThrow(NotFoundException::new);
    }


    public long count() {
        return countryRepository.count();
    }

    public Country create(CountryDto dto) {
        String name = dto.getName();

        if (countryRepository.existsByName(name)) {
            throw new ConflictException();
        }

        Country country = Country.builder()
                .id(generateId(dto.getName()))
                .name(dto.getName())
                .build();

        return countryRepository.save(country);
    }

    public Country update(String id, CountryDto dto) {
        Country existing = countryRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        String newName = dto.getName();

        if (!existing.getName().equalsIgnoreCase(newName) &&
                countryRepository.existsByName(newName)) {
            throw new ConflictException();
        }

        existing.setName(dto.getName());

        return countryRepository.save(existing);
    }

    public void delete(String id) {
        countryRepository.deleteById(id);
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