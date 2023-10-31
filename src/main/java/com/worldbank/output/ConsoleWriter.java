package com.worldbank.output;

import java.io.PrintStream;

public class ConsoleWriter implements Writer {

    @Override
    public void write(String content) {
        System.out.println(content);
    }
}
