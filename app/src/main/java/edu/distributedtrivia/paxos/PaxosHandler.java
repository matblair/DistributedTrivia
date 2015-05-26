package edu.distributedtrivia.paxos;

import java.util.ArrayList;
import java.util.Stack;

import edu.distributedtrivia.GameState;
import edu.distributedtrivia.Globals;


/**
 * Created by Mat on 26/05/15.
 */
public class PaxosHandler {
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
    private PaxosMessage promise;
    private PaxosMessage future;

    // Save all the responses so we know when we have a quorum
    private ArrayList<PaxosMessage> messages;
    private ArrayList<String> promisers;

    // Stack to keep track of history
    private Stack<PaxosMessage> history;

    // Private constructor for singleton method
    private PaxosHandler (String senderID) {
        this.senderID = senderID;
        currentState = State.UNDECIDED;
        round_number = 0;
        history = new Stack<PaxosMessage>();
        history.setSize(50);
        socket = new PaxosSocket();
    }

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
        if((type== PaxosMessage.MessageType.TIME) || (type== PaxosMessage.MessageType.START)){
            handleNormal(type, message);
        } else {
            handlePaxos(type, message);
        }
        // Finish up by saving it in history
        history.add(message);
    }

    public void proposeNewRound(PaxosMessage finalMsg){
        // Increment the round number
        round_number += 1;
        // Build the message
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.ROUND_START,
                (long)0, null, senderID);
        // Save the message that we ultimate want to create
        future = finalMsg;
        // Send the message
        socket.sendMessage(message);
        // Become a proposer
        currentState = State.PROPOSER;
    }


    public void sendTime(String player_id, long player_time){
        // Build a message for sending
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.TIME,
                player_time, player_id, senderID);
        // Send  message
        socket.sendMessage(message);
    }

    // These methods act as proposal methods in the paxos system
    public PaxosMessage proposeWinnerMsg(String winner_id){
        // Build round number
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.WINNER,
                (long)0, winner_id, senderID);
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
                // Start the game here!
                break;
            case TIME:
                if(gameState != null){
                    gameState.addPlayerResponse(message.getPlayerID(), message.getValue());
                }
                break;
            case NEW_PLAYER:
                if(gameState != null){
                    // Add player
                    Globals.userNames.add(message.getPlayerID());
                }
                break;
            default:
                break;
        }
    }

    // Private method to handle paxos
    private void handlePaxos(PaxosMessage.MessageType type, PaxosMessage message) {
        // If we are in a proposal state
        switch (type) {
            case ROUND_START:
                handleProposal(message);
                break;
            case WINNER:
                // If it's the current round number then it is what we have agreed to
                if (message.getRoundNumber() == round_number) {

                } // Ignore otherwise
                break;
            case QUESTION:
                // If it's the current round number then it is what we have agreed to
                if (message.getRoundNumber() == round_number) {

                } // Ignore otherwise
                break;
            case SCORE:
                // If it's the current round number then it is what we have agreed to
                if (message.getRoundNumber() == round_number) {

                } // Ignore otherwise
                break;
            case PROMISE:
                // If we are not a proposer we don't care about it
                if(currentState==State.PROPOSER){
                    // Save the result

                    // If we have a quorum (i.e. everyone joined
                    if(haveQuorum()){

                    }

                }
            default:
                // We don't handle so fail
                break;
        }
    }

    private boolean haveQuorum(){
        // We have quorum when everyone has accepted
        return (promisers.size() >= Globals.userNames.size());
    }

    // Methods to update state based on acceptance
    private void acceptQuestion(PaxosMessage message){
        // Assume if we've got here we can update
        gameState.nextRound((int)message.getValue());
    }

    public boolean shouldAgree(int proposalNumber){
        // We agree on proposals if proposalNumber is greater than last agreed
        return (proposalNumber > round_number);
    }

    public void handleProposal(PaxosMessage message){
        if (shouldAgree(message.getRoundNumber())) {
            // Send a promise, set our round number ot the current round number and
            // save the message as what we have promised
            promise = message;
            round_number = message.getRoundNumber();
            sendPromise(round_number);
        } // Ignore otherwise
    }
    // Methods to create and send accept messages
    private void sendPromise(int round_number){
         // Build the message
        PaxosMessage message = new PaxosMessage(round_number, PaxosMessage.MessageType.PROMISE,
                (long)0, null, senderID);
        // Send the message
        socket.sendMessage(message);
        // Become a proposer
        currentState = State.ACCEPTOR;

    }

}