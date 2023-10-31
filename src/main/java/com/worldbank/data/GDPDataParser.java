package com.worldbank.data;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GDPDataParser extends DataParser<List<Map.Entry<String, Map<Integer, Double>>>> {

    private Map<Integer, Double> worldGDP = new HashMap<>();
    private Map<String, Map<Integer, Double>> countryGDP = new HashMap<>();

    @Override
    public List<Map.Entry<String, Map<Integer, Double>>> process(String data, String[] args) {
        try {
            int fromYear = Integer.parseInt(args[1]);
            int toYear = Integer.parseInt(args[3]);

            initializeWorldGDP(fromYear, toYear);

            parseJSONData(data, fromYear, toYear);

            checkWorldGDP();

            List<Map.Entry<String, Map<Integer, Double>>> sortedData = sortEntriesByLastYearPercentage(toYear);

            calculatePercentages(sortedData);

            return sortedData;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error: " + e.getMessage());
        }
    }

    public Map<Integer, Double> getWorldGDP(){
        return this.worldGDP;
    }

    public Map<String, Map<Integer, Double>> getCountryGDP(){
        return this.countryGDP;
    }

    public void initializeWorldGDP(int fromYear, int toYear) {
        while (fromYear <= toYear) {
            worldGDP.put(fromYear, 0.00);
            fromYear++;
        }
    }

    public void checkWorldGDP(){
        for(double value : worldGDP.values()){
            if(value == 0.00){
                throw new IllegalArgumentException("World GDP Not Available");
            }
        }
    }

    public void parseJSONData(String data, int fromYear, int toYear) throws IllegalArgumentException {
        JSONArray entries = new JSONArray(data);

        int len = entries.length();
        int curr = 0;

        if (len < 1) {
            throw new IllegalArgumentException("Invalid JSON data format.");
        }

        while (curr < len) {
            JSONObject entry = entries.getJSONObject(curr);
            if (entry.has("countryiso3code") && "WLD".equals(entry.getString("countryiso3code"))) break;
            curr++;
        }

        while (curr < len) {
            JSONObject entry = entries.getJSONObject(curr);

            if (!entry.has("country") || !entry.getJSONObject("country").has("value") ||
                    !entry.has("date") || !entry.has("value")) {
                curr++;
                continue;
            }

            String countryName;
            int year;
            double gdpValue;

            try {
                countryName = entry.getJSONObject("country").getString("value");
                year = entry.getInt("date");
                gdpValue = entry.has("value") && !entry.isNull("value") ? entry.getDouble("value") : 0.00;
            } catch (JSONException e) {
                throw new JSONException("Invalid JSON data format.");
            }

            if (entry.has("countryiso3code") && "WLD".equals(entry.getString("countryiso3code"))) {
                if (worldGDP.containsKey(year)) {
                    worldGDP.put(year, gdpValue);
                    curr++;
                    continue;
                } else {
                    throw new IllegalArgumentException("Year not found in worldGDP map.");
                }
            }

            Map<Integer, Double> yearlyGDP = countryGDP.getOrDefault(countryName, new HashMap<>());
            yearlyGDP.put(year, gdpValue);
            countryGDP.put(countryName, yearlyGDP);

            curr++;

            }
        }

    public void calculatePercentages(List<Map.Entry<String, Map<Integer, Double>>> data){
        for (Map.Entry<String, Map<Integer, Double>> entry: data){
            Map<Integer, Double> innerValue = entry.getValue();
            for(Integer year: innerValue.keySet()){
                if(innerValue.containsKey(year) && worldGDP.containsKey(year)){
                    Double percentage = innerValue.get(year) / worldGDP.get(year) * 100;
                    percentage = Math.round(percentage * 100.00) / 100.00;
                    innerValue.put(year, percentage);
                }else{
                    innerValue.put(year, 0.00);
                }
            }
        }
    }

    public List<Map.Entry<String, Map<Integer, Double>>> sortEntriesByLastYearPercentage(int toYear){
        List<Map.Entry<String, Map<Integer, Double>>> sortedEntries = new ArrayList<>(countryGDP.entrySet());
        sortedEntries.sort((a, b) -> {
            double lastYearA = a.getValue().getOrDefault(toYear, 0.0);
            double lastYearB = b.getValue().getOrDefault(toYear, 0.0);

            int primaryComparison = Double.compare(lastYearB, lastYearA);
            if (primaryComparison != 0) return primaryComparison;
            return a.getKey().compareTo(b.getKey());
        });
        return sortedEntries;
    }
}
