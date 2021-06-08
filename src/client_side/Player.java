package client_side;

import utils.*;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * belongs to 'mafia game'
 * a class to store players' data and play
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Player implements Serializable {
    private String name;
    private boolean isReady;
    private boolean isAlive;
    private boolean isAllowedToChat;
    private Socket liveConnection;
//    private OutputStream outputStream;
    private ObjectOutputStream outObj;
//    private InputStream inputStream;
    private ObjectInputStream inObj;
    private Role_Group role;
    private Role_Group group;
    private Scanner userInput;
    private MsgReceiver msgReceiver;
    private MsgSender msgSender;
    private Player myVote;
    private Message startMsg;
    private Config config;

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
            outObj = new ObjectOutputStream(liveConnection.getOutputStream());
            inObj = new ObjectInputStream(liveConnection.getInputStream());
            msgSender = new MsgSender(this); // may have bug here
            msgReceiver = new MsgReceiver(this);
//            startMsgSender();
//            startMsgReceiver();
            Logger.log(getName() + " added.", LogLevels.INFO ,getClass().getName());

        }catch (IOException e){
            Logger.log("cannot make input or output stream" , LogLevels.ERROR , Player.class.getName());
            System.out.println("ERROR - exiting...");
            System.exit(-1);
        }
    }

    /**
     * the main playing loop of the client side , it takes action depending on server orders
     *
     */
    public void playLoop(){
        Message msg = null;

        System.out.println("All players joined.");
        stopMsgSender();
        stopMsgReceiver();
        while (true)
        {
            msg = getMsg();
            if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_SET_ROLE) {
                // must be in this format, for example:  'MAFIA_GROUP GODFATHER' , or it can be 'CITIZEN_GROUP DETECTIVE'
                String[] split = msg.getContent().trim().split(" ");
                setGroup(roleFromString(split[0]));
                setRole(roleFromString(split[1]));
            } else if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_FIRST_NIGHT_GREETING) {
                System.out.println("Now game starts.It's first night.Open your eyes...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Logger.log("interrupted while sleeping.", LogLevels.ERROR, getClass().getName());
                }
                System.out.print("Shhhh...You are ");
                System.out.print("\033[0;31m");
                System.out.print(getRole());
                System.out.print("\033[0m");
                System.out.print(" in ");
                System.out.print("\033[0;31m");
                System.out.println(getGroup());
                System.out.print("\033[0m");

                if (getRole() == Role_Group.MAYOR || getRole() == Role_Group.DOCTOR || getGroup() == Role_Group.MAFIA_GROUP) {
                    // teammates in this format: sepehr,ali,sahand, ... or it can be 'no body' which means no teammates
                    String[] split = getMsg().getContent().split(",");
                    if (split[0].equals("no body")) {
                        System.out.println("You have no teammate at nights!");
                    } else {
                        System.out.println("Your teammates:");
                        for (String name : split) {
                            System.out.print("\033[0;31m");
                            System.out.println(name);
                            System.out.print("\033[0m");
                        }
                    }
                }

            } else if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT) {
                if (getGroup() == Role_Group.MAFIA_GROUP) {
                    startMsgSender();
                    startMsgReceiver();
                }
            }




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

    /**
     * to set role at start
     * @param group Mafia or citizen
     */
    public void setGroup(Role_Group group) {
        this.group = group;
    }

    /**
     * to set role at start
     * @param role detective , godfather ,...
     */
    public void setRole(Role_Group role) {
        this.role = role;
    }

    /**
     * to identify role enum from string type
     * @param str entered string
     * @return enum Role_Group
     */
    public Role_Group roleFromString(String str){
        if (str.equals(Role_Group.MAFIA_GROUP.toString()))
            return Role_Group.MAFIA_GROUP;
        if (str.equals(Role_Group.GODFATHER.toString()))
            return Role_Group.GODFATHER;
        if (str.equals(Role_Group.DOCTOR_LECTER.toString()))
            return Role_Group.DOCTOR_LECTER;
        if (str.equals(Role_Group.NORMAL_MAFIA.toString()))
            return Role_Group.NORMAL_MAFIA;
        if (str.equals(Role_Group.CITIZEN_GROUP.toString()))
            return Role_Group.CITIZEN_GROUP;
        if (str.equals(Role_Group.DOCTOR.toString()))
            return Role_Group.DOCTOR;
        if (str.equals(Role_Group.DETECTIVE.toString()))
            return Role_Group.DETECTIVE;
        if (str.equals(Role_Group.SNIPER.toString()))
            return Role_Group.SNIPER;
        if (str.equals(Role_Group.CITIZEN.toString()))
            return Role_Group.CITIZEN;
        if (str.equals(Role_Group.MAYOR.toString()))
            return Role_Group.MAYOR;
        if (str.equals(Role_Group.PSYCHOLOGIST.toString()))
            return Role_Group.PSYCHOLOGIST;
        if (str.equals(Role_Group.DIE_HARD.toString()))
            return Role_Group.DIE_HARD;
        else return null;
    }

    /**
     * to get identify the start message between chats , when the game isn't started yet
     * @param startMsg message got from server
     */
    public void setStartMsg(Message startMsg) {
        this.startMsg = startMsg;
    }

    /**
     * to access start message for syncing msgReceiver and player
     * @return start message
     */
    public Message getStartMsg() {
        return startMsg;
    }

    /**
     * to set config by server
     * @param config config file got from server
     */
    public void setConfig(Config config) {
        this.config = config;
    }
}
