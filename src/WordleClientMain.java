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
            ArrayList<String> notifications = new ArrayList<>();        //array che raccoglie le notifiche ricevute dal gruppo multicast
            JsonElement fileElement = JsonParser.parseReader(new FileReader("src\\config.json"));
            JsonObject fileObject = fileElement.getAsJsonObject();
                                                                        //estraggo il dati di configurazione dal file Json
            int serverPort = fileObject.get("server_port").getAsInt();
            InetAddress ia = InetAddress.getByName(fileObject.get("server_hostname").getAsString());
            int timeout = fileObject.get("timeout").getAsInt();
            InetAddress group = InetAddress.getByName(fileObject.get("multicastAddress").getAsString());
            int multicastPort = fileObject.get("multicastPort").getAsInt();
            Socket socket = new Socket(ia, serverPort);          //mi connetto al server alla porta "serverPort"
            socket.setSoTimeout(timeout);
            Scanner scanner = new Scanner(System.in);           //creo uno scanner per leggere l'input da tastiera
            //inserire un comando 1...8
            Scanner in = new Scanner(socket.getInputStream());  //creo uno scanner per leggere nello stream di input
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true); //creo un PrintWriter per scrivere nello stream di output
            String received="null";
            NotificationTask notificationTask = new NotificationTask(group, multicastPort, notifications, timeout);
            //creo i task per la gestione del socket e delle notifiche e poi li passo come paramentri nei relativi thread
            Thread notificationThread = new Thread (notificationTask);
            notificationThread.start();
            do{
                received="null";
                while(!received.equals("end") && !received.equals("Logout done!")){
                    try {
                        received=in.nextLine();
                    } catch (NoSuchElementException ignore) {
                        received = "Logout done!";
                    }
                    if(received.equals("showMeSharing()")){       //quando received è uguale a showMeSharing()
                        for(String s:notifications){              //stampo tutte le notifiche ricevute
                            System.out.println(s+"\n");
                        }
                    }
                    else if(!received.equals("end")){             //se non ho letto la stringa di fine messaggio allora la stampo
                        System.out.println(received);
                    }
                }
                if(!received.equals("Logout done!")){            //se il server restituisce Logout done! non leggo da input
                    String str = scanner.nextLine();
                    out.println(str);
                }
            }while(!received.equals("Logout done!"));
            out.println("CODE_SHUTDOWN_0");
            System.out.println("Client is shutting down....");
            socket.close();
            notificationThread.interrupt();     //arresto i thread e chiudo i vari stream aperti
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


