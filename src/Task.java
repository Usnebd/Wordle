import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Task implements Runnable{
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private File file;
    private JsonElement fileElement;
    private JsonObject fileObject;

    public Task(Socket socket){
        try {
            this.socket=socket;
            this.file = new File("data.json");
            this.fileElement = JsonParser.parseReader(new FileReader(file));
            this.fileObject = fileElement.getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            String menu = "Insert Command\n"+"1) register\n"+"2) login\n"+"3) logout\n"+"4) playWORDLE\n"+"5) sendWord\n"+"6) sendMeStatistics\n"+"7) share\n"+"8) showMeSharing\n";
            int command;
            String username;
            String password;
            do{
                out.write(menu);
                command = in.nextInt();
                switch(command){
                    case 1:
                        out.write("Insert Username");
                        username = in.nextLine();
                        out.write("Insert Password");
                        password = in.nextLine();
                        register(username,password);
                        break;
                    case 2:
                        out.write("Insert Username");
                        username = in.nextLine();
                        out.write("Insert Password");
                        password = in.nextLine();
                        login(username,password);
                        break;
                    case 3:
                        out.write("Insert Username");
                        username = in.nextLine();
                        logout(username);
                        break;
                    case 4:
                        playWORDLE();
                        break;
                    case 5:
                        sendWord();
                        break;
                    case 6:
                        sendMeStatistics();
                        break;
                    case 7:
                        share();
                        break;
                    case 8:
                        showMeSharing();
                        break;
                }
            }while(command != 3);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void register(String username, String password) {
        while(){

        }
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
