package edu.distributedtrivia.paxos;

import android.text.style.UpdateAppearance;

import java.util.ArrayList;
import java.util.Stack;

import edu.distributedtrivia.GameState;
import edu.distributedtrivia.Globals;
import edu.distributedtrivia.NotifiableApplication;


/**
 * Created by Mat on 26/05/15.
 */
public class PaxosHandler {

    // Methods for actions
    public enum Actions{
        REFRESH, START_GAME, NEXT_SCREEN, ANSWERED, BUZZED, FIRST, QUESTION, PROPOSAL
    }

    // SINGLETON FOR THE WIN!! I Hate myself.
    private static PaxosHandler globalHandler;

    // Variable for paxos state in this system
    private int round_number;
    private GameState gameState;

    // To keep track of what state the system is in at any given point
    private enum State {
        ACCEPTOR, PROPOSER, UNDECIDED
    }

    // Our actual state
    private State currentState;
    private PaxosSocket socket;
    private String senderID;

    // The previously promised message
    private PaxosMessage pending;
    private PaxosMessage future;

    // Save all the responses so we know when we have a quorum
    private ArrayList<String> quorum;

    // Stack to keep track of history
    private Stack<PaxosMessage> history;

    // Private constructor for singleton method
    private PaxosHandler (String senderID) {
        this.senderID = senderID;
        currentState = State.UNDECIDED;
        round_number = 0;
        history = new Stack<PaxosMessage>();
        history.setSize(50);
        quorum = new ArrayList<String>();
        socket = new PaxosSocket();
    }

    public String getSenderID() { return senderID; }

    // Public access to receive Paxos Handler
    public static PaxosHandler getHandler(String senderID) {
        if (globalHandler == null) {
            globalHandler = new PaxosHandler(senderID);
        }
        return globalHandler;
    }

    // Register the GameState
    public boolean setGameState(GameState gs){
        if(gameState==null){
            gameState = gs;
            return true;
        } else {
            return false;
        }
    }

    // Generic method to handle any incoming paxos messages based on message type and decide
    // what should be done.
    public void handleMessage(PaxosMessage message){
        // Alright so we need to decide what message type we are accepting currently based on the
        // state we are in. We need to remmeber that the highest order number must always be
        // acted on first.
        PaxosMessage.MessageType type = message.getMessageType();
        // If the type is a non paxos request then handle it normally
        if((type== PaxosMessage.MessageType.TIME) || (type== PaxosMessage.MessageType.START)
                || (type== PaxosMessage.MessageType.NEW_PLAYER) ){
            handleNormal(type, message);
        } else {
            handlePaxos(type, message);
        }
        // Finish up by saving it in history
        history.add(message);
    }

    // Methods to send non paxos things
    public void sendName(String name){
        // Build a message to send
        PaxosMessage message = new PaxosMessage(0, PaxosMessage.MessageType.NEW_PLAYER, (long)0,
                name, senderID);
        // Send it
        socket.sendMessage(message);
    }

    // Methods to send non paxos things
    public void sendStart(Boolean paxos, int num){
        // Build a message to send
        String data = null;
        if(!paxos){
            data = "no_paxos";
        }
        PaxosMessage message = new PaxosMessage(0, PaxosMessage.MessageType.START, (long) num,
                data, senderID);
        // Send it
        socket.sendBackgroundMessage(message);
    }

