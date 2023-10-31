import com.worldbank.data.GDPDataParser;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GDPDataProcessingTest {

    private GDPDataParser processor;

    @BeforeEach
    void setUp() {
        processor = new GDPDataParser();
    }

    @Test
    void testInitializeWorldGDP() {
        processor.initializeWorldGDP(2000, 2002);

        assertEquals(Double.valueOf(0.00), processor.getWorldGDP().get(2000));
        assertEquals(Double.valueOf(0.00), processor.getWorldGDP().get(2001));
        assertEquals(Double.valueOf(0.00), processor.getWorldGDP().get(2002));
    }


    @Test
    void testParseJSONData_OnlyWorldData() {
        String mockData = "[" +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2020\",\"value\":78000000.00}" +
                "]";
        List<Map.Entry<String, Map<Integer, Double>>> result = processor.process(mockData, new String[]{"", "2020", "", "2020"});
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseJSONData_MissingWorldData() {
        processor.initializeWorldGDP(2000, 2002);
        String mockData = "[" +
                "{\"country\":{\"value\":\"CountryA\"},\"countryiso3code\":\"CTA\",\"date\":\"2020\",\"value\":39000000.00}" +
                "]";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> processor.process(mockData, new String[]{"", "2020", "", "2020"}));
        assertTrue(exception.getMessage().contains("World GDP Not Available"));
    }

    @Test
    void testParseJSONData_MissingFields() {
        String mockData = "[" +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2019\",\"value\":100.00}," +
                "{\"countryiso3code\":\"WLD\",\"date\":\"2020\"}" +
                "]";
        processor.process(mockData, new String[]{"", "2019", "", "2019"});
        assertEquals(null, processor.getCountryGDP().get(2020));
    }


    @Test
    void testParseJSONData_NormalData() {
        String mockData = "[" +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2020\",\"value\":78000000.00}," +
                "{\"country\":{\"value\":\"CountryA\"},\"countryiso3code\":\"CTA\",\"date\":\"2020\",\"value\":39000000.00}" +
                "]";

        processor.process(mockData, new String[]{"", "2020", "", "2020"});

        assertEquals(78000000.00, processor.getWorldGDP().get(2020));
        assertEquals(50.0, processor.getCountryGDP().get("CountryA").get(2020));
    }

    @Test
    void testMultipleYearsData() {
        String mockData = "[" +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2019\",\"value\":100.00}," +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2020\",\"value\":200.00}," +
                "{\"country\":{\"value\":\"CountryA\"},\"countryiso3code\":\"CTA\",\"date\":\"2019\",\"value\":30.00}," +
                "{\"country\":{\"value\":\"CountryA\"},\"countryiso3code\":\"CTA\",\"date\":\"2020\",\"value\":40.00}" +
                "]";

        List<Map.Entry<String, Map<Integer, Double>>> result = processor.process(mockData, new String[]{"", "2019", "", "2020"});
        assertEquals(1, result.size());

        Map<Integer, Double> gdpDataCountryA = result.get(0).getValue();
        System.out.println(result);
        assertEquals(30.0, gdpDataCountryA.get(2019));
        assertEquals(20.0, gdpDataCountryA.get(2020));
    }

    @Test
    void testSortEntriesByLastYearPercentage() {
        String mockData = "[" +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2020\",\"value\":78.00}," +
                "{\"country\":{\"value\":\"CountryA\"},\"countryiso3code\":\"CTA\",\"date\":\"2020\",\"value\":39.00}," +
                "{\"country\":{\"value\":\"CountryB\"},\"countryiso3code\":\"CTB\",\"date\":\"2020\",\"value\":195.00}" +
                "]";

        List<Map.Entry<String, Map<Integer, Double>>> sortedEntries = processor.process(mockData, new String[]{"", "2020", "", "2020"});

        assertEquals("CountryB", sortedEntries.get(0).getKey());
        assertEquals("CountryA", sortedEntries.get(1).getKey());
    }


    @Test
    void testCalculatePercentages() {
        String mockData = "[" +
                "{\"country\":{\"value\":\"World\"},\"countryiso3code\":\"WLD\",\"date\":\"2020\",\"value\":50.00}," +
                "{\"country\":{\"value\":\"CountryA\"},\"countryiso3code\":\"CTA\",\"date\":\"2020\",\"value\":25.00}," +
                "{\"country\":{\"value\":\"CountryB\"},\"countryiso3code\":\"CTB\",\"date\":\"2020\",\"value\":50.00}" +
                "]";

        processor.process(mockData, new String[]{"", "2020", "", "2020"});
        assertEquals(50.0, processor.getCountryGDP().get("CountryA").get(2020));
        assertEquals(100.0, processor.getCountryGDP().get("CountryB").get(2020));
    }

}


