import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WordleServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService server = Executors.newSingleThreadExecutor();
        while(true){
            server.execute(new WordleServer());
            System.out.println("Press 1 to reboot");
            if(scanner.nextLine().equals("1")){
                System.out.println("Rebooting.....");
                server.shutdown();
                try {
                    if(!server.awaitTermination(3000, TimeUnit.MILLISECONDS)){
                        server.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Rebooted!");
            }
        }
    }
}
