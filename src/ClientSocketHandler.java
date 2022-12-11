import java.io.IOException;
import java.net.Socket;

public class ClientSocketHandler implements Runnable{

    private final Socket socket;
    public ClientSocketHandler(Socket socket){         //è un thread che se viene arrestato chiude il socket del client
        this.socket=socket;
    }
    public void run(){
        boolean exit=false;
        while(!exit){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {     //evento interrupt, Thread.sleep() lancia un'eccezione
                exit=true;                              //viene impostata la flag a true e quindi il ciclo while termina
            }
        }
        try {
            socket.close();                              //appena il ciclo termina si chiude il socket lato client
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
