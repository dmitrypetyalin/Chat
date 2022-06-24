package ru.gb.chat.server.error;

/**
 * 20.06.2022 11:25
 *
 * @author PetSoft
 */
public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String s) {
        super(s);
    }
}
