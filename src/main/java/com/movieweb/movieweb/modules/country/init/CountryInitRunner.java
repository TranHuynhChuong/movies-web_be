package com.movieweb.movieweb.modules.country.init;

import com.movieweb.movieweb.modules.country.dto.CountryDto;
import com.movieweb.movieweb.modules.country.service.CountryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountryInitRunner implements CommandLineRunner {

    private final CountryService countryService;

    public CountryInitRunner(CountryService countryService) {
        this.countryService = countryService;
    }

    List<String> countries = List.of(
            "Việt Nam",
            "Hoa Kỳ",
            "Nhật Bản",
            "Hàn Quốc",
            "Pháp",
            "Anh",
            "Trung Quốc"
    );

    @Override
    public void run(String... args) throws Exception {
        if (countryService.count() == 0) {
            countries.stream()
                    .map(name -> {
                        CountryDto dto = new CountryDto();
                        dto.setName(name);
                        return dto;
                    })
                    .forEach(countryService::create);
        }
    }
}
