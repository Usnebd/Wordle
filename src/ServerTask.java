import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerTask implements Runnable{
    private UserData user;
    private final Socket socket;
    private Scanner in;
    private PrintWriter out;
    private int round=-1;
    private int guesses=0;
    private final ArrayList<Boolean> matchesResults=new ArrayList<>();      //array che memorizza l'esito delle partite
    private String lastSWplayed="null";
    private String secretWord;
    private final ArrayList<String> guessedWords = new ArrayList<String>(12);
    private final ArrayList<String> hints = new ArrayList<String>(12);

    public ServerTask(Socket socket){
        this.socket=socket;                      //inizializzo il task con il socket della connessione e SW
        secretWord=WordleServer.getSecretWord();
    }

    public void run() {
        try {
            in = new Scanner(socket.getInputStream());              //strem di input
            out = new PrintWriter(socket.getOutputStream(),true);  //stream di output
            String menu = "Insert Command\n"+"1) logout\n"+"2) playWORDLE\n"+"3) sendWord\n"+"4) sendMeStatistics\n"+"5) share\n"+"6) showMeSharing";
            String startMenu="Insert Command\n"+"1) register\n"+"2) login\n";
            boolean logged=false;
            boolean logout=false;
            String username = null;
            String password;
            String command;
            do{
                try {
                    if(!logged){     //se non ho effettuato l'accesso
                        out.println(startMenu);
                        out.println("end");
                        command=in.nextLine();      //leggo il comando
                        switch(command){
                            case "1":
                                out.println("Insert Username");
                                out.println("end");
                                username = in.nextLine();
                                out.println("Insert Password");
                                out.println("end");
                                password = in.nextLine();
                                out.println(register(username,password));
                                break;
                            case "2":
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
                                break;
                            default:
                                out.println("Bad input, try again\n");
                                break;
                        }
                    }else{      //se ho effettuato l'accesso
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
                                out.println("showMeSharing()");
                                break;
                            default:
                                out.println("Bad input, try again\n");
                                break;
                        }
                    }
                } catch (NoSuchElementException ignore) {}
            }while(!logout && !Thread.currentThread().isInterrupted());
            WordleServer.hashMap.replace(username,user);                                        //prima di terminare il task aggiorno la coppia <username,Userdata> con un oggetto aggiornato con i dati più recenti
            in.close();
            out.close();                                                           //chiudo i vari stream e il socket
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String register(String username, String password){
        if(WordleServer.hashMap.putIfAbsent(username,new UserData(password))!=null){     //se esiste già una coppia(putIfAbsent restituisce null) allora restituisco errore
            return "Error, user "+username+" is already registered\n";
        }else{
            return "Registered successfully!\n";                            //altrimenti lo inserisco nella hashMap in modo atomico e restituisco questo
        }
    }

    public String login(String username, String password) {
        user=WordleServer.hashMap.get(username);                                     //cerco la coppia <username,userdata> nell'hashMap
        if(user==null){                                                 //se non esiste nessun oggetto Userdata associato a "username" allora vuol dire che username non è registrato
            return "User not registered\n";
        }
        else if(password.equals(WordleServer.hashMap.get(username).getPassword())){  //se la trova e la password combacia con quella passata per parametro allora confermo l'esito postivo del login
            return "Logged successfully!\n";
        }else{                                                           //altrimenti vuol dire che ho sbagliato password
            return "Error, wrong credentials\n";
        }
    }

    public String logout() {
       return "Logout done!";
    }

    public void playWORDLE(){                                           //avvio il gioco
        secretWord=WordleServer.getSecretWord();
        if(lastSWplayed.equals(secretWord)){                            //confronto la secretWord appena presa del server
            out.println("Error, you have already played");
            out.println("Wait for the next Secret Word to be selected");
        }                                                               //se è uguale all'ultima SW giocata allora vuol dire che il server non ha ancora estratto una nuova SW
        else{                                                           //altrimenti pulisco gli array usati per salvare i progressi della partita e aggiorno il numero del round attuale
            if(round==-1){
                round=0;
                guessedWords.clear();
                hints.clear();
                out.println("Ready, you can play Wordle!");
            }else {
                out.println("Error, you are already playing!");
            }
        }
    }

    public void sendWord() {                                   //se la partita è avviata e non ha superato il dodicesimo round allora non è finito
        if (round >= 0 && round < 12) {                        //e quindi posso provare a indovinare la parola
            String guessedWord = null;
            out.println("Guess the word");
            out.println("end");
            guessedWord = in.nextLine();                        //leggo la parola spedita dal client
            guessedWord = guessedWord.toLowerCase();
            if (!WordleServer.words.contains(guessedWord)) {                    //se il vocabolarion NON contiente la parola do errore
                out.println("Error, not a playable word!");
            } else if (guessedWords.contains(guessedWord)) {        //se ho già giocato la parola do errore
                out.println("Error, word has been already played!");
            } else {
                if (guessedWord.equals(secretWord)) {               //se la parola passa i check controllo innanzitutto se è la SecretWord
                    matchesResults.add(true);                       //aggingo l'esito della partita all'array che tiene traccia delle partite giocate nella sessione
                    user.setGuessDistribution(((user.getGuessDistribution()*user.getPlayedMatches())+guesses+1)/ (user.getPlayedMatches()+1));
                    user.setPlayedMatches(user.getPlayedMatches()+1);
                    user.setGamesWon(user.getGamesWon()+1);                 //uso i metodi setter per aggiornare i dati dell'utente all'ultima partita giocata
                    user.setLastStreak(user.getLastStreak()+1);
                    user.setMaxStreak(findMaxStreak(user.getMaxStreak()));
                    hints.add("++++++++++");
                    guessedWords.add(guessedWord);
                    lastSWplayed = secretWord;                          //aggiorno l'ultima secretWord giocata con la secretWord indovinata
                    round = -1;                                         //aggiorno la variabile round
                    guesses=0;
                    out.println("CONGRATULATIONS, YOU WON!");
                } else {
                    guesses++;                                          //altrimenti incremento il numero di tentativi
                    out.println("Word is in the vocabulary");
                    String hint = null;
                    for(int k=0;k<10; k++){
                        if (secretWord.contains(String.valueOf(guessedWord.charAt(k)))) {       //vado a confrontare le lettere della SW e della GW
                            if (secretWord.charAt(k) == guessedWord.charAt(k)) {
                                if (hint != null) {
                                    hint = new String(hint.concat("+"));                    //e vado quindi a generare il suggerimento
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
                    guessedWords.add(round, guessedWord);                           //aggiungo la GuessedWord all'array
                    hints.add(hint);                                                //aggiungo l'hint all'array
                    for (int j = 0; j <= round; j++) {
                        out.println(guessedWords.get(j).toUpperCase() + "   Hint: " + hints.get(j).toUpperCase()); //mando al client la GW con il suggerimento
                    }
                    round++;                                                        //incremento i round
                    if(round==12){                                                  //se era il dodicesimo round e non ho indovinato ho perso
                        matchesResults.add(false);
                        user.setGuessDistribution(((user.getGuessDistribution()*user.getPlayedMatches())+guesses)/ (user.getPlayedMatches()+1));
                        user.setPlayedMatches(user.getPlayedMatches()+1);
                        user.setLastStreak(0);
                        guesses=0;
                        lastSWplayed = secretWord;
                        round = -1;
                        out.println("You lost!");
                    }
                }
            }
        }else{
            out.println("You have to start the game in order to play!");
        }
    }

    public int findMaxStreak(int maxStreak){            //funzione ausiliaria usata per calcolare il MaxStreak
        int aux=0;
        for(Boolean bool: matchesResults){
            if(bool){
                aux++;
                if(aux>maxStreak){
                    maxStreak=aux;
                }
            }else{
                aux=0;
            }
        }
        return maxStreak;
    }

    public void sendMeStatistics(){
        if(user.getPlayedMatches()==0){
           out.println("Error, you have completed no games");
        }else{
            out.println("Played matches: "+user.getPlayedMatches()+"\n");
            out.println("Games Won: "+(user.getGamesWon()/user.getPlayedMatches())*100+"%\n");
            out.println("Last Streak: "+user.getLastStreak()+"\n");
            out.println("Max Streak: "+user.getMaxStreak()+"\n");
            out.println("Guess Distribution: "+user.getGuessDistribution()+"\n");
        }
    }

    public void share() {
        if(!lastSWplayed.equals("null") && lastSWplayed.equals(secretWord)){                    //se ho giocato almeno una partita && non ne ho iniziata un'altra
            try(DatagramSocket socket = new DatagramSocket()){                                  // creo un datagram socket con il try-with-resources
                String s="WORDLE "+user.getPlayedMatches()+": "+guessedWords.size()+"/12\n\n";
                for(String hint:hints){
                    s=s.concat(hint.concat("\n"));                                          //genero il messaggio
                }
                DatagramPacket request = new DatagramPacket(s.getBytes(), s.getBytes().length, WordleServer.group, WordleServer.multicastPort);       //lo incapsulo nel datagramma
                socket.send(request);                                                           //spedisco il datagramma verso il gruppo multicast
                out.println("SHARED");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
           out.println("Error"); 
        }
    }
}
