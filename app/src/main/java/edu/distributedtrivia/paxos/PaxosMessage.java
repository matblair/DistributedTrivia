package edu.distributedtrivia.paxos;

// Import the simple JSON object parsers etc.
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Mat on 26/05/15.
 * A message object that will be sent to various paxos clients in order to then be accepted or
 * declined
 */

public class PaxosMessage {
    // Static JSON message parser for use by all subclasses
    private static JSONParser parser = new JSONParser();

    // Our enums for message types
    public enum MessageType{
        START, ROUND_START, WINNER, SCORE, QUESTION, ERROR, TIME, PROMISE,
        NEW_PLAYER, ACTION, ACCEPT
    }

    // Variables that represent the values
    private int roundNumber;
    private MessageType requestType;
    private long value;
    private String playerID;
    private String sender;


    // Contstructor to create response from variables
    public PaxosMessage(int roundNumber, MessageType type, long value, String sender){
        this.roundNumber = roundNumber;
        this.requestType = type;
        this.sender = sender;
        this.value = value;
    }

    public PaxosMessage(int roundNumber, MessageType type, long value,
                        String playerID, String sender){
        this.roundNumber = roundNumber;
        this.sender = sender;
        this.playerID = playerID;
        this.requestType = type;
        this.value = value;
    }

    // Set the round number of the message before we sent it
    public void setRoundNumber(int roundNumber){
        this.roundNumber = roundNumber;
    }

    // Returns a JSON representation of an object to be sent around using UDP and Multicast
    public String toJson(){
        // Create the json object
        JSONObject packet = new JSONObject();

        // Add the round number and value
        packet.put("round_number", this.roundNumber);
        packet.put("value", this.value);
        packet.put("player_id", this.playerID);
        packet.put("sender",sender);

        String type = "unknown";
        // Add the appropriate message type
        switch(requestType) {
            case ROUND_START:
                type = "start_round";
                break;
            case WINNER:
                type = "select_winner";
                break;
            case SCORE:
                type = "update_score";
                break;
            case QUESTION:
                type = "select_question";
                break;
            case START:
                type = "game_start";
                break;
            case NEW_PLAYER:
                type = "new_player";
                break;
            case TIME:
                type = "update_time";
                break;
            case PROMISE:
                type = "promise";
                break;
            case ACTION:
                type = "action";
                break;
            case ACCEPT:
                type = "accept";
                break;

            default:
                break;
        }

        // Insert the appropriate type.
        packet.put("type",type);
        return packet.toJSONString();
    }

    // Create an object from a JSON Object
    public static PaxosMessage buildFromJsonString(String jsonString) {
        // Try to validate and parse the JSON
        JSONObject packet = null;
        try {
            packet = (JSONObject) parser.parse(jsonString.replace("\0",""));
        } catch (ParseException e){
            System.out.print("Cannot validate JSON");
            e.printStackTrace();
        }

        if(packet != null){
            long value = (long) packet.get("value");
            long roundNum = (Long) packet.get("round_number");
            String type = (String) packet.get("type");
            String player = (String) packet.get("player_id");
            String sender = (String) packet.get("sender");
            MessageType mType = stringToMessageType(type);
            return new PaxosMessage((int)roundNum, mType, (int)value, player, sender);
        }
        // Otherwise no packed
        return null;
    }

    private static MessageType stringToMessageType(String string){
        if(string.equals("start_round")){
            return MessageType.ROUND_START;
        } else if(string.equals("select_winner")){
            return MessageType.WINNER;
        } else if (string.equals("update_score")){
            return MessageType.SCORE;
        } else if (string.equals("select_question")) {
            return MessageType.QUESTION;
        } else if (string.equals("game_start")){
            return MessageType.START;
        } else if (string.equals("update_time")){
            return MessageType.TIME;
        } else if (string.equals("new_player")){
            return MessageType.NEW_PLAYER;
        } else if (string.equals("promise")){
            return MessageType.PROMISE;
        } else if (string.equals("action")){
            return MessageType.ACTION;
        } else if (string.equals("accept")){
            return MessageType.ACCEPT;
        } else {
            return MessageType.ERROR;
        }
    }

    // Getters for private variables
    public int getRoundNumber(){ return this.roundNumber; }
    public long getValue(){ return this.value; }
    public MessageType getMessageType() { return  this.requestType; }
    public String getPlayerID() { return this.playerID; }
    public String getSender() { return this.sender; }

}
