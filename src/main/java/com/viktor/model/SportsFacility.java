package com.viktor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportsFacility {
    private int id;              // Номер из CSV
    private String name;         // Наименование
    private String region;       // Субъект РФ
    private String fullAddress;  // Полный адрес
    private String registryDate; // Дата занесения в реестр
}