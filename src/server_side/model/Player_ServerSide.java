package server_side.model;

import server_side.sendGet.MsgReceiver;
import server_side.sendGet.MsgSender;
import utils.Message;
import utils.Role_Group;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class Player_ServerSide implements Serializable {
    private String name;
    private Role_Group role;
    private Role_Group group;
    private Socket connection;
    private boolean isAlive;
    private transient MsgSender msgSender;
    private transient MsgReceiver msgReceiver;
    private transient ArrayBlockingQueue<Message> sharedInbox;
    private boolean isSilenced;


    /**
     * constructor - role and group should be set later
     * @param name of player
     * @param connection is the opened socket of player
     */
    public Player_ServerSide(String name , Socket connection , ObjectInputStream inObj , ObjectOutputStream outObj , ArrayBlockingQueue<Message> sharedInbox , LinkedTransferQueue<Message> readyMsgs)
    {
        this.connection = connection;
        this.sharedInbox = sharedInbox;
        this.name = name;
        isAlive = true;
        msgSender = new MsgSender(outObj);
        msgReceiver = new MsgReceiver(name , inObj , sharedInbox , readyMsgs);
        isSilenced = false;
    }

    /**
     * to access name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter
     * @return role in game
     */
    public Role_Group getRole() {
        return role;
    }

    /**
     * getter
     * @return group in game
     */
    public Role_Group getGroup() {
        return group;
    }

    /**
     * getter
     * @return true if alive , false if not
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * getter
     * @return msgReceiver
     */
    public MsgReceiver getMsgReceiver() {
        return msgReceiver;
    }

    /**
     * getter
     * @return msgSender
     */
    public MsgSender getMsgSender() {
        return msgSender;
    }

    /**
     * setter
     * @param role in game
     */
    public void setRole(Role_Group role) {
        this.role = role;
    }

    /**
     * setter
     * @param group mafia or citizen group
     */
    public void setGroup(Role_Group group) {
        this.group = group;
    }

    /**
     * to silence player
     * @param silenced true or false
     */
    public void setSilenced(boolean silenced) {
        isSilenced = silenced;
    }

    /**
     * getter
     * @return socket
     */
    public Socket getConnection() {
        return connection;
    }
}
