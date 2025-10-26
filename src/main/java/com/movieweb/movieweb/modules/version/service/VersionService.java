package com.movieweb.movieweb.modules.version.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.version.dto.VersionDto;
import com.movieweb.movieweb.modules.version.entity.Version;
import com.movieweb.movieweb.modules.version.repository.VersionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    public VersionService(VersionRepository repository) {
        this.versionRepository = repository;
    }
    @Cacheable(value = "versionsAll")
    public List<Version> getAll() {
        return versionRepository.findAll();
    }

    @Cacheable(value = "versionsById", key = "#id")
    public Version getById(String id) {
        return versionRepository.findById(id).orElseThrow(() -> new NotFoundException("Version not found"));
    }

    public long count() {
        return versionRepository.count();
    }
    @CacheEvict(value = "versionsAll", allEntries = true)
    public Version create(VersionDto dto) {
        String name = dto.getName();

        if (versionRepository.existsByName(name)) {
            throw new ConflictException();
        }

        String id;
        do {
            id = Generator.generateId(5);
        } while (versionRepository.existsById(id));

        Version version = Version.builder()
                .id(id)
                .name(dto.getName())
                .build();

        return versionRepository.save(version);
    }
    @CacheEvict(value = {"versionsAll", "versionsById"}, allEntries = true, key = "#id")
    public Version update(String id, VersionDto dto) {
        Version existing = versionRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        String newName = dto.getName();

        if (!existing.getName().equalsIgnoreCase(newName) &&
                versionRepository.existsByName(newName)) {
            throw new ConflictException();
        }

        existing.setName(dto.getName());

        return versionRepository.save(existing);
    }

    @CacheEvict(value = {"versionsAll", "versionsById"}, allEntries = true, key = "#id")
    public void delete(String id) {
        versionRepository.deleteById(id);
    }

    public List<Version> createAll(List<VersionDto> dtos) {
        List<String> names = dtos.stream()
                .map(VersionDto::getName)
                .toList();

        List<String> existingNames = versionRepository.findAllByNameIn(names)
                .stream()
                .map(Version::getName)
                .toList();

        List<Version> versionsToSave = dtos.stream()
                .filter(dto -> !existingNames.contains(dto.getName()))
                .map(dto -> {
                    String id;
                    do {
                        id = Generator.generateId(5);
                    } while (versionRepository.existsById(id));

                    return Version.builder()
                            .id(id)
                            .name(dto.getName())
                            .build();
                })
                .toList();

        return versionRepository.saveAll(versionsToSave);
    }

}