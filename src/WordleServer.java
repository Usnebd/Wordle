import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.*;

public class WordleServer implements Runnable{
    private static String secretWord;
    private static ArrayList<String> words = new ArrayList<>();
    public void run(){
        ConcurrentHashMap<String, UserData> hashMap = new ConcurrentHashMap<String, UserData>();
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
            InetAddress group = InetAddress.getByName(fileObject.get("multicastAddress").getAsString());
            int multicastPort = fileObject.get("multicastPort").getAsInt();
            String datafilepath = fileObject.get("datafile").getAsString();
            loadData(datafilepath,hashMap);
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
            saveData(datafilepath,hashMap);
            serverSocket.close();
            scheduledSwService.shutdown();
            service.shutdown();
            for(Socket client:connections){
                client.close();
            }
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
    private static void loadData(String pathname, ConcurrentHashMap hashMap) {
        try {
            JsonReader reader = new JsonReader(new FileReader(pathname));
            reader.beginArray();
            while(reader.hasNext()){
                reader.beginObject();
                UserData user;
                reader.nextName();
                String username = reader.nextString();
                reader.nextName();
                String password = reader.nextString();
                reader.nextName();
                int maxStreak = reader.nextInt();
                reader.nextName();
                int lastStreak = reader.nextInt();
                reader.nextName();
                int playedMatches = reader.nextInt();
                reader.nextName();
                int gamesWon = reader.nextInt();
                reader.nextName();
                int guessDistribution = reader.nextInt();
                reader.endObject();
                user = new UserData(password);
                user.setPlayedMatches(playedMatches);
                user.setGamesWon(gamesWon);
                user.setLastStreak(lastStreak);
                user.setMaxStreak(maxStreak);
                user.setGuessDistribution(guessDistribution);
                hashMap.putIfAbsent(username,user);
            }
            reader.endArray();
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void saveData(String pathname, ConcurrentHashMap hashMap){
        JsonWriter writer;
        try {
            writer = new JsonWriter(new FileWriter(pathname));
            Iterator<String> it = hashMap.keySet().iterator();
            writer.beginArray();
            while(it.hasNext()){
                String key = it.next();
                UserData user= (UserData) hashMap.get(key);
                writer.beginObject();
                writer.name("username").value(key);
                writer.name("password").value(user.getPassword());
                writer.name("maxStreak").value(user.getMaxStreak());
                writer.name("lastStreak").value(user.getLastStreak());
                writer.name("playedMatches").value(user.getPlayedMatches());
                writer.name("gamesWon").value(user.getGamesWon());
                writer.name("guessDistribution").value(user.getGuessDistribution());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}