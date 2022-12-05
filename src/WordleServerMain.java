import com.google.gson.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.*;

public class WordleServerMain {
    public static void main(String[] args){
        ConcurrentHashMap<String, UserData> hashMap = new ConcurrentHashMap<String, UserData>();
        InetAddress group = null;
        int multicastPort;
        //Apro il file config.json
        try {
            String[] secretWord = new String[1];
            FileReader fileReader = new FileReader("src\\words.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //conta il numero di righe possedute dal file words.txt
            int lines=0;
            do {
                lines++;
            }while(bufferedReader.readLine()!=null);
            //Seleziona la parola nella riga k-esima, con k numero casuale
            Random random = new Random();
            JsonElement fileElement = JsonParser.parseReader(new FileReader("src\\config.json"));
            JsonObject fileObject = fileElement.getAsJsonObject();
            //extracting basic fields
            int secretWordRate = fileObject.get("secretWordRate").getAsInt();
            int welcomePort = fileObject.get("server_port").getAsInt();
            int timeout = fileObject.get("timeout").getAsInt();
            group = InetAddress.getByName(fileObject.get("multicastAddress").getAsString());
            multicastPort = fileObject.get("multicastPort").getAsInt();
            ServerTask.multicastPort = multicastPort;
            ServerTask.group = group;
            //creo un welcome socket sulla porta "welcomePort"
            ServerSocket serverSocket = new ServerSocket(welcomePort);
            serverSocket.setSoTimeout(timeout);
            //creo un ThreadPool per gestire gli utenti e uno per estrarre la Secret Word casual periodicamente
            ExecutorService service = Executors.newCachedThreadPool();
            SecretWordTask secretWordTask = new SecretWordTask(random,bufferedReader,lines,secretWord);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(secretWordTask,0, secretWordRate, TimeUnit.MINUTES);
            while(true){
                //accetto ogni richiesta di connessione e passo la task al threadpool
                service.execute(new ServerTask(hashMap,serverSocket.accept(),secretWord));
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