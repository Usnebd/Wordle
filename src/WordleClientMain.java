import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class WordleClientMain {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            File file = new File("config.json");
            JsonElement fileElement = JsonParser.parseReader(new FileReader(file));
            JsonObject fileObject = fileElement.getAsJsonObject();
            //extracting basic fields
            int welcomePort = fileObject.get("welcomePort").getAsInt();
            InetAddress ia = InetAddress.getByName(fileObject.getAsString());
            int timeout = fileObject.getAsInt();
            //apro il file config.txt
            Socket socket = new Socket(ia, welcomePort);
            Scanner scanner = new Scanner(System.in);
            //inserire un comando 1...8
            String menu = "Insert Command\n"+"1) register\n"+"2) login\n"+"3) logout\n"+"4) playWORDLE\n"+"5) sendWord\n"+"6) sendMeStatistics\n"+"7) share\n"+"8) showMeSharing\n";
            System.out.println(menu);
            int command = scanner.nextInt();
            switch(command){
                case 1:
                    System.out.println("Username: \n");
                    String username=scanner.nextLine();
                    System.out.println("Password: \n");
                    String password=scanner.nextLine();
                    register(username,password);
                    break;
                case 2:
                    System.out.println("Username: \n");
                    username = scanner.nextLine();
                    System.out.println("Password: \n");
                    password=scanner.nextLine();
                    login(username,password);
                    break;
                case 3:
                    
                default:
                    break;
            }
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(String username, String password) {
    }

    public static void login(String username, String password) {
    }

    public static void logout(String username) {
    }

    public static void playWORDLE() {
    }

    public static void sendWord() {
    }

    public static void sendMeStatistics() {
    }

    public static void share() {
    }

    public static void showMeSharing() {
    }
}