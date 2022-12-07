import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerTask implements Runnable{
    public static InetAddress group;
    public static int multicastPort;
    public static ArrayList<String> notificationsSent = new ArrayList<>();
    private UserData user;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private ConcurrentHashMap<String, UserData> hashMap;
    private ArrayList<String> words;
    private int round=-1;
    private String lastSWplayed="null";
    private String secretWord=WordleServerMain.getSecretWord();
    private ArrayList<String> guessedWords = new ArrayList<String>(12);
    private ArrayList<String> hints = new ArrayList<String>(12);
    public ServerTask(ConcurrentHashMap<String, UserData> hashMap, Socket socket, ArrayList<String> words){
        this.socket=socket;
        this.hashMap=hashMap;
        this.words=words;
    }

    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true);
            String menu = "Insert Command\n"+"1) logout\n"+"2) playWORDLE\n"+"3) sendWord\n"+"4) sendMeStatistics\n"+"5) share\n"+"6) showMeSharing";
            String startMenu="Insert Command\n"+"1) register\n"+"2) login\n";
            Boolean logged=false;
            Boolean registered=false;
            Boolean logout=false;
            String username = null;
            String password;
            String command;
            do{
                if(logged==false){
                    out.println(startMenu);
                    out.println("end");
                    command=in.nextLine();
                    switch(command){
                        case "1":
                            if(registered==true){
                                out.println("Error, user is already registered");
                            }else{
                                out.println("Insert Username");
                                out.println("end");
                                username = in.nextLine();
                                out.println("Insert Password");
                                out.println("end");
                                password = in.nextLine();
                                String result = register(username,password);
                                if(result.equals("Registered successfully!\n")){
                                    registered=true;
                                }
                                out.println(result);
                            }
                            break;
                        case "2":
                            if(logged){
                                out.println("Error, user is already logged");
                            }else{
                                out.println("Insert Username");
                                out.println("end");
                                username = in.nextLine();
                                out.println("Insert Password");
                                out.println("end");
                                password = in.nextLine();
                                String result = login(username,password);
                                if(result.equals("Logged successfully!\n")){
                                    logged=true;
                                }
                                out.println(result);
                            }
                            break;
                        default:
                            out.println("Bad input, try again\n");
                            break;
                    }
                }else{
                    out.println(menu);
                    out.println("end");
                    command=in.nextLine();
                    switch(command){
                        case "1":
                            out.println(logout());
                            logout=true;
                            break;
                        case "2":
                            playWORDLE();
                            break;
                        case "3":
                            sendWord();
                            break;
                        case "4":
                            sendMeStatistics();
                            break;
                        case "5":
                            share(username);
                            break;
                        case "6":
                            showMeSharing();
                            break;
                        default:
                            out.println("Bad input, try again\n");
                            break;
                    }
                }
            }while(!logout);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String register(String username, String password){
        if(hashMap.putIfAbsent(username,new UserData(password))!=null){
            return "Error, user "+username+" is already registered\n";
        }else{
            return "Registered successfully!\n";
        }
    }

    public String login(String username, String password) {
        user=hashMap.get(username);
        if(user==null){
            return "User not registered\n";
        }
        else if(password.equals(hashMap.get(username).getPassword())){
            return "Logged successfully!\n";
        }else{
            return "Error, wrong credentials\n";
        }
    }

    public String logout() {
       return "Logout done!";
    }

    public void playWORDLE(){
        secretWord=WordleServerMain.getSecretWord();
        System.out.println(secretWord);
        if(lastSWplayed==secretWord){
            out.println("Error, you have already played");
            out.println("Wait for the next Secret Word to be selected");
        }
        else{
            if(round==-1){
                round=0;
                out.println("Ready, you can play Wordle!");
            }else {
                out.println("Error, you are already playing!");
            }
        }
    }

    public void sendWord() {
        if (round >= 0 && round < 12) {
            String guessedWord = null;
            out.println("Guess the word");
            out.println("end");
            guessedWord = in.nextLine();
            if (!words.contains(guessedWord)) {
                out.println("Error, not a playable word!");
            } else if (guessedWords.contains(guessedWord)) {
                out.println("Error, word has been already played!");
            } else {
                if (guessedWord.equals(secretWord)) {
                    user.incrementGuesses();
                    user.addMatch(true);
                    lastSWplayed = secretWord;
                    round = -1;
                    guessedWords.clear();
                    hints.clear();
                    out.println("CONGRATULATIONS, YOU WON!");
                } else {
                    user.incrementGuesses();
                    out.println("Word is in the vocabulary");
                    String hint = null;
                    for(int k=0;k<10; k++){
                        if (secretWord.contains(String.valueOf(guessedWord.charAt(k)))) {
                            if (secretWord.charAt(k) == guessedWord.charAt(k)) {
                                if (hint != null) {
                                    hint = new String(hint.concat("+"));
                                } else {
                                    hint = "?";
                                }
                            } else {
                                if (hint != null) {
                                    hint = new String(hint.concat("?"));
                                } else {
                                    hint = "?";
                                }
                            }
                        } else {
                            if (hint != null) {
                                hint = new String(hint.concat("X"));
                            } else {
                                hint = "X";
                            }
                        }
                    }
                    guessedWords.add(round, guessedWord);
                    hints.add(hint);
                    for (int j = 0; j <= round; j++) {
                        out.println(guessedWords.get(j).toUpperCase() + "   Hint: " + hints.get(j).toUpperCase());
                    }
                    round++;
                }
            }
        }else if(round==12){
            user.addMatch(false);
            lastSWplayed = secretWord;
            round = -1;
            guessedWords.clear();
            hints.clear();
            out.println("You lost!");
        }else{
            out.println("You have to start the game in order to play!");
        }
    }

    public void sendMeStatistics(){
        if(lastSWplayed=="null"){
           out.println("Error, you have completed no games");
        }else{
            ArrayList<Boolean> matchesResults = user.getMatchesResults();
            int gamesWon=0;
            int lastStreak=0;
            int aux=0;
            Boolean exit=false;
            int maxStreak=0;
            int guessDistribution= user.getGuesses();
            int matchesPlayed=matchesResults.size();
            for(Boolean bool: matchesResults){
                if(bool==true){
                    gamesWon++;
                    aux++;
                    if(aux>maxStreak){
                        maxStreak=aux;
                    }
                }else{
                    aux=0;
                }
            }
            for(int i=matchesPlayed-1;i>=0;i--){
                if(!exit){
                    if(matchesResults.get(i)==true){
                        lastStreak++;
                    }else{
                        exit=true;
                    }
                }
            }
            guessDistribution=guessDistribution/matchesPlayed;
            gamesWon=(gamesWon/matchesPlayed)*100;
            out.println("Played matches: "+matchesPlayed+"\n");
            out.println("Games Won: "+gamesWon+"%\n");
            out.println("Last Streak: "+lastStreak+"\n");
            out.println("Max Streak: "+maxStreak+"\n");
            out.println("Guess Distribution: "+guessDistribution+"\n");
        }
    }

    public void share(String username) {
        try {
            DatagramSocket socket = new DatagramSocket();
            String s = new String("User: "+username+" has won!");
            DatagramPacket request = new DatagramPacket(s.getBytes(), s.length(), group, multicastPort);
            socket.send(request);
            out.println("SHARED");
            synchronized (notificationsSent){
                notificationsSent.add(s);
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void showMeSharing(){
        for(String s: notificationsSent){
            out.println(s);
        }
    }
}