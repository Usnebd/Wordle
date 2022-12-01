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
            Scanner server = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            while(!socket.isClosed()){
                System.out.println(server.next());
                out.write(scanner.next());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}