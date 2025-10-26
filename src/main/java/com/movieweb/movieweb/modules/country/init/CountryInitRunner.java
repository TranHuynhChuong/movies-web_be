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
            // Đông Á
            "Việt Nam",
            "Nhật Bản",
            "Hàn Quốc",
            "Trung Quốc",
            "Hồng Kông",
            "Đài Loan",

            // Đông Nam Á
            "Thái Lan",
            "Philippines",
            "Malaysia",
            "Indonesia",
            "Singapore",

            // Nam Á
            "Ấn Độ",
            "Pakistan",
            "Bangladesh",

            // Âu - Mỹ
            "Hoa Kỳ",
            "Anh",
            "Pháp",
            "Đức",
            "Ý",
            "Tây Ban Nha",
            "Nga",
            "Canada",

            // Châu Đại Dương
            "Úc",
            "New Zealand",

            // Trung Đông & Bắc Phi
            "Thổ Nhĩ Kỳ",
            "Iran",
            "Ai Cập",

            // Mỹ Latin
            "Mexico",
            "Brazil",
            "Argentina"
    );

    @Override
    public void run(String... args) throws Exception {
        if (countryService.count() == 0) {
            List<CountryDto> dtos = countries.stream()
                    .map(name -> {
                        CountryDto dto = new CountryDto();
                        dto.setName(name);
                        return dto;
                    })
                    .toList();

            countryService.createAll(dtos);
        }
    }

}
