import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class NotificationTask implements Runnable {
    private InetAddress group;
    private int multicastPort;
    private Boolean exit=false;
    private ArrayList notifications;
    public NotificationTask(InetAddress group, int multicastPort, ArrayList notifications) {
        this.group = group;
        this.multicastPort = multicastPort;
        this.notifications=notifications;
    }

    public void run() {
        try {
            MulticastSocket ms = new MulticastSocket(multicastPort);
            ms.joinGroup(group);
            byte[] buffer = new byte[8192];
            while (!exit) {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ms.receive(dp);
                String s = new String(dp.getData(), 0, dp.getLength());
                notifications.add(s);
            }
            ms.leaveGroup(group);
            ms.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeNotification(){
        exit=true;
    }
}
