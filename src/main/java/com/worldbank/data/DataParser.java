package com.worldbank.data;

public abstract class DataParser<T> {

    public abstract T process(String data, String[] args);

}
