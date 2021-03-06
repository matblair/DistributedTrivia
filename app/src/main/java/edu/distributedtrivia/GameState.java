package edu.distributedtrivia;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

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
    private boolean started;

    // Score keeping functionality
    private HashMap<String,Long> responseTime;

    private Player playerAnswered;
    private boolean correctAnswer;

    public GameState(Context context) {
        roundNum = 1;
        started = false;
        players = new ArrayList();
        qB = new QuestionBank(context);
    }

    public void gameSetup(int numRounds, boolean paxos){
        this.numRounds = numRounds;
        this.paxos = paxos;
        this.responseTime = new HashMap<String, Long>();

        try {
            currentQuestion = qB.nextQuestion(FIRST_QUESTION);
        } catch(UnknownQuestion e){
            System.out.println("No Question.");
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

    public boolean hasStarted() { return started; }

    public int nextQuestion(){
        return qB.randomQuestionID();
    }

    public void nextRound(int questionID) {
        if(!started){
            started = true;
        }
        try {
            currentQuestion = qB.nextQuestion(questionID);
        } catch(UnknownQuestion e){
        System.out.println("No Question!");
        }
        this.responseTime.clear();
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
                return p2.getScore() - p1.getScore();
            }
        });

        for (int i=0; i<players.size(); i++) {
            Player newPos = players.get(i);
            newPos.setPosition(i+1);
            players.set(i, newPos);
        }
    }

    public Player getPlayer(String playerName){
        for(Player p: players){
            if(p.getName().equals(playerName)){
                return p;
            }
        }
        return null;
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

    // Method to add response time
    public void addPlayerResponse(String player_id, long value){
        this.responseTime.put(player_id, value);
    }

    // Find the fastest
    public String getFastestPlayer(){
            long min = Long.MAX_VALUE;
            String fastest = null;
            Iterator iterator = responseTime.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                System.out.println("Key is " + entry.getKey() + " value is " + entry.getValue());
                String key = (String) entry.getKey();
                Long value = (Long) entry.getValue();
                if(value<min){
                        fastest = key; min = value;
                }
            }
            return fastest;
    }

}
