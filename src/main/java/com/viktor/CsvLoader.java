package com.viktor;

import com.viktor.model.SportsFacility;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {
    public static List<SportsFacility> loadData(Path path) throws IOException {
        List<SportsFacility> resultList = new ArrayList<>();

        @SuppressWarnings("deprecation")
        var csvFormat = CSVFormat.EXCEL.builder() // Формат Excel часто лучше справляется с мусором
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setAllowMissingColumnNames(true)
                .build();

        // Используем windows-1251
        try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("windows-1251"));
             CSVParser parser = CSVParser.parse(br, csvFormat)) {

            for (CSVRecord row : parser) {
                try {
                    // Проверка на минимальное количество данных
                    if (row.size() < 3) continue;

                    SportsFacility facility = new SportsFacility();

                    String idStr = getSafe(row, "Номер");
                    if (idStr != null && idStr.matches("\\d+")) { // Проверяем, что это число
                        facility.setId(Integer.parseInt(idStr));
                    } else {
                        continue; // Если нет ID, скорее всего это мусорная строка
                    }

                    facility.setName(getSafe(row, "Наименование"));
                    facility.setRegion(getSafe(row, "Субъект РФ"));
                    facility.setFullAddress(getSafe(row, "Полный адрес"));
                    facility.setRegistryDate(getSafe(row, "Дата занесения в реестр"));

                    resultList.add(facility);
                } catch (Exception e) {
                    System.err.println("Пропуск строки: " + e.getMessage());
                }
            }
        }
        return resultList;
    }

    // Вспомогательный метод, чтобы не падать, если колонки нет
    private static String getSafe(CSVRecord row, String header) {
        try {
            return row.get(header);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}