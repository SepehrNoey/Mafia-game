package server_side;

import client_side.Player;
import utils.ChatroomType;
import utils.Message;
import utils.MessageTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * a chatroom with its messages and its members
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class ChatRoom {
    private ChatroomType type;
    private List<Player> members;
    private List<Message> inbox;
    private Server server;

    /**
     * constructor
     * @param type is the type of chatroom
     */
    public ChatRoom(ChatroomType type ,Server server , ArrayList<Player> members){
        this.type = type;
        this.members = members;
        this.server = server;
        inbox = new ArrayList<>();
    }
    /**
     * to notify all members of a list with sending message
     * must be used in this way (player list must not be null!):
     *          msg == null && msgType == null && createdMsg == 'a created before message object'  ---> to send a created before message object
     *          or
     *          msg == 'some string' && msgType == 'a MessageType enum' && createdMsg == null  ---> to create a new message object
     *
     *          otherwise ---> it doesn't do anything
     *
     * @param list clients list(watchers ,  observers , alives , or other list of players)
     * @param msg  is the content of new message to send as 'God'
     * @param msgType is the MessageType enum
     * @param createdMsg is the created before(not a new message) message object
     */
    public void notifyMems(List<Player> list, String msg , MessageTypes msgType, Message createdMsg){
        server.notifyList(list,msg ,msgType,createdMsg);
    }

}
