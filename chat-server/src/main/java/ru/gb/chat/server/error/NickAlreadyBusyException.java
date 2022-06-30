package ru.gb.chat.server.error;

/**
 * 20.06.2022 11:12
 *
 * @author PetSoft
 */
public class NickAlreadyBusyException extends IllegalArgumentException {
    public NickAlreadyBusyException() {
    }

    public NickAlreadyBusyException(String s) {
        super(s);
    }
}
