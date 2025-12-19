package com.viktor;

import com.viktor.model.SportsFacility;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("SqlResolve")
public class SqliteRepository {
    private static final String CONNECTION_STR = "jdbc:sqlite:Sports.db";

    /**
     * Создание таблицы
     */
    public void initSchema() throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             Statement stmt = conn.createStatement()) {

            String sql = """
                        CREATE TABLE IF NOT EXISTS facilities (
                            id INTEGER PRIMARY KEY,
                            csv_id INTEGER,
                            name TEXT,
                            region TEXT,
                            address TEXT,
                            reg_date TEXT
                        )
                    """;
            stmt.execute(sql);
        }
    }

    /**
     * Batch вставка данных
     */
    public void saveAll(List<SportsFacility> list) throws SQLException {
        String insertSql = """
                    INSERT INTO facilities (csv_id, name, region, address, reg_date)
                    VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            conn.setAutoCommit(false);

            for (int i = 0; i < list.size(); i++) {
                SportsFacility f = list.get(i);
                ps.setInt(1, f.getId());
                ps.setString(2, f.getName());
                ps.setString(3, f.getRegion());
                ps.setString(4, f.getFullAddress());
                ps.setString(5, f.getRegistryDate());

                ps.addBatch();

                if (i % 500 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        }
    }

    /**
     * Получить статистику по регионам.
     * Объединяет Москву и Московскую область.
     */
    public Map<String, Integer> getRegionStats() throws SQLException {
        Map<String, Integer> rawStats = new HashMap<>();

        String query = """
                    SELECT region, COUNT(*) as cnt
                    FROM facilities
                    GROUP BY region
                """;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String region = rs.getString("region");
                int count = rs.getInt("cnt");

                if (region == null) continue;

                // Логика объединения Москвы и области
                if (region.equalsIgnoreCase("Москва") || region.equalsIgnoreCase("Московская область")) {
                    rawStats.merge("Москва и Московская обл.", count, Integer::sum);
                } else {
                    rawStats.merge(region, count, Integer::sum);
                }
            }
        }

        // Сортировка по убыванию для удобства
        return rawStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}