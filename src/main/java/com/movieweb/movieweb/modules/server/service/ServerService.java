package com.movieweb.movieweb.modules.server.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.server.dto.ServerDto;
import com.movieweb.movieweb.modules.server.entity.Server;
import com.movieweb.movieweb.modules.server.repository.ServerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository repository) {
        this.serverRepository = repository;
    }
    @Cacheable(value = "serversAll")
    public List<Server> getAll() {
        return serverRepository.findAll();
    }
    @Cacheable(value = "serversById", key = "#id")
    public Server getById(String id) {
        return serverRepository.findById(id).orElseThrow(() -> new NotFoundException("Server not found"));
    }

    public long count() {
        return serverRepository.count();
    }
    @CacheEvict(value = "serversAll", allEntries = true)
    public Server create(ServerDto dto) {
        String name = dto.getName();

        if (serverRepository.existsByName(name)) {
            throw new ConflictException();
        }

        String id;
        do {
            id = Generator.generateId(5);
        } while (serverRepository.existsById(id));

        Server server = Server.builder()
                .id(id)
                .name(dto.getName())
                .build();

        return serverRepository.save(server);
    }
    @CacheEvict(value = {"serversAll", "serversById"}, allEntries = true, key = "#id")
    public Server update(String id, ServerDto dto) {
        Server existing = serverRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        String newName = dto.getName();

        if (!existing.getName().equalsIgnoreCase(newName) &&
                serverRepository.existsByName(newName)) {
            throw new ConflictException();
        }

        existing.setName(dto.getName());

        return serverRepository.save(existing);
    }
    @CacheEvict(value = {"serversAll", "serversById"}, allEntries = true, key = "#id")
    public void delete(String id) {
        serverRepository.deleteById(id);
    }

    public List<Server> createAll(List<ServerDto> dtos) {
        List<String> names = dtos.stream()
                .map(ServerDto::getName)
                .toList();

        List<String> existingNames = serverRepository.findAllByNameIn(names)
                .stream()
                .map(Server::getName)
                .toList();

        List<Server> serversToSave = dtos.stream()
                .filter(dto -> !existingNames.contains(dto.getName()))
                .map(dto -> {
                    String id;
                    do {
                        id = Generator.generateId(5);
                    } while (serverRepository.existsById(id));

                    return Server.builder()
                            .id(id)
                            .name(dto.getName())
                            .build();
                })
                .toList();

        return serverRepository.saveAll(serversToSave);
    }
}