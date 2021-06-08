package server_side;

import client_side.Player;
import utils.ChatroomType;
import utils.Config;
import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private final String name;
    private ServerSocket welcomeSocket;
    private HashMap<Player, ClientHandler> playersHandlers;
    private ChatRoom publicChatroom;
    private ChatRoom mafiaChatroom;
    private List<Player> gameWatchers; // dead players , which want to see the rest of game
    private List<Player> observers;
    private Config gameConfig;

    /**
     * constructor - singleton pattern
     *
     */
    public Server(int port)throws IOException{
        this.name = "God_" + port;
        welcomeSocket = new ServerSocket(port);
        playersHandlers = new HashMap<>();
        gameWatchers = new ArrayList<>();
        observers = new ArrayList<>();
        Logger.log("server created." , LogLevels.INFO , getClass().getName());
    }

    /**
     * to access mafia chatroom
     * @return mafia chatroom
     */
    public ChatRoom getMafiaChatroom() {
        return mafiaChatroom;
    }

    /**
     * notifying a player(sending message)
     * must be used in this way (player must not be null!):
     *          msg == null && msgType == null && createdMsg == 'a created before message object'  ---> to send a created before message object
     *          or
     *          msg == 'some string' && msgType == 'a MessageType enum' && createdMsg == null  ---> to create a new message object
     *
     *          otherwise ---> it doesn't do anything
     *
     * @param player to send message
     * @param msg  is the content of new message to send as 'God'
     * @param createdMsg is the created before(not a new message) message object
     */
    public void notifyMember(Player player ,String msg, MessageTypes msgType , Message createdMsg){
        ClientHandler clHandler = playersHandlers.get(player);
        clHandler.sendMsg(msg,msgType,createdMsg);
        Logger.log("end of trying to send msg to " + player.getName() , LogLevels.INFO , getClass().getName());
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
    public void notifyList(List<Player> list, String msg , MessageTypes msgType, Message createdMsg){
        for (Player player: list)
        {
            ClientHandler clHandler = playersHandlers.get(player);
            clHandler.sendMsg(msg,msgType,createdMsg);
        }
        Logger.log("end of trying to send msg to players in list" , LogLevels.INFO , getClass().getName());
    }

    /**
     * this method must be used before using chatroom methods
     * @param type is the type of chatroom
     */
    public void makeChatRoom(ChatroomType type , ArrayList<Player> members){
        if (type == ChatroomType.MAFIA_CHATROOM && mafiaChatroom == null)
        {
            mafiaChatroom = new ChatRoom(ChatroomType.MAFIA_CHATROOM , this , members);
        }
        else if (type == ChatroomType.PUBLIC_CHATROOM && publicChatroom == null)
        {
            publicChatroom = new ChatRoom(ChatroomType.PUBLIC_CHATROOM , this , members);
        }
        else {
            Logger.log("trying to make invalid chatroom" , LogLevels.WARN , getClass().getName());
        }
    }

    /**
     * getter
     * @return welcomeSocket
     */
    public ServerSocket getWelcomeSocket() {
        return welcomeSocket;
    }

    /**
     * getter
     * @return name of server
     */
    public String getName() {
        return name;
    }
}
