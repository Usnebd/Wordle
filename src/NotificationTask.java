import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
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
            DatagramChannel channel = ms.getChannel();
            channel.configureBlocking(false);
            ms.joinGroup(group);
            int length;
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            while (!Thread.currentThread().isInterrupted()){
                buffer.clear();
                channel.read(buffer);
                buffer.flip();
                length=buffer.getInt();
                byte[] notificationBytes = new byte[length];
                buffer.get(notificationBytes);
                notifications.add(new String(notificationBytes));
            }
            ms.leaveGroup(group);
            ms.close();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
