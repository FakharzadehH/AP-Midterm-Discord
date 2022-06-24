package view;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


/**
 * The type Menu handler.
 */
public class MenuHandler {

    static Scanner sc = new Scanner(System.in);


    public static int returnChoice() {
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
        return choice;
    }

    public static String getUsername() {
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        return username;
    }


    /**
     * Gets password.
     *
     * @return the password
     */
    public static String getPassword() {
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        return password;
    }


    public static String getEmail() {
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        return email;
    }

    /**
     * Gets avatar.
     */
    public static InputStream getAvatar()   {
        System.out.print("Enter image address:");
        String path = sc.nextLine();

        try {
            InputStream img = new FileInputStream(path);
            return img;
        } catch (IOException e) {
            try {
                return new FileInputStream("./src/defaultAvatar.png");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return null;

            }
        }
    }

    public static int showStartMenu() {
        System.out.println("""
                1) Signup
                2) Login
                3) Exit""");
        return returnChoice();
    }

    public static int showLoginMenu() {
        System.out.println("""
                1) Severs
                2) Friends
                3) Settings
                4) Exit""");
        return returnChoice();
    }


    public static int friendMenu() {
        System.out.println("""
                1) Add new friend
                2) Chat with a friend
                3) Exit""");

        return returnChoice();
    }


    public static int serverMenu() {
        System.out.println("""
                1) Add new server
                2) List of all servers
                3) Exit""");

        return returnChoice();
    }

    public static int settingMenu() {
        System.out.println("""
                1) Change password
                2) Change avatar
                3) Exit""");
        return returnChoice();
    }

}
