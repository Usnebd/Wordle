import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class WordleClientMain {
    public static void main(String[] args) {
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader("src\\config.json"));
            JsonObject fileObject = fileElement.getAsJsonObject();
            //extracting basic fields
            int welcomePort = fileObject.get("server_port").getAsInt();
            InetAddress ia = InetAddress.getByName(fileObject.get("server_hostname").getAsString());
            int timeout = fileObject.get("timeout").getAsInt();
            InetAddress group = InetAddress.getByName(fileObject.get("multicastAddress").getAsString());
            int multicastPort = fileObject.get("multicastPort").getAsInt();
            //apro il file config.txt
            Socket socket = new Socket(ia, welcomePort);
            socket.setSoTimeout(timeout);
            Scanner scanner = new Scanner(System.in);
            //inserire un comando 1...8
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            String received="null";
            NotificationTask notificationTask = new NotificationTask(group, multicastPort, notifications);
            Thread thread = new Thread (notificationTask);
            do{
                received="null";
                while(!received.equals("eof") && !received.equals("Logout done!")){
                    received=in.nextLine();
                    if(!received.equals("eof")){
                        System.out.println(received);
                    }
                }
                if(!received.equals("Logout done!")){
                    thread.start();
                    out.println(scanner.nextLine());
                    notificationTask.closeNotification();
                }
            }while(!socket.isClosed() && !received.equals("Logout done!"));
            scanner.close();
            out.close();
            in.close();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


