package client_side;

import utils.*;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * belongs to 'mafia game'
 * a class to store players' data and play
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public abstract class Player implements Serializable {
    private String name;
    private boolean isReady;
    private boolean isAlive;
    private boolean isAllowedToChat;
    private Socket liveConnection;
    private OutputStream outputStream;
    private ObjectOutputStream outObj;
    private InputStream inputStream;
    private ObjectInputStream inObj;
    private Role_Group role;
    private Role_Group group;
    private Scanner userInput;
    private MsgReceiver msgReceiver;
    private MsgSender msgSender;
    private Player myVote;

    /**
     * constructor
     * @param name is the name of player
     * @param socket connection socket
     * @param role role of this player
     * @param group mafia group or citizen group
     */
    public Player(String name , Socket socket , Role_Group role , Role_Group group){
        this.name = name;
        isReady = false;
        isAlive = true;
        isAllowedToChat = true;
        liveConnection = socket;
        this.role = role;
        this.group = group;
        userInput = new Scanner(System.in);
        try {
            outputStream = liveConnection.getOutputStream();
            outObj = new ObjectOutputStream(outputStream);
            inputStream = liveConnection.getInputStream();
            inObj = new ObjectInputStream(inputStream);
            msgSender = new MsgSender(this); // may have bug here
            msgReceiver = new MsgReceiver(this);
            startMsgSender();
            startMsgReceiver();
//            execService = Executors.newFixedThreadPool(1);
//            execService.execute(msgReceiver);
            Logger.log(getName() + " added and its msgReceiver and msgSender are running - start playing",
                    LogLevels.INFO ,getClass().getName());

        }catch (IOException e){
            Logger.log("cannot make input or output stream" , LogLevels.ERROR , Player.class.getName());
            System.out.println("ERROR - exiting...");
            System.exit(-1);
        }
    }

    /**
     * to access name
     * @return name of player
     */
    public String getName() {
        return name;
    }

    /**
     * to know if a player is alive or not
     * @return true if the player is alive , false if not
     */
    public boolean isAlive(){
        return isAlive;
    }

    /**
     * to access group of player
     * @return group name(as an enum)
     */
    public Role_Group getGroup() {
        return group;
    }

    /**
     * a method to do player's acts at night
     *
     */
    public abstract void act();

    /**
     * to send a single message
     * @param content content of message
     * @param type type of message
     * @param target is the name of target (if exists) , this parameter must be null when there is no target
     */
    public void sendMsg(String content , ChatroomType type , MessageTypes msgType , String target){

        try{
            Message msg = new Message(getName() , content , type , msgType);
            msg.setTarget(target);
            outObj.writeObject(msg);
        }catch (IOException e)
        {
            Logger.log( this.getName()+ " can't send message with type " + type + " to server." ,
                    LogLevels.ERROR , this.getClass().getName());
        }
    }

    /**
     * to get message from server
     * @return object message
     */

    public Message getMsg(){
        try
        {
            return (Message)inObj.readObject();

        }catch (IOException e){
            Logger.log("can't get message object from server." , LogLevels.ERROR , getClass().getName());
        }
        catch (ClassNotFoundException e){
            Logger.log("can't find the class Message." , LogLevels.ERROR , getClass().getName());
        }
        return null;
    }
//
//    public boolean vote(String plyName){ // must be overridden for some characters
//        ArrayList<Player> alives = (ArrayList<Player>) SharedData.getSharedData().getAlives();
//        for (Player player:alives)
//        {
//            if (player.getName().equalsIgnoreCase(plyName))
//            {
//                myVote = player;
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * to know if is ready to join the game
     * @return if player is ready , returns true , else false
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * to know if is allowed to chat
     * @return allowed == true , not allowed == false
     */
    public boolean isAllowedToChat() {
        return isAllowedToChat;
    }

    /**
     * to access open socket
     * @return open socket
     */
    public Socket getLiveConnection() {
        return liveConnection;
    }

    /**
     * to access role of player
     * @return enum role
     */
    public Role_Group getRole() {
        return role;
    }


    /**
     * to stop msgReceiver
     */
    public void stopMsgReceiver(){
        msgReceiver.getThread().interrupt();
    }

    /**
     * to start msgReceiver
     */
    public void startMsgReceiver(){
        msgReceiver.getThread().start();
    }

    /**
     * to stop msgSender
     */
    public void stopMsgSender(){
        msgSender.getThread().interrupt();
    }

    /**
     * to start msgSender
     */
    public void startMsgSender(){
        msgSender.getThread().start();
    }

    /**
     * to clear voted before players
     */
    public void clearVote(){
        myVote = null;
    }

}
