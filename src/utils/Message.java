package utils;

import java.io.Serializable;

/**
 * belongs to 'mafia game'
 * a class to have data of a message from players or from god
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Message implements Serializable {
    String senderName;
    String content;
    int type; // it can be 0 or 1 , 0 is for god , 1 is for players

    public Message(String senderName, String content , int type){
        this.senderName = senderName;
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString(){
        return senderName + ": " + content;
    }
}
