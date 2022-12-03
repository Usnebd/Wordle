public class UserData {
    private String password=null;
    private int gamesWon=0;
    private int lastStreak=0;
    private int maxStreak=0;

    public UserData(String password){
        this.password=password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public void setGamesWon(int gamesWon){
        this.gamesWon=gamesWon;
    }
    public void setLastStreak(int lastStreak){
        this.lastStreak=lastStreak;
    }
    public void setMaxStreak(int maxStreak){
        this.maxStreak=maxStreak;
    }
    public String getPassword(){
        return password;
    }
    public int getGamesWon(){
        return gamesWon;
    }
    public int getLastStreak(){
        return lastStreak;
    }
    public int getMaxStreak(){
        return maxStreak;
    }

}
