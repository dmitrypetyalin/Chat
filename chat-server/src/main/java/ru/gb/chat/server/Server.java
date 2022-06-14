package ru.gb.chat.server;

import ru.gb.chat.server.service.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.gb.chat.constants.MessageConstants.REGEX;
import static ru.gb.chat.enums.Command.*;

/**
 * 10.06.2022 17:48
 *
 * @author PetSoft
 */
public class Server {
    private static final int PORT = 8189;
    private List<ClientHandler> handlers;
    private UserService userService;

    public Server(UserService userService) {
        this.userService = userService;
        this.handlers = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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
        System.out.println("Message from Server, broadcastMessage()");
        for (ClientHandler handler : handlers) {
            handler.send(msg);
        }
    }

    public void sendPrivateMessage(String from, String to, String message) {
        String msg = PRIVATE_MESSAGE.getCommand() + REGEX + String.format("[%s]: %s", from, message);
        for (ClientHandler handler : handlers) {
            if(handler.getUser().equals(to) || handler.getUser().equals(from)) {
                handler.send(msg);
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public synchronized boolean isUserAlreadyOnline(String nick) {
        for (ClientHandler handler : handlers) {
            if (handler.getUser().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addHandler(ClientHandler handler) {
        this.handlers.add(handler);
        sendContacts();
    }

    public synchronized void removeHandler(ClientHandler handler) {
        this.handlers.remove(handler);
        sendContacts();
    }

    private void shutdown() {
        userService.stop();
    }

    private void sendContacts() {
        String contacts = handlers.stream().
                map(ClientHandler::getUser).
                collect(Collectors.joining(REGEX));
        String msg = LIST_USERS.getCommand() + REGEX + contacts;
        for (ClientHandler handler : handlers) {
            handler.send(msg);
        }
    }
}
