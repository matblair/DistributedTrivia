package edu.distributedtrivia;

/**
 * Created by jburd on 23/05/15.
 */
public class Player {
    private String ipAddress;
    private String name;
    private int score;
    private int position;

    public Player(String ip, String name){
        this.ipAddress = ip;
        this.name = name;
        score = 0;
        position = 1;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void updateScore(boolean correct){
        if(correct) {
            score += Globals.CORRECT_SCORE;
        }else{
            score =- Globals.INCORRECT_SCORE;
        }
    }
}