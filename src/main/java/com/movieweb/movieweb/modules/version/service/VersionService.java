package com.movieweb.movieweb.modules.version.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.version.dto.VersionDto;
import com.movieweb.movieweb.modules.version.entity.Version;
import com.movieweb.movieweb.modules.version.repository.VersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    public VersionService(VersionRepository repository) {
        this.versionRepository = repository;
    }

    public List<Version> getAll() {
        return versionRepository.findAll();
    }

    public Version getById(String id) {
        return versionRepository.findById(id).orElseThrow(() -> new NotFoundException("Version not found"));
    }

    public long count() {
        return versionRepository.count();
    }

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

    public void delete(String id) {
        versionRepository.deleteById(id);
    }

}