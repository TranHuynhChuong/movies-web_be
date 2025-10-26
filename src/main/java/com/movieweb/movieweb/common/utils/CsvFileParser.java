package com.movieweb.movieweb.common.utils;

import com.movieweb.movieweb.common.exception.BadRequestException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class CsvFileParser {

    private CsvFileParser() {}

    public static <T> List<T> parseCsvFile(MultipartFile file, Function<CSVRecord, T> mapper) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
        )) {
            CSVFormat csvFormat = CSVFormat.Builder.create()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
                List<T> result = new ArrayList<>();
                for (CSVRecord record : csvParser) {
                    result.add(mapper.apply(record));
                }
                return result;
            }
        } catch (IOException e) {
            throw new BadRequestException("Lỗi đọc file CSV: " + e.getMessage());
        }
    }
}
