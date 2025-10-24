package com.movieweb.movieweb.modules.server.init;

import com.movieweb.movieweb.modules.server.dto.ServerDto;
import com.movieweb.movieweb.modules.server.service.ServerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerInitRunner implements CommandLineRunner {

    private final ServerService serverService;

    public ServerInitRunner(ServerService serverService) {
        this.serverService = serverService;
    }

    private final List<String> servers = List.of(
            "Phim NguồnC",
            "Dailymotion",
            "Abyss",
            "OK RU",
            "Youtube",
            "Vkontakte"
    );

    @Override
    public void run(String... args) throws Exception {
        if (serverService.count() == 0) {
            servers.stream()
                    .map(name -> {
                        ServerDto dto = new ServerDto();
                        dto.setName(name);
                        return dto;
                    })
                    .forEach(serverService::create);
        }
    }
}