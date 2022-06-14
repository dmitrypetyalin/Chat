package ru.gb.chat.enums;

import java.util.Objects;

/**
 * 11.06.2022 14:01
 *
 * @author PetSoft
 */
public enum Command {
    BROADCAST_MESSAGE("/broadcast"),
    LIST_USERS("/list"),
    PRIVATE_MESSAGE("/private"),
    AUTH_MESSAGE("/auth"),
    AUTH_OK("/auth-ok"),
    ERROR_MESSAGE("/error");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static Command getByCommand(String command) {
        for (Command value : values()) {
            if (Objects.equals(value.command, command)) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }
}
