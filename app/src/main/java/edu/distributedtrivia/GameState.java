package edu.distributedtrivia;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.distributedtrivia.QuestionBank;

public class GameState {
    // Constants for the game setup
    private static final int FIRST_QUESTION =0;


    // Variables to be replicated
    private int numRounds;
    private int roundNum;
    private ArrayList<Player> players;
    private QuestionBank qB;
    private Question currentQuestion;
    private boolean paxos;
    private Player playerAnswered;
    private boolean correctAnswer;

    public GameState(Context context) {
        roundNum = 1;
        players = new ArrayList();
        qB = new QuestionBank(context);
    }

    public void gameSetup(int numRounds, boolean paxos){
        this.numRounds = numRounds;
        this.paxos = paxos;
        try {
            currentQuestion = qB.nextQuestion(FIRST_QUESTION);
        } catch(UnknownQuestion e){
            System.out.println("We done fucked up now.");
        }

        for(String name : Globals.userNames){
            players.add(new Player(name));
        }
        Globals.userNames.clear();
    }

    public boolean isPaxos() {
        return paxos;
    }

    public void setPaxos(boolean paxos) {
        this.paxos = paxos;
    }

    public void nextRound() {
        try {
            currentQuestion = qB.nextQuestion(roundNum);
        } catch(UnknownQuestion e){
            System.out.println("We done fucked up even more!");
        }
        roundNum++;
    }

    public void updatePlayer(Player playerUpdate, boolean correct){
        for (Player player : players) {
            if(player.getName().equals(playerUpdate.getName())){
                players.set(players.indexOf(player), playerUpdate);
                playerAnswered = playerUpdate;
                correctAnswer = correct;
                break;
            }
        }

        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.getScore() - p2.getScore();
            }
        });

        for (int i=0; i<players.size(); i++) {
            Player newPos = players.get(i);
            newPos.setPosition(i+1);
            players.set(i, newPos);
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
