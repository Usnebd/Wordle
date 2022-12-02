import com.google.gson.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordleServerMain {
    public static void main(String[] args){
        //Apro il file config.json
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File("config.json");
            JsonElement fileElement = JsonParser.parseReader(new FileReader(file));
            JsonObject fileObject = fileElement.getAsJsonObject();
            //extracting basic fields
            int welcomePort = fileObject.get("welcomePort").getAsInt();
            InetAddress ia = InetAddress.getByName(fileObject.getAsString());
            int timeout = fileObject.getAsInt();
            //creo un welcome socket sulla porta "welcomePort"
            ServerSocket serverSocket = new ServerSocket(welcomePort);
            //creo un ThreadPool per gestire gli utenti
            ExecutorService service = Executors.newCachedThreadPool();
            while(true){
                //accetto ogni richiesta di connessione e passo la task al threadpool
                service.execute(new Task(serverSocket.accept()));
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
