import java.util.ArrayList;
import java.util.Random;

public class SecretWordTask implements Runnable {
    private static Random random;
    private static int lines;
    private static ArrayList<String> words;
    public SecretWordTask(Random random, int lines, int secretWordRate, ArrayList<String> words){
        SecretWordTask.random=random;
        SecretWordTask.lines=lines;
        SecretWordTask.words=words;
    }

    public void run() {
        String secretWord = words.get(random.nextInt(lines));
        WordleServerMain.setSecretWord(secretWord);
    }
}
