package com.movieweb.movieweb.modules.version.init;

import com.movieweb.movieweb.modules.version.dto.VersionDto;
import com.movieweb.movieweb.modules.version.service.VersionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VersionInitRunner implements CommandLineRunner {

    private final VersionService versionService;

    public VersionInitRunner(VersionService versionService) {
        this.versionService = versionService;
    }

    private final List<String> versions = List.of(
            "Thuyết minh",
            "Lồng tiếng",
            "Phụ đề",
            "Bản gốc"
    );

    @Override
    public void run(String... args) throws Exception {
//        if (versionService.count() == 0) {
//            List<VersionDto> dtos = versions.stream()
//                    .map(name -> {
//                        VersionDto dto = new VersionDto();
//                        dto.setName(name);
//                        return dto;
//                    })
//                    .toList();
//
//            versionService.createAll(dtos);
//        }
    }
}