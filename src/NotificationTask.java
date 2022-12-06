import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MulticastChannel;
import java.util.ArrayList;

public class NotificationTask implements Runnable {
    private InetAddress group;
    private int multicastPort;
    private ArrayList notifications;
    private int timeout;
    public NotificationTask(InetAddress group, int multicastPort, ArrayList notifications, int timeout) {
        this.group = group;
        this.multicastPort = multicastPort;
        this.notifications=notifications;
        this.timeout=timeout;
    }

    public void run() {
        try {
            MulticastSocket ms = new MulticastSocket(multicastPort);
            ms.joinGroup(group);
            byte[] buffer = new byte[8192];
            ms.setSoTimeout(timeout);
            while (!Thread.currentThread().isInterrupted()){
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                try {
                    ms.receive(dp);
                } catch (SocketTimeoutException ignore) {}
                if(dp.getData().length==0){

                }
                String s = new String(dp.getData(), 0, dp.getLength());
                notifications.add(s);
            }
            System.out.println("Notification thread shutted down!");
            ms.leaveGroup(group);
            ms.close();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
