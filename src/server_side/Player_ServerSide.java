package server_side;

import server_side.clientHandler.MsgReceiver;
import server_side.clientHandler.MsgSender;
import utils.Message;
import utils.Role_Group;

import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class Player_ServerSide implements Serializable {
    private String name;
    private Role_Group role;
    private Role_Group group;
    private boolean isAlive;
    private transient MsgSender msgSender;
    private transient MsgReceiver msgReceiver;
    private transient ArrayBlockingQueue<Message> sharedInbox;
    private transient LinkedTransferQueue<Message> readyMsgs;

    /**
     * constructor - role and group should be set later
     * @param name of player
     * @param connection is the opened socket of player
     */
    public Player_ServerSide(String name , Socket connection , ArrayBlockingQueue<Message> sharedInbox)
    {
        this.sharedInbox = sharedInbox;
        this.name = name;
        isAlive = true;
        msgSender = new MsgSender(connection);
        msgReceiver = new MsgReceiver(connection , sharedInbox);
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
     * to set a shared inbox for ready messages
     * @param readyMsgs readyMsg inbox
     */
    public void setReadyMsgs(LinkedTransferQueue<Message> readyMsgs) {
        this.readyMsgs = readyMsgs;
    }
}
