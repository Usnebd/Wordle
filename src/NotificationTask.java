import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class NotificationTask implements Runnable {
    private final InetAddress group;
    private final int multicastPort;
    private final ArrayList notifications;
    private final int timeout;
    public NotificationTask(InetAddress group, int multicastPort, ArrayList notifications, int timeout) {
        this.group = group;
        this.multicastPort = multicastPort;
        this.notifications=notifications;
        this.timeout=timeout;
    }

    public void run() {                                                 //thread in esecuzione sul client che si occupa di raccogliere le notifiche
        try {
            MulticastSocket ms = new MulticastSocket(multicastPort);    //apro il socket in ascolto nella porta "multicastPort"
            ms.joinGroup(group);                                        //mi unisco al gruppo multicast
            byte[] buffer = new byte[8192];                             //creo un buffer per la ricezione dei dati
            ms.setSoTimeout(timeout);                                   //imposto un timeout di "timeout" millisecondi
            boolean notData;
            while(!Thread.currentThread().isInterrupted()){             //se il thread non viene arrestato allora continuo a rimanere in ascolto
                notData=false;
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);  //creo il datagramma che raccoglietà i dati ricevuti
                try {
                    ms.receive(dp);                                             //rimango in ascolto
                } catch (SocketTimeoutException e) {                            //scaduto il timeout senza nessuna riposta
                    notData=true;                                               //imposto la flag true segnalando non ci sono dati nel buffer
                }
                if(!notData){                                      //se la flag è false allora vuol dire che il timeout non è scaduto e che quindi ho dati
                    String s = new String(dp.getData(), 0, dp.getLength());
                    notifications.add(s);                          //vado salvare la notifica ricevuta in un array
                }
            }
            ms.leaveGroup(group);                                   //prima di terminare il thread lascio il gruppo multicast
            ms.close();                                             //e chiudo il socket
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
