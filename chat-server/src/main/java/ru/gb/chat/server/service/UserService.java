package ru.gb.chat.server.service;

import ru.gb.chat.server.model.User;

/**
 * 11.06.2022 0:06
 *
 * @author PetSoft
 */
public interface UserService {
    void start();
    void stop();
    String authenticate(String login, String password);
    String changeNick(String login, String newNick);
    User createUser(String login, String password, String nick);
    void deleteUser(String login, String password);
    void changePassword(String login, String oldPassword, String newPassword);

}
