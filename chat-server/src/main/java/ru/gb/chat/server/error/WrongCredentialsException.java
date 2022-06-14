package ru.gb.chat.server.error;

/**
 * 11.06.2022 13:32
 *
 * @author PetSoft
 */
public class WrongCredentialsException extends IllegalArgumentException {
    public WrongCredentialsException() {
    }

    public WrongCredentialsException(String s) {
        super(s);
    }
}
