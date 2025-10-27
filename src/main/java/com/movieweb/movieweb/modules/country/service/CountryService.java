package com.movieweb.movieweb.modules.country.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.country.dto.CountryDto;
import com.movieweb.movieweb.modules.country.entity.Country;
import com.movieweb.movieweb.modules.country.repository.CountryRepository;
import com.movieweb.movieweb.modules.genre.entity.Genre;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository repository) {
        this.countryRepository = repository;
    }
    @Cacheable(value = "countriesAll")
    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    @Cacheable(value = "countriesById", key = "#id")
    public Country getById(String id) {
        return countryRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tim thấy quốc gia"));
    }


    public long count() {
        return countryRepository.count();
    }
    @CacheEvict(value = "countriesAll", allEntries = true)
    public Country create(CountryDto dto) {
        String name = dto.getName();

        if (countryRepository.existsByName(name)) {
            throw new ConflictException();
        }

        String id;
        do {
            id = Generator.generateId(5);
        } while (countryRepository.existsById(id));

            Country country = Country.builder()
                    .id(id)
                    .name(dto.getName())
                    .build();

            return countryRepository.save(country);
    }

    @CacheEvict(value = {"countriesAll", "countriesById"}, allEntries = true, key = "#id")
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

    @CacheEvict(value = {"countriesAll", "countriesById"}, allEntries = true, key = "#id")
    public void delete(String id) {
        countryRepository.deleteById(id);
    }

    public List<Country> createAll(List<CountryDto> dtos) {
        List<String> names = dtos.stream()
                .map(CountryDto::getName)
                .toList();

        List<String> existingNames = countryRepository.findAllByNameIn(names)
                .stream()
                .map(Country::getName)
                .toList();

        List<Country> countriesToSave = dtos.stream()
                .filter(dto -> !existingNames.contains(dto.getName()))
                .map(dto -> {
                    String id;
                    do {
                        id = Generator.generateId(5);
                    } while (countryRepository.existsById(id));

                    return Country.builder()
                            .id(id)
                            .name(dto.getName())
                            .build();
                })
                .toList();

        return countryRepository.saveAll(countriesToSave);
    }


    public List<Country> getAllByIds(List<String> countriesIds) {
        List<Country> countries = countryRepository.findAllById(countriesIds);

        if (countries.size() != countriesIds.size()) {
            // Tìm các id không tồn tại
            List<String> foundIds = countries.stream()
                    .map(Country::getId)
                    .toList();
            List<String> notFoundIds = countriesIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new RuntimeException("Country not found for IDs: " + notFoundIds);
        }

        return countries;
    }
}