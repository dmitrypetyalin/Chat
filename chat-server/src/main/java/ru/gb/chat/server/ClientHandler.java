package ru.gb.chat.server;

import ru.gb.chat.enums.Command;
import ru.gb.chat.server.error.NickAlreadyBusyException;
import ru.gb.chat.server.error.WrongCredentialsException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

import static ru.gb.chat.constants.MessageConstants.REGEX;
import static ru.gb.chat.enums.Command.*;

/**
 * 10.06.2022 21:45
 *
 * @author PetSoft
 */
public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread handlerThread;
    private Server server;
    private String user;

    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Connection problems with user: " + user);
        }
    }

    public void handle() {
        handlerThread = new Thread(() -> {
            try {
                Executors.newSingleThreadExecutor().submit(this::authorize).get(120, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
//            authorize();
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    String message = in.readUTF();
                    parseMessage(message);
                } catch (IOException e) {
                    System.out.println("Connection broken with client: " + user);
//                    if (user != null) {
                        server.removeHandler(user, this);
//                    }
                }
            }
        });
        handlerThread.start();
    }


    private void parseMessage(String message) {
        String[] split = message.split(REGEX);
        Command command = Command.getByCommand(split[0]);
        switch (command) {
            case BROADCAST_MESSAGE -> server.broadcast(user, split[1]);
            case PRIVATE_MESSAGE -> server.sendPrivateMessage(user, split[1], split[2]);
            case CHANGE_NICK -> changeNick(split[1]);
            default -> System.out.println("Unknown message" + message);
        }
    }

    private void changeNick(String newNick) {
        try {
            server.getUserService().changeNick(user, newNick);
            server.updateHandlerUserName(user, newNick);
            user = newNick;
            send(CHANGE_NICK_OK.getCommand() + REGEX + newNick);
        } catch (NickAlreadyBusyException e) {
            send(ERROR_MESSAGE.getCommand() + REGEX + "This nickname already in use");
        }
    }

    private void authorize() {
        System.out.println("Authorizing");
        try {
            while (!socket.isClosed()) {
                String msg = in.readUTF();
                if (msg.startsWith(AUTH_MESSAGE.getCommand())) {
                    String[] parsed = msg.split(REGEX);
                    String response = "";
                    String nickname = null;
                    try {
                        nickname = server.getUserService().authenticate(parsed[1], parsed[2]);
                        if (server.isUserAlreadyOnline(nickname)) {
                            response = ERROR_MESSAGE.getCommand() + REGEX + "This client already connected";
                            System.out.println("Already connected");
                        }
                    } catch (WrongCredentialsException e) {
                        response = ERROR_MESSAGE.getCommand() + REGEX + e.getMessage();
                        System.out.println("Wrong credentials: " + parsed[1]);
                    }
                    if (!response.equals("")) {
                        send(response);
                    } else {
                        this.user = nickname;
                        send(AUTH_OK.getCommand() + REGEX + nickname);
                        server.addHandler(nickname, this);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        return user;
    }
}
