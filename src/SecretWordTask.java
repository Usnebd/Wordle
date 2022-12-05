import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

public class SecretWordTask implements Runnable{
    private static Random random;
    private static BufferedReader bufferedReader;
    private static int lines;
    private static String[] secretWord;
    public SecretWordTask(Random random, BufferedReader bufferedReader, int lines, String[] secretWord){
        SecretWordTask.random=random;
        SecretWordTask.bufferedReader=bufferedReader;
        SecretWordTask.lines=lines;
        SecretWordTask.secretWord=secretWord;
    }

    public static String findRandomWord(){
        int wordLine = random.nextInt(lines);
        int i=0;
        String secretWord;
        try {
            bufferedReader.reset();
            do{
                secretWord=bufferedReader.readLine();
                i++;
            }while(i!=wordLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return secretWord;
    }

    public void run() {
        secretWord[0]=findRandomWord();
    }
}