    // Send the time we completed the message in
    public void sendTime(String player_id, long player_time){
        // Build a message for sending
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.TIME,
                player_time, player_id, senderID);
        // Send  message
        socket.sendBackgroundMessage(message);
    }

    // Propose a new round
    public void proposeNewRound(PaxosMessage finalMsg){
        // Increment the round number
        round_number += 1;
        // Build the message
        System.out.println("Round number in propose new round is " + round_number);

        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.ROUND_START,
                (long)0, null, senderID);
        // Save the message that we ultimate want to create
        future = finalMsg;
        // Send the message
        socket.sendBackgroundMessage(message);
        // Become a proposer
        currentState = State.PROPOSER;
    }

    // These methods act as proposal methods in the paxos system


    public PaxosMessage proposeWinnerMsg(String winner_id){
        // Build round number
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.WINNER,
                (long)0, winner_id, senderID);
        return message;
    }

    public PaxosMessage proposeScoreMsg(String player_id, int score){
        // Build round number
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.WINNER,
                score, player_id, senderID);
        return message;
    }


    public PaxosMessage proposeQuestionMsg(int question_id){
        // Build round number
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.QUESTION,
                (long)question_id, null, senderID);
        return message;
    }

    // Private methods to deal with handling normal objects
    private void handleNormal(PaxosMessage.MessageType type, PaxosMessage message){
        switch(type){
            case START:
                if (Globals.gs == null){
                   handleStart(message);
                }
                break;
            case TIME:
                System.out.println("Reciving and handling!");
                if(gameState != null){
                    gameState.addPlayerResponse(message.getPlayerID(), message.getValue());
                    updateApplication(Actions.BUZZED);
                }
                break;
            case NEW_PLAYER:
                // Add player
                Globals.addUserName(message.getPlayerID());
                updateApplication(Actions.REFRESH);
                break;
            default:
                break;
        }
    }

    private void handleStart(PaxosMessage msg){
        // Crate the game state if we don't have it
        if (Globals.gs == null  ){
            NotifiableApplication app = (NotifiableApplication) NotifiableApplication.getContext();
            Globals.gs = new GameState(app.getApplicationContext());
            gameState = Globals.gs;
            // We use the value field as num rounds and the player_id field as stupid
            Boolean usePaxos = true;
            if ((msg.getPlayerID()!=null) && msg.getPlayerID().equals("no_paxos")){
                usePaxos = false;
            }
            gameState.gameSetup((int)msg.getValue(),usePaxos);
        }
        // Start the game here!
        updateApplication(Actions.START_GAME);
    }

        // Private method to handle paxos
    private void handlePaxos(PaxosMessage.MessageType type, PaxosMessage message) {
        // If we are in a proposal state
        switch (type) {
            case ROUND_START:
                handleProposal(message);
                break;
            case WINNER:
            case QUESTION:
            case SCORE:
                // If it's the current round number then it is what we have agreed to
                if ((message.getRoundNumber() == round_number) && (currentState == State.ACCEPTOR)) {
                    // Accept the message
                    pending = message;
                    // Send a final acknowledgement
                    acknowledgeAcceptance(message);
                } // Ignore otherwise
                break;
            case ACTION:
                if ((message.getRoundNumber() == round_number) && (currentState == State.ACCEPTOR)) {
                    // Action the existing thing
                    actionConsensus();
                }
                break;
            case ACCEPT:
                // If we are not a proposer we don't care about it
                if(currentState==State.PROPOSER && message.getRoundNumber() == round_number){
                    // Save the result, if we have quorom
                    if(haveQuorum()){
                        // Then send a message to action
                        sendAction(message);
                    }
                }
            case PROMISE:
                // If we are not a proposer we don't care about it
                if(currentState==State.PROPOSER){
                    // Save the result
                    // If we have a quorum (i.e. everyone joined
                    if(haveQuorum()){
                        // Send the message we were going to send
                        future.setRoundNumber(round_number);
                        socket.sendBackgroundMessage(future);
                        // Clear quorum
                        clearQuorum();
                    }
                }
            default:
                // We don't handle so fail
                break;
        }
    }

    private boolean haveQuorum(){
        // We have quorum when everyone has accepted
        return true;
//        return (quorums.size() >= Globals.userNames.size());
    }

    private void clearQuorum(){
        // Clear the quorum
        quorum.clear();
    }

    // Send a message as a proposer to make all acceptors action
    private void sendAction(PaxosMessage message){
        // Build the response message
        PaxosMessage response = new PaxosMessage(message.getRoundNumber(),
                PaxosMessage.MessageType.ACTION, (long)0, null, senderID);
        // Send that message
        socket.sendBackgroundMessage(response);
    }

    // Method to send a final acceptance
    private void acknowledgeAcceptance(PaxosMessage message){
        // Build the message
        PaxosMessage response = new PaxosMessage(message.getRoundNumber(),
                PaxosMessage.MessageType.ACCEPT,(long)0, null, senderID);
        // Send message
        socket.sendBackgroundMessage(response);
    }

    // Methods to update state based on acceptance
    private void acceptQuestion(PaxosMessage message){
        // Assume if we've got here we can update
        gameState.nextRound((int)message.getValue());
    }

    private void acceptProposal(PaxosMessage message){
        // Save the message to pending
        pending = message;
        // Build accept message
        PaxosMessage response = new PaxosMessage(message.getRoundNumber(),
                PaxosMessage.MessageType.ACTION, (long)0, null, senderID);
        // Send that message
        socket.sendBackgroundMessage(message);
    }

    public void actionConsensus(){
        // Find the existing action
        if (pending != null){
            // I should action this action!
            System.out.println("I Should action this now! " + pending.toJson());
            updateApplication(Actions.REFRESH);
            // Now clear it
            pending = null;
            currentState = State.UNDECIDED;
        }
    }

    // Method to notify application that it might need to check change in status
    public void updateApplication(Actions action){
        NotifiableApplication app = (NotifiableApplication) NotifiableApplication.getContext();
        app.notifyCurrentActivity(action);
    }
    public boolean shouldAgree(int proposalNumber){
        // We agree on proposals if proposalNumber is greater than last agreed
        return (proposalNumber > round_number);
    }

    public void handleProposal(PaxosMessage message){
        if (shouldAgree(message.getRoundNumber())) {
            // Send a promise, set our round number ot the current round number and
            // save the message as what we have promised
            round_number = message.getRoundNumber();
            sendPromise(round_number);
        } // Ignore otherwise
    }

    // Methods to create and send accept messages
    private void sendPromise(int round_number){
         // Build the message
        updateApplication(Actions.PROPOSAL);
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.PROMISE,
                (long)0, null, senderID);
        // Send the message
        socket.sendBackgroundMessage(message);
        // Become a proposer
        currentState = State.ACCEPTOR;

    }

}
