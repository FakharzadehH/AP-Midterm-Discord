package controller;

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Connection {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;
    private String username;

    public Connection(Socket socket,ObjectOutputStream outputStream,ObjectInputStream inputStream, String username) throws IOException {
        this.socket = socket;
        this.username = username;
        this.outputStream = outputStream; // new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = inputStream; //new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(Message message, int index) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Message receiveMessage() {
        try {
            return (Message) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendAllMessages(ArrayList<Message> messages) {
        try {
            outputStream.writeObject(messages);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Message> receiveAllMessages() {
        try {
            return (ArrayList<Message>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection)) return false;
        Connection that = (Connection) o;
        return Objects.equals(outputStream, that.outputStream) && Objects.equals(inputStream, that.inputStream) && Objects.equals(socket, that.socket) && Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputStream, inputStream, socket, getUsername());
    }
}
