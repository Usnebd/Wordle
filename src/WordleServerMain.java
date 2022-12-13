import java.util.Scanner;

public class WordleServerMain {
    public static void main(String[] args) {                        //creo una classe che ingloba WordleServer
        Scanner scanner = new Scanner(System.in);                   //tale classe servirà a gestire il ciclo di vita del server (avvio, riavvio e spegnimento)
        Thread server = null;
        boolean badInput=false;
        while(true){
            if(!badInput){                                          //controlla se l'utente ha immesso un comando non presente nella lista
                server = new Thread(new WordleServer());            //false alla prima iterazione del while
                server.start();                                     //quindi nella prima iterazione avvierà il server
            }
            badInput=false;                                         //pulisco la flag in caso di n°esima iterazione (n>1// )
            System.out.println("Press 1 to reboot");
            String command=scanner.nextLine();
            if(command.equals("1")){                                //se l'amministratore del server digita "1" il server si riavvia
                System.out.println("Rebooting.....");
                server.interrupt();                                 //arresto il thread server
                while(server.isAlive()){                            //attendo che completi le operazioni di spegnimento
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Rebooted!");
            }else{
                System.out.println("Invalid input!");
                badInput=true;
            }
        }
    }
}
