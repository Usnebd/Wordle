import java.util.ArrayList;
import java.util.Random;

public class SecretWordTask implements Runnable {
    private static Random random;
    private static int lines;
    private static ArrayList<String> words;
    public SecretWordTask(Random random, int lines, ArrayList<String> words){   //il costruttore di SecretWordTask viene inizializzato
        SecretWordTask.random=random;                                           //con un oggetto Random
        SecretWordTask.lines=lines;                                             //il numero di righe del file words.txt
        SecretWordTask.words=words;                                             //l'array contenete le parole del vocabolario
    }

    public void run() {
        String secretWord = words.get(random.nextInt(lines));                   //random.nextInt(lines) genera un numero random compreso tra 0 e lines
        WordleServer.setSecretWord(secretWord);                                 //andiamo poi a prendere la parola n-esima e aggiorniamo la secretWord del server
    }
}
