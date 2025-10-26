package com.movieweb.movieweb.modules.server.controller;

import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.modules.server.dto.ServerDto;
import com.movieweb.movieweb.modules.server.entity.Server;
import com.movieweb.movieweb.modules.server.service.ServerService;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import com.movieweb.movieweb.modules.auth.annotations.RoleRequired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/servers")
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PublicEndpoint
    @GetMapping
    public ResponseEntity<ApiResponse<List<Server>>> getAll() {
        List<Server> servers = serverService.getAll();
        return ResponseEntity.ok(ResponseHelper.success(servers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Server>> getById(@PathVariable String id) {
        Server server = serverService.getById(id);
        return ResponseEntity.ok(ResponseHelper.success(server));
    }

    @RoleRequired("ADMIN")
    @PostMapping
    public ResponseEntity<ApiResponse<Server>> create(@RequestBody ServerDto dto) {
        Server created = serverService.create(dto);
        return ResponseEntity.ok(ResponseHelper.success(created));
    }

    @RoleRequired("ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Server>> update(@PathVariable String id, @RequestBody ServerDto dto) {
        Server updated = serverService.update(id, dto);
        return ResponseEntity.ok(ResponseHelper.success(updated));
    }

    @RoleRequired("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Server>> delete(@PathVariable String id) {
        serverService.delete(id);
        return ResponseEntity.ok(ResponseHelper.success());
    }
}