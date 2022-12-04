import com.google.gson.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordleServerMain {
    public static void main(String[] args){
        ConcurrentHashMap<String, UserData> hashMap = new ConcurrentHashMap<String, UserData>();
        InetAddress group = null;
        int multicastPort;
        //Apro il file config.json
        try {
            FileReader fileReader = new FileReader("src\\words.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //conta il numero di righe possedute dal file words.txt
            int lines=0;
            do {
                lines++;
            }while(bufferedReader.readLine()!=null);
            //Seleziona la parola nella riga k-esima, con k numero casuale
            Random random = new Random();
            String currentSecretWord = findRandomWord(random,bufferedReader,lines);
            JsonElement fileElement = JsonParser.parseReader(new FileReader("src\\config.json"));
            JsonObject fileObject = fileElement.getAsJsonObject();
            //extracting basic fields
            int secretWordTimeValidity = fileObject.get("secretWordTimeValidity").getAsInt();
            int welcomePort = fileObject.get("server_port").getAsInt();
            InetAddress ia = InetAddress.getByName(fileObject.get("server_hostname").getAsString());
            int timeout = fileObject.get("timeout").getAsInt();
            group = InetAddress.getByName(fileObject.get("multicastAddress").getAsString());
            multicastPort = fileObject.get("multicastPort").getAsInt();
            ServerTask.multicastPort = multicastPort;
            ServerTask.group = group;
            //creo un welcome socket sulla porta "welcomePort"
            ServerSocket serverSocket = new ServerSocket(welcomePort);
            //creo un ThreadPool per gestire gli utenti
            ExecutorService service = Executors.newCachedThreadPool();
            service.execute();
            while(true){
                //accetto ogni richiesta di connessione e passo la task al threadpool
                service.execute(new ServerTask(hashMap,serverSocket.accept()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String findRandomWord(Random random, BufferedReader bufferedReader, int lines){
        int wordLine = random.nextInt(lines);
        int i=0;
        String secretWord;
        try {
            do{
                secretWord=bufferedReader.readLine();
                i++;
            }while(i!=wordLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return secretWord;
    }
}