import java.util.Scanner;

public class WordleServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Thread mainThread;
        while(true){
            mainThread = new Thread(new WordleServer());
            mainThread.start();
            System.out.println("Press 1 to reboot");
            if(scanner.nextLine()=="1"){
                mainThread.interrupt();
            }
        }
    }
}
