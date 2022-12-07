import java.util.ArrayList;

public class UserData {
    private String password=null;
    private ArrayList<Boolean> matchesResults;
    private int guesses;
    private int gamesWon;
    private int lastStreak;
    private int maxStreak;
    private float guessDistribution;
    public UserData(String password){
        this.password=password;
        matchesResults = new ArrayList<Boolean>();
    }

    public void addMatch(Boolean result){
        matchesResults.add(result);
    }
    public void setGamesWon(int gamesWon){
        this.gamesWon=gamesWon;
    }
    public void setLastStreak(int lastStreak){this.lastStreak=lastStreak;}
    public void setMaxStreak(int maxStreak){this.maxStreak=maxStreak;}
    public void setGuessDistribution(float guessDistribution) {
        this.guessDistribution = guessDistribution;
    }
    public String getPassword(){
        return password;
    }
    public ArrayList<Boolean> getMatchesResults(){
        return matchesResults;
    }
    public void incrementGuesses(){
        guesses++;
    }
    public int getGuesses() {
        return guesses;
    }
    public int getLastStreak(){return lastStreak;}
    public int getMaxStreak() {
        return maxStreak;
    }
    public int getGamesWon() {
        return gamesWon;
    }
    public float getGuessDistribution() {
        return guessDistribution;
    }
}
