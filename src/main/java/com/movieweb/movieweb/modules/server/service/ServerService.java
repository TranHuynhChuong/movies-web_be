package com.movieweb.movieweb.modules.server.service;

import com.movieweb.movieweb.common.exception.ConflictException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.server.dto.ServerDto;
import com.movieweb.movieweb.modules.server.entity.Server;
import com.movieweb.movieweb.modules.server.repository.ServerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository repository) {
        this.serverRepository = repository;
    }

    public List<Server> getAll() {
        return serverRepository.findAll();
    }

    public Server getById(String id) {
        return serverRepository.findById(id).orElseThrow(() -> new NotFoundException("Server not found"));
    }

    public long count() {
        return serverRepository.count();
    }

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

    public void delete(String id) {
        serverRepository.deleteById(id);
    }
}