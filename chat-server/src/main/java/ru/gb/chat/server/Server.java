package ru.gb.chat.server;

import ru.gb.chat.props.PropertyReader;
import ru.gb.chat.server.service.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static ru.gb.chat.constants.MessageConstants.REGEX;
import static ru.gb.chat.enums.Command.*;

/**
 * 10.06.2022 17:48
 *
 * @author PetSoft
 */
public class Server {
    private final int port;
    private Map<String, ClientHandler> handlers;
    private UserService userService;

    public Server(UserService userService) {
        this.userService = userService;
        this.handlers = new TreeMap<>();
        port = PropertyReader.getInstance().getPort();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server start!");
            userService.start();
            while (true) {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                ClientHandler handler = new ClientHandler(socket, this);
                handler.handle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void broadcast(String from, String message) {
        String msg = BROADCAST_MESSAGE.getCommand() + REGEX + String.format("[%s]: %s", from, message);
        handlers.forEach((k, v) -> v.send(msg));
    }

    public void sendPrivateMessage(String from, String to, String message) {
        String msg = PRIVATE_MESSAGE.getCommand() + REGEX + String.format("[%s][to %s]: %s", from, to, message);
        handlers.get(to).send(msg);
        handlers.get(from).send(msg);
    }

    public UserService getUserService() {
        return userService;
    }

    public synchronized boolean isUserAlreadyOnline(String nick) {
        return handlers.get(nick) != null;

    }

    public synchronized void addHandler(String nick, ClientHandler handler) {
        this.handlers.put(nick, handler);
        sendContacts();
    }

    public synchronized void removeHandler(String user, ClientHandler handler) {
        this.handlers.remove(user);
        sendContacts();
    }

    private void shutdown() {
        userService.stop();
    }

    private void sendContacts() {
        String contacts = String.join(REGEX, handlers.keySet());
        String msg = LIST_USERS.getCommand() + REGEX + contacts;
        handlers.forEach((k, v) -> v.send(msg));
    }

    public synchronized void updateHandlerUserName(String oldNick, String newNick) {
        handlers.put(newNick, handlers.get(oldNick));
        handlers.remove(oldNick);
        sendContacts();
    }
}
