import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class WordleClientMain {
    public static void main(String[] args) {
        try {
            ArrayList notifications = new ArrayList<>();
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
            NotificationTask notificationTask = new NotificationTask(group, multicastPort, notifications, timeout);
            Thread thread = new Thread (notificationTask);
            thread.start();
            do{
                received="null";
                while(!received.equals("end") && !received.equals("Logout done!")){
                    if(!socket.isClosed()){
                        try {
                            received=in.nextLine();
                        } catch (NoSuchElementException e) {
                            System.out.println("Error, socket may be closed\n");
                        }
                        if(!received.equals("end")){
                            System.out.println(received);
                        }
                    }else{
                        received="end";
                    }
                }
                if(!received.equals("Logout done!")){
                    out.println(scanner.nextLine());
                }
            }while(!socket.isClosed() && !received.equals("Logout done!"));
            thread.interrupt();
            System.out.println("Notification thread is shutting down....");
            scanner.close();
            out.close();
            in.close();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


