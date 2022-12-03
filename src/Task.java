import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Task implements Runnable{
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private ConcurrentHashMap hashMap;
    private Iterator<String> it;

    public Task(ConcurrentHashMap<String, UserData> hashMap,Socket socket){
        this.socket=socket;
        this.hashMap=hashMap;
    }
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            String menu = "Insert Command\n"+"1) register\n"+"2) login\n"+"3) logout\n"+"4) playWORDLE\n"+"5) sendWord\n"+"6) sendMeStatistics\n"+"7) share\n"+"8) showMeSharing\n";
            int command;
            String username;
            String password;
            do{
                out.println(menu);
                command = Integer.parseInt(in.nextLine());
                switch(command){
                    case 1:
                        out.println("Insert Username");
                        username = in.nextLine();
                        out.println("Insert Password");
                        password = in.nextLine();
                        out.println(register(username,password));
                        break;
                    case 2:
                        out.println("Insert Username");
                        username = in.nextLine();
                        out.println("Insert Password");
                        password = in.nextLine();
                        login(username,password);
                        break;
                    case 3:
                        out.println("Insert Username");
                        username = in.nextLine();
                        out.println(logout(username));
                        break;
                    case 4:
                        playWORDLE();
                        break;
                    case 5:
                        sendWord();
                        break;
                    case 6:
                        sendMeStatistics();
                        break;
                    case 7:
                        share();
                        break;
                    case 8:
                        showMeSharing();
                        break;
                    default:
                        out.println("Bad input, try again\n");
                        break;
                }
            }while(command != 3);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String register(String username, String password){
        if(hashMap.putIfAbsent(username,new UserData(password))!=null){
            return "Error, user "+username+" is already registered\n";
        }else{
            return "Success: Code 101\n";
        }
    }

    public String login(String username, String password) {
        UserData userData = (UserData) hashMap.get(username);
        if(password == userData.getPassword()){
            return "Success\n";
        }else{
            return "Error, wrong credentials\n";
        }
    }

    public String logout(String username) {
       return "Logout done!\n";
    }

    public void playWORDLE() {
    }

    public void sendWord() {

    }

    public void sendMeStatistics() {
    }

    public void share() {
    }

    public void showMeSharing() {
    }
}
