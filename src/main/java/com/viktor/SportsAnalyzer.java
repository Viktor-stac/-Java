package com.viktor;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Главный класс приложения для анализа спортивных данных.
 */
public class SportsAnalyzer {
    public static void main(String[] args) {
        try {
            // Установка кодировки консоли UTF8
            System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));

            System.out.println("Запуск анализа спортивных объектов...");

            // 1. Чтение данных
            final var sourceFile = Paths.get("Объекты спорта.csv");
            if (!sourceFile.toFile().exists()) {
                System.err.println("Файл Объекты спорта.csv не найден!");
                return;
            }

            final var dataList = CsvLoader.loadData(sourceFile);
            System.out.printf("Прочитано записей: %d%n", dataList.size());

            // 2. База данных
            final var dbFile = new File("Sports.db");
            if (dbFile.exists()) {
                dbFile.delete();
            }

            final var repository = new SqliteRepository();
            repository.initSchema();
            repository.saveAll(dataList);
            System.out.println("Данные загружены в БД.");

            // 3. Анализ
            System.out.println("\n--- Результаты анализа ---");

            // Получаем статистику по регионам (с объединенной Москвой)
            Map<String, Integer> regionStats = repository.getRegionStats();

            // А) Среднее количество объектов
            double average = regionStats.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            System.out.printf("Среднее количество объектов спорта в регионе: %.2f%n", average);

            // Б) Топ-3 региона
            System.out.println("\nТоп-3 региона по количеству объектов:");
            regionStats.entrySet().stream()
                    .limit(3)
                    .forEach(entry -> System.out.printf("- %s: %d%n", entry.getKey(), entry.getValue()));

            // 4. График
            final var visualizer = new StatsVisualizer();
            visualizer.createBarChart(regionStats);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}