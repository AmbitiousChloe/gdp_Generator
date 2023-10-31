package com.worldbank.api;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import java.net.MalformedURLException;
import java.io.IOException;

import org.json.JSONArray;


public class GDPAPIInterface extends APIInterface<String> {

    private static final String BASE_URL = "http://api.worldbank.org/v2/country/indicator/NY.GDP.MKTP.CD?date=";

    @Override
    public String fetchData(String[] params) {
        JSONArray consolidatedData = new JSONArray();
        int page = 1;

        while (true) {
            String apiURL = String.format("%s%s:%s&format=json&per_page=1000&page=%d", BASE_URL, params[1], params[3], page);

            try {
                URL url = new URL(apiURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                }

                StringBuilder jsonStr = new StringBuilder();
                try (Scanner scanner = new Scanner(url.openStream())) {
                    while (scanner.hasNext()) {
                        jsonStr.append(scanner.nextLine());
                    }
                }

                JSONArray jsonArray = new JSONArray(jsonStr.toString());
                if (jsonArray.length() == 0 || jsonArray.getJSONArray(1).length() == 0) {
                    break;
                }

                JSONArray pageData = jsonArray.getJSONArray(1);
                for (int i = 0; i < pageData.length(); i++) {
                    consolidatedData.put(pageData.get(i));
                }

            } catch (MalformedURLException e) {
                System.out.println("Invalid URL");
                throw new RuntimeException("Invalid URL: " + apiURL, e);
            } catch (IOException e) {
                System.out.println("Error while fetching data from the API");
                throw new RuntimeException("Error while fetching data from the API", e);
            }

            page++;
        }

        return consolidatedData.toString();
    }
}
