package com.worldbank.input;

public class GDPInputValidation extends InputValidation {
    @Override
    public boolean validator(String[] args){
        if(args.length != 4) {
            System.out.println("Incorrect number of arguments.");
            return false;
        }
        if(!args[0].equals("--from-year") || !args[2].equals("--to-year")) {
            System.out.println("Invalid arguments provided.");
            return false;
        }
        String regex = "^\\d{4}$";
        if(!args[1].matches(regex) || !args[3].matches(regex)) {
            System.out.println("Year should be a 4-digit number.");
            return false;
        }
        try{
            int fromYear = Integer.parseInt(args[1]);
            int toYear = Integer.parseInt(args[3]);
            if(fromYear >= toYear) {
                System.out.println("'from-year' should be less than to 'to-year'.");
                return false;
            }
        }catch (NumberFormatException e){
            System.out.println("Provided years are not valid numbers.");
            return false;
        }
        return true;
    }
}
