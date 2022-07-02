package controller;

import model.Chat;
import model.User;
import model.guild.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static view.MenuHandler.sc;

/**
 * The type App controller.
 */
public class AppController {


    /**
     * The enum Server error type.
     */
    public enum ServerErrorType {
        /**
         * No error server error type.
         */
        NO_ERROR(1),
        /**
         * User already exists server error type.
         */
        USER_ALREADY_EXISTS(2),
        /**
         * Server connection failed server error type.
         */
        SERVER_CONNECTION_FAILED(3),
        /**
         * Database error server error type.
         */
        DATABASE_ERROR(4),
        /**
         * Duplicate error server error type.
         */
        Duplicate_ERROR(5),
        /**
         * Already friend server error type.
         */
        ALREADY_FRIEND(6),
        /**
         * Unknown error server error type.
         */
        UNKNOWN_ERROR(404);

        private int code;

        ServerErrorType(int code) {
            this.code = code;
        }
    }

    private User currentUser;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Instantiates a new App controller.
     */
    public AppController() {
        setupConnection();
    }


    private void setupConnection() {
        try {
            socket = new Socket("localhost", 7777);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException x) {
            System.out.println("SERVER CONNECTION FAILED.");
            System.exit(0);
        }
    }


    /**
     * Sign up.
     *
     * @param username the username
     * @param password the password
     * @param email    the email
     * @param phoneNum the phone num
     * @param avatar   the avatar
     * @return the string
     */
    public String signUp(String username, String password, String email, String phoneNum, InputStream avatar) {

        try {
            outputStream.writeUTF("#signUp");
            outputStream.flush();
            outputStream.writeUTF(username + " " + password + " " + email + " " + phoneNum);
            outputStream.flush();
            byte[] img = avatar.readAllBytes();
            outputStream.writeInt(img.length);
            outputStream.flush();
            outputStream.write(img, 0, img.length);
            outputStream.reset();
            return parseError(inputStream.readInt());
        } catch (IOException x) {
            x.printStackTrace();
            return "IOException";
        }
    }


    /**
     * Login user.
     *
     * @param username the username
     * @param password the password
     * @return the user
     */
    public User login(String username, String password) {
        try {
            outputStream.writeUTF("#login");
            outputStream.flush();
            outputStream.writeUTF(username + " " + password);
            outputStream.flush();
            User user = (User) inputStream.readObject();
            if (user == null) {
                return user;
            }
            int avatarSize = inputStream.readInt();
            byte[] avatar = new byte[avatarSize];
            inputStream.readFully(avatar, 0, avatarSize);
            user.setAvatar(avatar);
            currentUser = user;
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can not write for server.");
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Can not read from server.");
            return null;
        }
    }

    /**
     * Sets status.
     *
     * @param status   the status
     * @param username the username
     * @return the status
     */
    public String setStatus(String status, String username) {
        try {
            outputStream.writeUTF("#setStatus");
            outputStream.flush();
            outputStream.writeUTF(username);
            outputStream.flush();
            outputStream.writeUTF(status);
            outputStream.flush();
            String respond = inputStream.readUTF();
            return respond;
        } catch (IOException e) {
            e.printStackTrace();
            return "couldn't change status.";
        }
    }

    /**
     * Friend request.
     *
     * @param username   the username
     * @param targetUser the target user
     * @return the string
     */
    public String friendRequest(String username, String targetUser) {
        String answer;
        int answerCode;
        try {
            outputStream.writeUTF("#friendRequest");
            outputStream.flush();

            outputStream.writeUTF(username);
            outputStream.flush();

            outputStream.writeUTF(targetUser);
            outputStream.flush();
            answerCode = inputStream.readInt();
            answer = parseError(answerCode);
        } catch (IOException e) {
            e.printStackTrace();
            answer = "something went wrong with friend Request.";
        }
        return answer;
    }

