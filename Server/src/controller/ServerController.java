package controller;


import Database.Database;
import model.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerController implements Runnable {

    public static ArrayList<ServerController> serverControllers = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private String appUsername;
    private static ArrayList<User> allUsers = new ArrayList<>();
    private static HashMap<String, User> loggedInUsers;
    private String userToken;

    public ServerController(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }


    public void getService() {
        try {
            String task = inputStream.readUTF();
            if (task.equals("signUp")) signUp();
            else if (task.equals("login")) login();

        } catch (IOException e) {
            System.out.println("A user disconnected.");
            try {
                inputStream.close();
                outputStream.close();
                this.socket.close();
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    public void signUp() {
        try {
            String[] parts = inputStream.readUTF().split(" ");
            String username = parts[0];
            String pass = parts[1];
            String email = parts[2];
            String phoneNum = null;
            if (parts.length == 4)
                phoneNum = parts[3];
            String token = UUID.randomUUID().toString();
            int avatarSize = inputStream.readInt();
            byte[] avatar = new byte[avatarSize];
            inputStream.readFully(avatar, 0, avatarSize);
            int answer = Database.insertToDB(username, pass, email, phoneNum, token, avatar).getCode();

            outputStream.writeInt(answer);
            outputStream.flush();
            //allUsers.add(new User(parts[0], parts[1], parts[]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        try {
            String[] parts = inputStream.readUTF().split(" ");
            String username = parts[0];
            String pass = parts[1];
            User answer = Database.retrieveFromDB(username, pass);
            if(answer == null){
                outputStream.writeObject(answer);
                return;
            }
            BufferedImage userAvatar = answer.getAvatar();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(userAvatar, "png", baos);
            byte[] byteAvatar = baos.toByteArray();

            outputStream.writeObject(answer);
            outputStream.flush();

            outputStream.writeInt(byteAvatar.length);

            outputStream.flush();
            outputStream.write(byteAvatar, 0, byteAvatar.length);
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
/*
    public static synchronized boolean register(String username, String password, String fullName) {
        for (User user: allUsers) {
            if (user.getUsername().equals(username)) return false;
        }
        allUsers.add(new User(username, password, fullName));
        return true;

    }
*/


    @Override
    public void run() {
        while (!socket.isClosed()) {
            getService();
        }
    }
}

