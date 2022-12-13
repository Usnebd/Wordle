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
    private static String secretWord;                                   //secretWord è una variabile statica che verrà aggiornata periodicamente con la nuova secretWord del server
    public static ArrayList<String> words = new ArrayList<>();   //array in cui verranno salvate tutte le parole presenti nel vocabolario del file words.txt
    public static InetAddress group;                        //indirizzo del gruppo di multicast
    public static int multicastPort;                        //porta a cui il server spedirà il datagramma multicast
    public void run(){
        try {
            ConcurrentHashMap<String, UserData> hashMap = new ConcurrentHashMap<String, UserData>(); //struttura dati concorrente in cui verranno salvate coppie (username,Oggetto Userdata)
            Random random = new Random();                               //oggetto che mi serve per generare numeri random(servirà più in avanti)
            JsonElement fileElement = JsonParser.parseReader(new FileReader("src\\config.json"));          //apro il file config.json
            JsonObject fileObject = fileElement.getAsJsonObject();
            //estraggo il dati di configurazione dal file Json
            int secretWordRate = fileObject.get("secretWordRate").getAsInt();
            int welcomePort = fileObject.get("server_port").getAsInt();
            int timeout = fileObject.get("timeout").getAsInt();
            group = InetAddress.getByName(fileObject.get("multicastAddress").getAsString());
            multicastPort = fileObject.get("multicastPort").getAsInt();
            String datafilepath = fileObject.get("datafile").getAsString();         //nome del file in cui sono presenti i dati degli utenti
            String wordfilepath = fileObject.get("wordfile").getAsString();         //nome del file in cui è presente il vocabolario
            loadData(datafilepath,hashMap);                                         //carico i dati degli utenti(partite vinte, statistiche varie,ecc...) nella hashMap
            int wordsNumber = loadWords(words,wordfilepath);                        //carico le parole nell'array words e restituisco il numero di parole presenti nel file
            ServerSocket serverSocket = new ServerSocket(welcomePort);              //creo un welcome socket sulla porta "welcomePort"
            serverSocket.setSoTimeout(timeout);                                     //imposto un timeout del socket
            ExecutorService service = Executors.newCachedThreadPool();              //creo una pool di thread di tipo elastica
            SecretWordTask secretWordTask = new SecretWordTask(random,wordsNumber,words);  //creo un task che prende come parametro l'oggetto random,il numero di parole del vocabolario e l'array di parole
            ScheduledExecutorService  scheduledSwService = Executors.newSingleThreadScheduledExecutor();    //creo un SingleThreadScheduledExecutor per estrarre la Secret Word casual periodicamente
            scheduledSwService.scheduleAtFixedRate(secretWordTask,0L,secretWordRate,TimeUnit.MINUTES);      //genero una nuova secretWord con delay iniziale di 0 e con frequenza di "secretWordRate" minuti
            while(!Thread.currentThread().isInterrupted()){                                     //verifico che il thread Server non sia stato fermato
                try {
                    //accetto una richiesta di connessione e salvo il socket della connessione nella variabile client
                    service.execute(new ServerTask(serverSocket.accept(),hashMap));
                    //mando in esecuzione un task che si occuperà di fornire un servizio al client
                } catch (IOException ignore) {}
            }                                                       //dopo che è stata rilevato un Thread.interrupt() eseguo delle operazioni di terminazione
            serverSocket.close();                                   //chiudo il serverSocket
            scheduledSwService.shutdownNow();                          //arresto il thread che genera periodicamente la secretWord
            service.shutdownNow();                                     //arresto il threadpool
            saveData(datafilepath,hashMap);                         //faccio "flush" dei dati degli utenti che sono presenti sulla hashMap nel file data.json
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
    }           //restituisce la secretWord
    public static void setSecretWord(String secretWord){                  //imposta la secretWord (metodo usato nel task SecretWordTask)
        WordleServer.secretWord=secretWord;
    }
    private static int loadWords(ArrayList<String> words, String filename){
        try {
            FileReader fileReader = new FileReader(filename);                   //apro il file contenente le parole
            BufferedReader bufferedReader = new BufferedReader(fileReader);     //con un bufferdReader
            String word = null;
            int lines=0;
             do{
                word = bufferedReader.readLine();
                if (word != null) {                                         //leggo le parole, le aggiungo all'array e incremento il conteggio delle parole (variabile lines)
                    words.add(word);
                    lines++;
                }
            }while (word != null);
             bufferedReader.close();
             fileReader.close();                                            //chiudo gli stream e restituisco il numero di parole lette
             return lines;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void loadData(String pathname, ConcurrentHashMap hashMap) {
        try {
            JsonReader reader = new JsonReader(new FileReader(pathname));               //apro il file json contenente i dati degli utenti
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
                reader.nextName();                                                     //salvo i campi nelle rispettive variabili
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
                user.setGamesWon(gamesWon);                                            //creo un oggetto UserData e lo inizializzo con i dati presenti nel json
                user.setLastStreak(lastStreak);
                user.setMaxStreak(maxStreak);
                user.setGuessDistribution(guessDistribution);
                hashMap.putIfAbsent(username,user);                                   //inserisco l'oggetto nella hashMap
            }
            reader.endArray();
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void saveData(String filename, ConcurrentHashMap hashMap){
        JsonWriter writer;
        try {
            writer = new JsonWriter(new FileWriter(filename));          //se il file "filename" non esiste viene creato, altrimenti viene sovrascritto
            Iterator<String> it = hashMap.keySet().iterator();          //creo un iteratore che serve per scorrere gli elemento della hashMap
            writer.beginArray();                                        // [
            while(it.hasNext()){                                        //finché ha elementi
                String key = it.next();
                UserData user= (UserData) hashMap.get(key);
                writer.beginObject();                                   // {
                writer.name("username").value(key);
                writer.name("password").value(user.getPassword());
                writer.name("maxStreak").value(user.getMaxStreak());         //uso i metodi getter per ottenere i dati dell'utente e li vado a scrivere le file json contente di dati
                writer.name("lastStreak").value(user.getLastStreak());
                writer.name("playedMatches").value(user.getPlayedMatches());
                writer.name("gamesWon").value(user.getGamesWon());
                writer.name("guessDistribution").value(user.getGuessDistribution());
                writer.endObject();                                     // }
            }
            writer.endArray();                                          // ]
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}