package com.viktor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class StatsVisualizer {
    public void createBarChart(Map<String, Integer> dataMap) throws IOException {
        var dataset = new DefaultCategoryDataset();

        // Берем топ-15 для графика
        dataMap.entrySet().stream()
                .limit(20)
                .forEach(e -> dataset.addValue(e.getValue(), "Объекты", e.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Количество спортивных объектов по регионам", // Заголовок
                "Регион",                                         // Ось X
                "Количество",                                     // Ось Y
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        File outputFile = new File("sports_stats.png");
        ChartUtils.saveChartAsPNG(outputFile, chart, 1600, 900);

        System.out.println("График сохранен в файл: " + outputFile.getAbsolutePath());
    }
}

