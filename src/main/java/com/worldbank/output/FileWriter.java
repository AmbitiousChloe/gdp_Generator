package com.worldbank.output;
import java.io.IOException;


public class FileWriter implements Writer {
    private String filename;

    public FileWriter(String filename) {
        this.filename = filename;
    }

    @Override
    public void write(String content) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(filename);
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to file to the file: " + this.filename, e);
            }
    }
}
