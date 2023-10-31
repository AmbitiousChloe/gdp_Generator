package com.worldbank.api;

public abstract class APIInterface<T> {

    public abstract T fetchData(String[] params);

}



