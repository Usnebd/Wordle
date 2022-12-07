import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class WordleServer implements Runnable{
    private static String secretWord;
    private static ArrayList<String> words = new ArrayList<>();
    public void run(){
        ConcurrentHashMap<String, UserData> hashMap = new ConcurrentHashMap<String, UserData>();
        InetAddress group = null;
        int multicastPort;
        //Apro il file config.json
        try {
            ArrayList<Socket> connections = new ArrayList<>();
            int wordsNumber = loadWords(words,"src\\words.txt");
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
            SecretWordTask secretWordTask = new SecretWordTask(random,wordsNumber,secretWordRate,words);
            ScheduledExecutorService  scheduledSwService = Executors.newSingleThreadScheduledExecutor();
            scheduledSwService.scheduleAtFixedRate(secretWordTask,0L,secretWordRate,TimeUnit.MINUTES);
            while(!Thread.currentThread().isInterrupted()){
                //accetto ogni richiesta di connessione e passo la task al threadpool
                try {
                    Socket client = serverSocket.accept();
                    connections.add(client);
                    service.execute(new ServerTask(hashMap,client,words));
                } catch (IOException ignore) {}
            }
            serverSocket.close();
            scheduledSwService.shutdown();
            service.shutdown();
            for(Socket client:connections){
                client.close();
            }
            if(!service.awaitTermination(2000,TimeUnit.MILLISECONDS)&&!scheduledSwService.awaitTermination(2000,TimeUnit.MILLISECONDS)){
                service.shutdownNow();
                scheduledSwService.shutdownNow();
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getSecretWord(){
        return secretWord;
    }
    public static void setSecretWord(String secretWord){
        WordleServer.secretWord=secretWord;
    }
    private static int loadWords(ArrayList<String> words, String filename){
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String word = null;
            int lines=0;
             do{
                word = bufferedReader.readLine();
                if (word != null) {
                    words.add(word);
                    lines++;
                }
            }while (word != null);
             bufferedReader.close();
             fileReader.close();
             return lines;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}