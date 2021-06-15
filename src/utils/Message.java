package utils;

import java.io.Serializable;

/**
 * a simple message
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Message implements Serializable{
    private String sender;
    private ChatroomType chatroomType;
    private String content;
    private MessageTypes msgType;
    private String target;

    /**
     * constructor
     * @param sender name of sender
     * @param content main content of message
     * @param chatroomType where is it going(destination)
     * @param msgType what type it is ? (normal chat , an order to server , ...)
     */
    public Message(String sender , String content , ChatroomType chatroomType , MessageTypes msgType , String target){
        this.sender = sender;
        this.content = content;
        this.chatroomType = chatroomType;
        this.msgType = msgType;
        this.target = target;
    }

    /**
     * to access sender name
     * @return sender name
     */
    public String getSender() {
        return sender;
    }

    /**
     * to access type of message
     * @return type of msg
     */
    public ChatroomType getChatroomType() {
        return chatroomType;
    }

    /**
     * to access content of message
     * @return content of message
     */
    public String getContent() {
        return content;
    }

    /**
     * to access message type
     * @return enum MessageTypes
     */
    public MessageTypes getMsgType() {
        return msgType;
    }

    /**
     * to access target name
     * @return target name
     */
    public String getTarget() {
        return target;
    }

    /**
     * setter - used in msgSeparator
     * @param chatroomType the new chatroomType
     */
    public void setChatroomType(ChatroomType chatroomType) {
        this.chatroomType = chatroomType;
    }

}
