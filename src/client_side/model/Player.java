package client_side.model;

import client_side.sendGet.MsgReceiver;
import client_side.sendGet.MsgSender;
import utils.*;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedTransferQueue;

/**
 * belongs to 'mafia game'
 * a class to store players' data and play
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Player implements Serializable {
    private final String name;
    private boolean isAlive;
    private boolean isAllowedToChat;
    private Socket liveConnection;
    private ObjectOutputStream outObj;
    private ObjectInputStream inObj;
    private Role_Group role;
    private Role_Group group;
    private MsgReceiver msgReceiver;
    private MsgSender msgSender;
    private Message startMsg;
    private Config config;
    private LinkedTransferQueue<Message> closeChatroomMsg;
    private boolean isSilenced;

    /**
     * constructor
     * @param name is the name of player
     * @param socket connection socket
     * @param role role of this player
     * @param group mafia group or citizen group
     */
    public Player(String name , Socket socket , Role_Group role , Role_Group group){
        closeChatroomMsg = new LinkedTransferQueue<>();
        this.name = name;
        isAlive = true;
        isAllowedToChat = true;
        liveConnection = socket;
        this.role = role;
        this.group = group;
        try {
            outObj = new ObjectOutputStream(liveConnection.getOutputStream());
            inObj = new ObjectInputStream(liveConnection.getInputStream());
            msgSender = new MsgSender(this); // may have bug here
            msgReceiver = new MsgReceiver(this , closeChatroomMsg);
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

                sleep(2000 , "interrupted while sleeping.");

                System.out.print("Shhhh...You are ");
                System.out.print("\033[0;31m");
                System.out.print(getRole());
                System.out.print("\033[0m");
                System.out.print(" in ");
                System.out.print("\033[0;31m");
                System.out.println(getGroup());
                System.out.print("\033[0m");

                if (getRole() == Role_Group.MAYOR || getRole() == Role_Group.DOCTOR || getGroup() == Role_Group.MAFIA_GROUP) {
                    // teammates in this format: sepehr,ali,sahand, ... or it can be 'nobody' which means no teammates
                    String[] split = getMsg().getContent().split(",");
                    if (split[0].equals("nobody")) {
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
                System.out.println("Night greeting ended.going for day...");

            } else if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT){ // all acts are implemented by typing the act name by client
                int roleTime = config.getEachRoleNightActingTime();
                System.out.println("It's night now.");
                System.out.print("You have ");
                System.out.print("\033[0;31m");
                System.out.print(String.valueOf(roleTime) + "s");
                System.out.print("\033[0m");
                System.out.println(" for doing your role...");
                startMsgSender();
                startMsgReceiver();

                // here , waits until server lets to go on
                Message closeMsg = getCloseMsg("interrupted in getting close chatroom message. shouldn't happen! - for " + getName());

                System.out.println("Time ended.going for day...");
                stopMsgSender();
                stopMsgReceiver();
            }else if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_DAY_PUBLIC_CHAT)
            {
                System.out.println("Last night summary: ");
                System.out.print("\033[0;36m");
                System.out.println(msg.getContent());
                System.out.print("\033[0m");

                if (!isSilenced)
                {
                    startMsgSender();
                    startMsgReceiver();
                }

                Message closeMsg = getCloseMsg("interrupted in getting transfer msg in day. - for " + getName());

                if (!isSilenced)
                {
                    stopMsgReceiver();
                    stopMsgSender();
                }
                System.out.println("Day time ended.going for vote...");

            }else if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_VOTE) // the incoming message for this must be in special format:
                // player1Name,player2Name,player3Name,....
            {
                setSilenced(false);
                System.out.print("\033[0;31m");
                System.out.print("Attention!");
                System.out.print("\033[0m");
                System.out.println(" Enter your vote in the following format:(voting to yourself will be ignored!)");
                System.out.print("\033[0;31m");
                System.out.println("vote PlayerName");
                System.out.println();
                System.out.print("\033[0m");
                System.out.print("You have ");
                System.out.print("\033[0;31m");
                System.out.print(String.valueOf(config.getVotingTime()) + "s");
                System.out.print("\033[0m");
                System.out.println(" for voting...");
                System.out.println();

                String[] split = msg.getContent().trim().split(",");
                for (int i = 1 ; i <= split.length ; i++)
                {
                    System.out.println(i + ") " + split[i-1]);
                }
                startMsgSender();
                startMsgReceiver();

                Message closeMsg = getCloseMsg("interrupted in getting transfer msg. - voting time - for" + getName());

                stopMsgSender();
                stopMsgReceiver();
                System.out.println("Voting time ended. The one who goes out is ...");
                msg = getMsg(); // result of voting

                System.out.print("\033[0;31m");
                System.out.println(msg.getContent());
                System.out.print("\033[0m");
                System.out.println();
                System.out.println("Going for another night...");

            }else if (msg.getMsgType() == MessageTypes.END_OF_GAME)
            {
                System.out.println(msg.getContent()); // special content should be made in server
                try {
                    inObj.close();
                    outObj.close();
                    liveConnection.close();
                }catch (IOException e)
                {
                    Logger.log("can't close objectInput or output , or socket." , LogLevels.WARN , getClass().getName());
                }
                System.exit(0);
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
            outObj.writeObject(new Message(getName() , content , type , msgType , target));
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

    public MsgSender getMsgSender() {
        return msgSender;
    }

    /**
     * sleep method
     * @param time to sleep
     * @param msg data to save in log file
     */
    private void sleep(int time , String msg){
        try {
            Thread.sleep(time);
        }catch (InterruptedException e) {
            Logger.log(msg, LogLevels.WARN, getClass().getName());
        }
    }

    /**
     * used for synchronizing msgReceiver and playLoop
     * @param msg the details of error to save in Logger and print
     * @return Transferred message
     */
    private Message getCloseMsg(String msg){
        try {
            return closeChatroomMsg.take();
        }catch (InterruptedException e)
        {
            System.out.println(msg);
            Logger.log(msg,LogLevels.ERROR,getClass().getName());
        }
        return null;
    }

    /**
     * to silence the player
     * @param silenced true or false
     */
    public void setSilenced(boolean silenced) {
        isSilenced = silenced;
    }

    /**
     * getter
     * @return true or false
     */
    public boolean isSilenced() {
        return isSilenced;
    }
}
