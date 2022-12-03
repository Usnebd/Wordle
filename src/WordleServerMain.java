import com.google.gson.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordleServerMain {
    public static void main(String[] args){
        ConcurrentHashMap<String, UserData> hashMap = new ConcurrentHashMap<String, UserData>();
        //Apro il file config.json
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader("src\\config.json"));
            JsonObject fileObject = fileElement.getAsJsonObject();
            //extracting basic fields
            int welcomePort = fileObject.get("server_port").getAsInt();
            InetAddress ia = InetAddress.getByName(fileObject.get("server_hostname").getAsString());
            int timeout = fileObject.get("timeout").getAsInt();
            //creo un welcome socket sulla porta "welcomePort"
            ServerSocket serverSocket = new ServerSocket(welcomePort);
            //creo un ThreadPool per gestire gli utenti
            ExecutorService service = Executors.newCachedThreadPool();
            while(true){
                //accetto ogni richiesta di connessione e passo la task al threadpool
                service.execute(new Task(hashMap,serverSocket.accept()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
