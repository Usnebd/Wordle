import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerTask implements Runnable{
    public static InetAddress group;
    public static int multicastPort;
    private BufferedReader bufferedReader;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private ConcurrentHashMap hashMap;
    private Iterator<String> it;
    private String[] secretWord;
    private String lastSWplayed="null";

    public ServerTask(ConcurrentHashMap<String, UserData> hashMap, Socket socket, String[] secretWord){
        this.socket=socket;
        this.hashMap=hashMap;
        this.secretWord=secretWord;
    }

    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            String menu = "Insert Command\n"+"1) logout\n"+"2) playWORDLE\n"+"3) sendWord\n"+"4) sendMeStatistics\n"+"5) share\n"+"6) showMeSharing";
            String startMenu="Insert Command\n"+"1) register\n"+"2) login\n";
            Boolean logged=false;
            Boolean registered=false;
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
                                if(result.equals("Registered successfully\n")){
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
                                if(result.equals("Logged successfully\n")){
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
                            logged=false;
                            out.println(logout());
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
            }while(!command.equals("3"));
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
            return "Registered successfully\n";
        }
    }

    public String login(String username, String password) {
        UserData userData = (UserData) hashMap.get(username);
        if(password.equals(userData.getPassword())){
            return "Logged successfully\n";
        }else{
            return "Error, wrong credentials\n";
        }
    }

    public String logout() {
       return "Logout done!";
    }

    public void playWORDLE() {
        if(!lastSWplayed.equals(secretWord[0])){
            FileReader fileReader = null;
            String guessedWord = null;
            String[] guessedWords = new String[12];
            String[] hints = new String[12];
            String aux = null;
            Boolean isWord=false;
            Boolean won=false;
            try {
                fileReader = new FileReader("src\\words.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                out.println("Ready, you can play Wordle!");
                for(int i=0;i<12;i++){
                    if(!won){
                        out.println("Guess the word");
                        out.println("end");
                        bufferedReader.reset();
                        while(!isWord){
                            guessedWord=in.nextLine();
                            do{
                                aux=bufferedReader.readLine();
                                if(guessedWord.equals(aux)){
                                    isWord=true;
                                }
                            }while(aux!=null);
                            if(!isWord){
                                out.println("Error, not a playable word!");
                                out.println("end");
                            }
                        }
                        if (guessedWord.equals(secretWord[0])){
                            won=true;
                            out.println("CONGRATULATIONS, YOU WON!");
                        }else{
                            guessedWords[i]=guessedWord;
                            for(int j=0;j<12;j++){
                                if(secretWord[0].contains(String.valueOf(guessedWord.charAt(j)))){
                                    if(secretWord[0].charAt(j) == guessedWord.charAt(j)){

                                    }
                                }else{

                                }
                            }
                        }
                    }
                }
                lastSWplayed=secretWord[0];
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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