import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordleServerMain {
    public static void main(String[] args){
        ConcurrentHashMap<String, Integer> hashmap = new ConcurrentHashMap<String, Integer>();
        //Apro il file config.json
        try {
            File datafile = new File("data.json");
            if(!datafile.exists()){
                datafile.createNewFile();
                JsonWriter writer = new JsonWriter(new FileWriter(datafile));
                writer.beginObject();
                writer.beginArray();
                writer.endArray();
                writer.endObject();
                writer.close();
            }
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
