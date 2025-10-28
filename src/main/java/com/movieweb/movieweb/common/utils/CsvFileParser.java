package com.movieweb.movieweb.common.utils;


import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvFileParser {
    private CsvFileParser() {}

    public static <T> List<T> parseCsv(MultipartFile file, Class<T> type) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            System.out.println("========== NỘI DUNG CSV GỐC ==========");
            file.getInputStream().transferTo(System.out);
            System.out.println("\n====================================");

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<T> records = csvToBean.parse();
            return records;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file CSV: " + e.getMessage(), e);
        }
    }

}
