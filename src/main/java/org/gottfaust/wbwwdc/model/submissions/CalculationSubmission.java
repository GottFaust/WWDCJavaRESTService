package org.gottfaust.wbwwdc.model.submissions;

import java.util.regex.Pattern;

public class CalculationSubmission {

    /** The name of the weapon **/
    public String weaponName;

    /**
     * Default CTOR
     */
    public CalculationSubmission() {
    }

    /**
     * Validates all of the relevant fields against the strict regex
     * @return valid/invalid
     */
    public boolean validate(){
        boolean isValid = true;
        if(!valid(weaponName)){ isValid = false; }
        return isValid;
    }

    /**
     * Validates that a string does not contain any non-word-non-digit characters
     * @param str String to valid
     * @return boolean valid/invalid
     */
    private boolean valid(String str){
        //Validate the string if it's not null, otherwise it's valid
        if(notNullOrEmpty(str)) {
            String regex = "^[a-zA-Z0-9\\s.\\-:\"',.;]+$";
            final Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(str).matches();
        }else{
            return true;
        }
    }

    /**
     * Checks to see if the string is not null or empty
     * @param str String to check
     * @return Boolean result
     */
    private boolean notNullOrEmpty(String str){
        return (str != null && !str.equals(""));
    }

}
