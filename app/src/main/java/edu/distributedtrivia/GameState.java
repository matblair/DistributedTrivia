package edu.distributedtrivia;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

import edu.distributedtrivia.QuestionBank;

public class GameState {
    private int numRounds;
    private int roundNum;
    private ArrayList<Player> players;
    private QuestionBank qB;
    private Question currentQuestion;
    private boolean paxos;
    private Player playerAnswered;
    private boolean correctAnswer;

    public GameState() {
        roundNum = 1;
        players = new ArrayList();
        qB = new QuestionBank("assets/trivia_questions.json");
    }

    public void gameSetup(int numRounds, boolean paxos){
        this.numRounds = numRounds;
        this.paxos = paxos;
    }

    public boolean isPaxos() {
        return paxos;
    }

    public void setPaxos(boolean paxos) {
        this.paxos = paxos;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void nextRound() {
        currentQuestion = qB.nextQuestion();
        roundNum++;
    }

    public void updatePlayer(Player playerUpdate, boolean correct){
        for (Player player : players) {
            if(player.getIpAddress().equals(playerUpdate.getIpAddress())){
                players.set(players.indexOf(player), playerUpdate);
                playerAnswered = playerUpdate;
                correctAnswer = correct;
                break;
            }
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Question getCurrentQuestion(){
        return currentQuestion;
    }

    public int getRoundNum(){
        return roundNum;
    }

    public int getNumRounds(){
        return numRounds;
    }

    public Player getPlayerAnswered() {
        return playerAnswered;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }
}
