package com.worldbank.output;


public class WriterFactory {


    private String filename;

    public WriterFactory(String filename){
        this.filename = filename;
    }

   public Writer createWriter(){
        if (this.filename != null){
            return new FileWriter(filename);
        }else{
            return new ConsoleWriter();
        }
   }
}
