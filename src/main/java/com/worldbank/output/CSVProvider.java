package com.worldbank.output;

import java.util.List;
import java.util.Map;

public class CSVProvider {
    public String generateCSV(List<Map.Entry<String, Map<Integer, Double>>> processedData, String[] args) {
        StringBuilder csvBuilder = new StringBuilder();
        int fromYear = Integer.parseInt(args[1]);
        int toYear = Integer.parseInt(args[3]);

        csvBuilder.append("Country");
        for (int i = fromYear; i <= toYear; i++) {
            csvBuilder.append(",").append(i);
        }
        csvBuilder.append("\n");


        for (Map.Entry<String, Map<Integer, Double>> entry : processedData) {
            csvBuilder.append(entry.getKey()); // Country Name

            Map<Integer, Double> yearlyData = entry.getValue();
            for (int i = fromYear; i <= toYear; i++) {
                csvBuilder.append(",").append(yearlyData.getOrDefault(i, 0.00));
            }
            csvBuilder.append("\n");
        }

        return csvBuilder.toString();
    }
}
