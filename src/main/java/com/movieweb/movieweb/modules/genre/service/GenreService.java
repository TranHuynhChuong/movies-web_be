package com.movieweb.movieweb.modules.genre.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.genre.dto.GenreDto;
import com.movieweb.movieweb.modules.genre.entity.Genre;
import com.movieweb.movieweb.modules.genre.repository.GenreRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository repository) {
        this.genreRepository = repository;
    }

    public List<Genre> getAll() {
        return genreRepository.findAll();
    }

    public Genre getById(String id) {
        return genreRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public long count() {
        return genreRepository.count();
    }

    public Genre create(GenreDto dto) {
        String name = dto.getName();

        if (genreRepository.existsByName(name)) {
            throw new ConflictException();
        }

        String id;
        do {
            id = Generator.generateId(5);
        } while (genreRepository.existsById(id));


                Genre genre = Genre.builder()
                        .id(id)
                        .name(dto.getName())
                        .build();

                return genreRepository.save(genre);

    }

    public Genre update(String id, GenreDto dto) {
        Genre existing = genreRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        String newName = dto.getName();

        if (!existing.getName().equalsIgnoreCase(newName) &&
                genreRepository.existsByName(newName)) {
            throw new ConflictException();
        }

        existing.setName(dto.getName());

        return genreRepository.save(existing);
    }

    public void delete(String id) {
        genreRepository.deleteById(id);
    }

    public List<Genre> getAllByIds(List<String> genreIds) {
        List<Genre> genres = genreRepository.findAllById(genreIds);

        if (genres.size() != genreIds.size()) {
            // Tìm các id không tồn tại
            List<String> foundIds = genres.stream()
                    .map(Genre::getId)
                    .toList();
            List<String> notFoundIds = genreIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new RuntimeException("Genres not found for IDs: " + notFoundIds);
        }

        return genres;
    }
}
