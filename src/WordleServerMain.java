import java.util.Scanner;

public class WordleServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Thread server;
        while(true){
            server = new Thread(new WordleServer());
            server.start();
            System.out.println("Press 1 to reboot");
            if(scanner.nextLine().equals("1")){
                System.out.println("Rebooting.....");
                server.interrupt();
                while(server.isAlive()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Rebooted!");
            }
        }
    }
}
