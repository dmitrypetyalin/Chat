package ru.gb.chat.client.net;

import ru.gb.chat.props.PropertyReader;

import java.io.*;
import java.net.Socket;

/**
 * 11.06.2022 18:25
 *
 * @author PetSoft
 */
public class NetworkService {
    private final String host;
    private final int port;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private Thread clientThread;
    private final MessageProcessor messageProcessor;

    public NetworkService(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
        PropertyReader prop = PropertyReader.getInstance();
        host = prop.getHost();
        port = prop.getPort();
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        readMessages();
    }

    private void readMessages() {
        clientThread = new Thread(() -> {
            try {
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    String income = in.readUTF();
                    messageProcessor.processMessage(income);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        clientThread.start();
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && socket.isClosed();
    }

    public void shutdown() throws IOException {
        if (clientThread != null && clientThread.isAlive()) {
            clientThread.interrupt();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Client stopped");
    }
}
