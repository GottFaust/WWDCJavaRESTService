package org.gottfaust.wwdc.controller.interfaces;

import java.util.regex.Pattern;

public interface IwwdcController {

    /**
     * Validates a name only includes letters, spaces, 's and -s
     *
     * @param name Name to validate
     * @return boolean valid/invalid
     */
    default boolean validateName(String name) {
        String regex = "^[a-zA-Z '-]+";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(name).matches();
    }

    /**
     * Validates an email to be string + @ + string + . + string
     *
     * @param email Email to validate
     * @return boolean valid/invalid
     */
    default boolean validateEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }

    /**
     * Validates that a string does not contain any non-word-non-digit characters
     *
     * @param str String to validate
     * @return boolean valid/invalid
     */
    default boolean validateString(String str) {
        String regex = "^[a-zA-Z0-9\\s.\\-:\"'*]+$";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    /**
     * Validates a phone number to make sure it only contains numbers
     *
     * @param phoneNumber Phone Number to validate/format
     * @return String formatted phone number
     */
    default String validatePhoneNumber(String phoneNumber) {
        String regex = "[\\D]";
        return phoneNumber.replaceAll(regex, "");
    }
}