    /**
     * Friend request list hash set.
     *
     * @param username the username
     * @return the hash set
     */
    public HashSet<String> friendRequestList(String username) {
        try {
            outputStream.writeUTF("#RequestList");
            outputStream.flush();
            outputStream.writeUTF(username);
            outputStream.flush();
            return (HashSet<String>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Revised friend requests.
     *
     * @param username the username
     * @param accepted the accepted
     * @param rejected the rejected
     * @return the string
     */
    public String revisedFriendRequests(String username, HashSet<String> accepted, HashSet<String> rejected) {
        try {
            outputStream.writeUTF("#revisedFriendRequests");
            outputStream.flush();
            outputStream.writeObject(accepted);
            outputStream.flush();
            outputStream.writeObject(rejected);
            outputStream.flush();
            outputStream.writeUTF(username);
            outputStream.flush();
            String response = inputStream.readUTF();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "something went wrong while revising Friend requests.";
        }
    }

    /**
     * Friend list hash set.
     *
     * @param username the username
     * @return the hash set
     */
    public HashSet<String> friendList(String username) {
        try {
            outputStream.writeUTF("#FriendList");
            outputStream.flush();
            outputStream.writeUTF(username);
            outputStream.flush();
            return (HashSet<String>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public User getUser(String username) {
        try {
            outputStream.writeUTF("#getUser");
            outputStream.flush();
            outputStream.writeUTF(username);
            outputStream.flush();
            return (User) inputStream.readUnshared();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Block user.
     *
     * @param username the username
     * @return the string
     */
    public String blockUser(String username) {
        try {
            outputStream.writeUTF("#blockUser");
            outputStream.writeUTF(currentUser.getUsername());
            outputStream.flush();
            outputStream.writeUTF(username);
            outputStream.flush();
            String dbResponse = inputStream.readUTF();
            return dbResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return "something went wrong while blocking user.";
        }
    }

    /**
     * Unblock user.
     *
     * @param unblockTarget the unblock target
     * @return the string
     */
    public String unblockUser(String unblockTarget) {
        try {
            outputStream.writeUTF("#unblockUser");
            outputStream.flush();
            outputStream.writeUTF(currentUser.getUsername());
            outputStream.flush();
            outputStream.writeUTF(unblockTarget);
            outputStream.flush();
            String respone = inputStream.readUTF();
            return respone;
        } catch (IOException e) {
            e.printStackTrace();
            return "something went wrong while unblocking user.";
        }
    }


    /**
     * Blocked list hash set.
     *
     * @return the hash set
     */
    public HashSet<String> blockedList() {
        try {
            outputStream.writeUTF("#blockList");
            outputStream.flush();
            outputStream.writeUTF(currentUser.getUsername());
            outputStream.flush();
            HashSet<String> blockedList = (HashSet<String>) inputStream.readObject();
            return blockedList;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Request for direct chat.
     *
     * @param friend the friend
     */
    public void requestForDirectChat(User friend) {
        try {
            outputStream.writeUTF("#requestForDirectChat");
            outputStream.flush();
            outputStream.writeObject(friend);
            outputStream.flush();
            outputStream.writeObject(currentUser);
            outputStream.flush();
            String answer = inputStream.readUTF();
            if (answer.equals("Success")) {
                Chat directChat = (Chat) inputStream.readObject();
                directChat.setCurrUser(currentUser);
                directChat.setOutputStream(outputStream);
                directChat.setInputStream(inputStream);
                Thread chatThread = new Thread(directChat);
                chatThread.start();
                chatThread.join();
//                new Thread(directChat).start();// "You are in private chat with " + friend.getUsername();
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
            return;//"Could not open chat with " + friend.getUsername();
        }
    }

    /**
     * Remove from direct chat.
     *
     * @param user   the user
     * @param friend the friend
     */
    public void removeFromDirectChat(User user, User friend) {
        try {
            outputStream.writeUTF("#removeFromChat");
            outputStream.flush();
            outputStream.writeUTF(user.getUsername());
            outputStream.flush();
            outputStream.writeUTF(friend.getUsername());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add server.
     *
     * @param guild the guild
     * @return the string
     */
    public String addServer(Guild guild) {
        try {
            outputStream.writeUTF("#addGuild");
            outputStream.flush();
            outputStream.writeObject(guild);
            outputStream.flush();
            return inputStream.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
            return "Fail to add server.";
        }
    }

    /**
     * List of joined servers array list.
     *
     * @return the array list
     */
    public ArrayList<Guild> listOfJoinedServers() {
        try {
            outputStream.writeUTF("#serverList");
            outputStream.flush();
            outputStream.writeUTF(currentUser.getUsername());
            outputStream.flush();
            ArrayList<Guild> guilds = (ArrayList<Guild>) inputStream.readUnshared();
            return guilds;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets guild.
     *
     * @param owner     the owner
     * @param guildName the guild name
     * @return the guild
     */
    public Guild getGuild(String owner, String guildName) {
        try {
            outputStream.writeUTF("#getGuild");
            outputStream.flush();
//            outputStream.writeUTF(owner);
//            outputStream.flush();
            outputStream.writeUTF(guildName);
            outputStream.flush();
            Guild guild = (Guild) inputStream.readUnshared();
            return guild;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add member to server .
     *
     * @param name  the name
     * @param guild the guild
     * @return the string
     */
    public String addMemberToServer(String name, Guild guild) {
        try {
            User user = getUser(name);
            GuildUser member = new GuildUser(user, new Role("member"));
            outputStream.writeUTF("#addMember");
            outputStream.flush();
            outputStream.writeObject(member);
            outputStream.flush();
            outputStream.writeUTF(guild.getOwnerName());
            outputStream.flush();
            outputStream.writeUTF(guild.getName());
            outputStream.flush();
            String respond = inputStream.readUTF();
            return respond;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "something went wrong while adding member to server.";
    }

    /**
     * Add new text channel .
     *
     * @param guild the guild
     * @return the string
     */
    public String addNewTextChannel(Guild guild) {
        String response = null;
        try {
            System.out.print("Enter text channel name: ");
            String name = sc.nextLine();
            outputStream.writeUTF("#addTextChannel");
            outputStream.flush();
            outputStream.writeUTF(name);
            outputStream.flush();
            outputStream.writeUTF(guild.getOwnerName());
            outputStream.flush();
            outputStream.writeUTF(guild.getName());
            outputStream.flush();
            response = inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            response = "something went wrong while adding text channel";
        }
        return response;
    }

    /**
     * Request for group chat.
     *
     * @param guild       the guild
     * @param textChannel the text channel
     */
    public void requestForGroupChat(Guild guild, TextChannel textChannel) {
        try {
            outputStream.writeUTF("#getTextChannel");
            outputStream.flush();
            outputStream.writeUTF(guild.getOwnerName());
            outputStream.flush();
            outputStream.writeUTF(guild.getName());
            outputStream.flush();
            outputStream.writeUTF(textChannel.getName());
            outputStream.flush();
            String answer = inputStream.readUTF();
            if (answer.equals("success.")) {

                Chat groupChat = new Chat();
                groupChat.setOutputStream(outputStream);
                groupChat.setInputStream(inputStream);
                groupChat.setCurrUser(currentUser);
                Thread groupChatThread = new Thread(groupChat);
                groupChatThread.start();
                groupChatThread.join();
            } else {
                System.out.println("something went wrong while requesting for group chat.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete member from server.
     *
     * @param name  the name
     * @param guild the guild
     * @return the string
     */
    public String deleteMemberFromServer(String name, Guild guild) {
        try {
            User user = getUser(name);
            GuildUser member = new GuildUser(user, new Role("member"));
            outputStream.writeUTF("#removeMember");
            outputStream.flush();
            outputStream.writeObject(member);
            outputStream.flush();
            outputStream.writeUTF(guild.getOwnerName());
            outputStream.flush();
            outputStream.writeUTF(guild.getName());
            outputStream.flush();
            String respond = inputStream.readUTF();
            return respond;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "something went wrong while removing member from server.";
    }

    /**
     * Update user .
     *
     * @param user the user
     * @return the boolean
     */
    public boolean updateUser(User user) {
        try {
            outputStream.writeUTF("#updateUser");
            outputStream.flush();
            outputStream.writeObject(user);
            outputStream.flush();
            String respone = inputStream.readUTF();
            if (respone.equals("success.")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Change guild name.
     *
     * @param guild   the guild
     * @param newName the new name
     * @return the string
     */
    public String changeGuildName(Guild guild, String newName) {
        try {
            outputStream.writeUTF("#changeGuildName");
            outputStream.flush();
            outputStream.writeUTF(guild.getOwnerName());
            outputStream.flush();
            outputStream.writeUTF(guild.getName());
            outputStream.flush();
            outputStream.writeUTF(newName);
            outputStream.flush();
            return inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "something went wrong while renaming server.";
    }

    /**
     * Remove text channel.
     *
     * @param guild       the guild
     * @param textChannel the text channel
     * @return the string
     */
    public String removeTextChannel(Guild guild, TextChannel textChannel) {
        String response = null;
        try {
            outputStream.writeUTF("#deleteTextChannel");
            outputStream.flush();
            outputStream.writeUTF(guild.getOwnerName());
            outputStream.flush();
            outputStream.writeUTF(guild.getName());
            outputStream.flush();
            outputStream.writeObject(textChannel);
            outputStream.flush();
            response = inputStream.readUTF();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "couldn't delete text channel.";
        }
    }

    /**
     * Delete guild.
     *
     * @param guild  the guild
     * @param gOwner the g owner
     * @return the string
     */
    public String deleteGuild(Guild guild, String gOwner) {
        try {
            outputStream.writeUTF("#deleteGuild");
            outputStream.flush();
            outputStream.writeUTF(gOwner);
            outputStream.flush();
            outputStream.writeObject(guild);
            outputStream.flush();
            return inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return "Oops, Something went wong we can not delete server, please try again.";
        }
    }

    /**
     * Parse error.
     *
     * @param errorCode the error code
     * @return the string
     */
    public String parseError(int errorCode) {
        String error;
        switch (errorCode) {
            case 1:
                error = "Success";
                break;
            case 2:
                error = "This user already exists.";
                break;
            case 3:
                error = "Connection with server failed.";
                break;
            case 4:
                error = "There was a problem with database.";
                break;
            case 5:
                error = "you already have a friend request with this user.";
                break;
            case 6:
                error = "you are already friend of this user.";
                break;
            default:
                error = "UNKNOWN ERROR.";
                break;

        }
        return error;
    }

}