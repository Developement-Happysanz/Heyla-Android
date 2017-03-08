package com.findafun.utils;


import java.util.regex.Pattern;

public class FindAFunValidator {

    public static boolean checkNullString(String value) {
        if (value == null)
            return false;
        else
            return value.trim().length() > 0;
    }

    public static boolean checkStringMinLength(int minValue, String value) {
        if (value == null)
            return false;
        return value.trim().length() >= minValue;
    }

    public static boolean checkStringMaxLength(int maxValue, String value) {
        if (value == null)
            return false;
        return value.trim().length() <= maxValue;
    }

    public static boolean checkConfirmPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkContainsNumber(String password) {
        Pattern hasNumber = Pattern.compile("\\d");

        return hasNumber.matcher(password).find();
    }

    public static boolean checkContainsSpecialChar(String password) {
        Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
        return !hasSpecialChar.matcher(password).find();
    }

    public static boolean checkContainsCharacter(String password) {
        Pattern hasCharacter = Pattern.compile("[a-zA-Z]");
        return hasCharacter.matcher(password).find();
    }

    public static boolean withinPermittedLength(String password){
        return (password.length() > 6) && (password.length() <= 200);
    }

    public static boolean checkValidContact(String value){
        String MobilePattern = "[0-9]{10}";
        return value.matches(MobilePattern);
    }


    public static boolean checkStartWith(String value){
        Boolean result;
        String val = new String(value);
       // val =value.toLowerCase();

        if(val.toLowerCase().startsWith("select")){
            System.out.print("True" );
            result = false;
        } else {
            System.out.print("False");
            result = true;
        }

     return result;
    }

}
