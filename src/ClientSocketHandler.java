import java.io.IOException;
import java.net.Socket;

public class ClientSocketHandler implements Runnable{
    private Socket socket;
    public ClientSocketHandler(Socket socket){
        this.socket=socket;
    }
    public void run(){
        Boolean exit=false;
        while(!exit){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
                exit=true;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
