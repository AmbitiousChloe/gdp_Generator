package com.worldbank;

import com.worldbank.api.APIInterface;
import com.worldbank.api.GDPAPIInterface;
import com.worldbank.data.DataParser;
import com.worldbank.data.GDPDataParser;
import com.worldbank.input.GDPInputValidation;
import com.worldbank.input.InputValidation;
import com.worldbank.output.CSVProvider;
import com.worldbank.output.Writer;
import com.worldbank.output.WriterFactory;

import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {

        InputValidation validate = new GDPInputValidation();
        if(!validate.validator(args))System.exit(1);

        APIInterface handler = new GDPAPIInterface();
        String data = (String) handler.fetchData(args);
        if(data == null) System.exit(1);


        DataParser processor = new GDPDataParser();
        List<Map.Entry<String, Map<Integer, Double>>> sortedData =
                (List<Map.Entry<String, Map<Integer, Double>>>) processor.process(data, args);

        if(sortedData == null)System.exit(1);

        CSVProvider generator = new CSVProvider();
        String output = generator.generateCSV(sortedData, args);

        WriterFactory writeFactory = new WriterFactory(args[5]);
        Writer writer = writeFactory.createWriter();
        writer.write(output);

        System.exit(0);
    }
}
