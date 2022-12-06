import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerTask implements Runnable{
    public static InetAddress group;
    public static int multicastPort;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private ConcurrentHashMap hashMap;
    private ArrayList<String> words;
    private String lastSWplayed="null";

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
            String username;
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
                            share();
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
        if(hashMap.get(username)!=null){
            UserData userData = (UserData) hashMap.get(username);
            if(password.equals(userData.getPassword())){
                return "Logged successfully!\n";
            }else{
                return "Error, wrong credentials\n";
            }
        }else{
            return "Error, wrong credentials\n";
        }
    }

    public String logout() {
       return "Logout done!";
    }

    public void playWORDLE() {
        String secretWord=WordleServerMain.getSecretWord();
        if(!lastSWplayed.equals(secretWord)){
            String guessedWord = null;
            ArrayList<String> guessedWords = new ArrayList<String>(12);
            ArrayList<String> hints = new ArrayList<String>(12);
            Boolean won=false;
            out.println("Ready, you can play Wordle!");
            for(int i=0;i<12;i++){
                if(!won){
                    do{
                        out.println("Guess the word");
                        out.println("end");
                        guessedWord=in.nextLine();
                        if(!words.contains(guessedWord)){
                            out.println("Error, not a playable word!");
                            if(i>0){
                                for(int z=0;z<i;z++){
                                    out.println(guessedWords.get(z).toUpperCase()+"   "+hints.get(z).toUpperCase());
                                }
                            }
                        }
                        if(guessedWords.contains(guessedWord)){
                            out.println("Error, word is already played!");
                            if(i>0){
                                for(int y=0;y<i;y++){
                                    out.println(guessedWords.get(y).toUpperCase()+"   "+hints.get(y).toUpperCase());
                                }
                            }
                        }
                    }while(!words.contains(guessedWord) || guessedWords.contains(guessedWord));
                    if(guessedWord.equals(secretWord)){
                        won=true;
                        out.println("CONGRATULATIONS, YOU WON!");
                    }else{
                        guessedWords.add(i,guessedWord);
                        String hint=null;
                        for(int j=0;j<10;j++){
                            if(secretWord.contains(String.valueOf(guessedWord.charAt(j)))){
                                if(secretWord.charAt(j) == guessedWord.charAt(j)){
                                    if(hint!=null){
                                        hint = new String(hint.concat("+"));
                                    }else{
                                        hint = "?";
                                    }
                                }else{
                                    if(hint!=null){
                                        hint = new String(hint.concat("?"));
                                    }else{
                                        hint = "?";
                                    }
                                }
                            }else{
                                if(hint!=null){
                                    hint = new String(hint.concat("X"));
                                }else{
                                    hint = "X";
                                }
                            }
                        }
                        hints.add(hint);
                        for(int k=0;k<=i;k++){
                            out.println(guessedWords.get(k).toUpperCase()+"   "+hints.get(k).toUpperCase());
                        }
                    }
                }
            }
            if(!won){
                out.println("Sorry, you've lost the match!");
            }
            lastSWplayed=secretWord;
        }else{
            out.println("Error, you have already played");
            out.println("Wait for the next Secret Word to be selected");
        }
    }

    public void sendWord() {

    }

    public void sendMeStatistics() {
    }

    public void share() {
    }

    public void showMeSharing() {
    }
}