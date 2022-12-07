public class UserData {
    private String password;
    private int gamesWon=0;
    private int lastStreak=0;
    private int maxStreak=0;
    private int playedMatches=0;
    private double guessDistribution=0;
    public UserData(String password){
        this.password=password;
    }
    public void setPlayedMatches(int playedMatches){this.playedMatches=playedMatches;}
    public void setGamesWon(int gamesWon){
        this.gamesWon=gamesWon;
    }
    public void setGuessDistribution(double guessDistribution) {
        this.guessDistribution = guessDistribution;
    }
    public void setLastStreak(int lastStreak){this.lastStreak=lastStreak;}
    public void setMaxStreak(int maxStreak){this.maxStreak=maxStreak;}
    public String getPassword(){
        return password;
    }
    public int getPlayedMatches() {
        return playedMatches;
    }
    public int getLastStreak(){return lastStreak;}
    public int getMaxStreak() {
        return maxStreak;
    }
    public int getGamesWon() {
        return gamesWon;
    }
    public double getGuessDistribution() {
        return guessDistribution;
    }
}
