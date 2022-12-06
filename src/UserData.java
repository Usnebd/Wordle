import java.util.ArrayList;

public class UserData {
    private String password=null;
    private ArrayList<Boolean> matchesResults;
    private int guesses;
    public UserData(String password){
        this.password=password;
        matchesResults = new ArrayList<Boolean>();
    }

    public void addMatch(Boolean result){
        matchesResults.add(result);
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
}
