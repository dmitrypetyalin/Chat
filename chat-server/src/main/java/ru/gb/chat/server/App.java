package ru.gb.chat.server;

import ru.gb.chat.server.service.Impl.InMemoryUserServiceImpl;

/**
 * 10.06.2022 22:23
 *
 * @author PetSoft
 */
public class App {
    public static void main(String[] args) {
        new Server(new InMemoryUserServiceImpl()).start();
    }
}
