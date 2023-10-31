package com.worldbank.output;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileWriterTest {

    private String filename = "chloe.csv";
    private Writer fileWriter = new FileWriter(filename);
    @Test
    void writeToFileOnce() {
        String someContent = "abc123";
        fileWriter.write(someContent);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        assertEquals(someContent, stringBuilder.toString());
    }
}