package server_side;

import utils.ChatroomType;
import utils.Config;
import utils.Message;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {
    private final String name;
    private ServerSocket welcomeSocket;
    private List<Player_ServerSide> players;
    private ChatRoom publicChatroom;
    private ChatRoom mafiaChatroom;
    private List<Player_ServerSide> gameWatchers; // dead players , which want to see the rest of game
    private List<Player_ServerSide> observers;
    private Config gameConfig;
    private ArrayBlockingQueue<Message> sharedInbox;

    /**
     * constructor - singleton pattern
     *
     */
    public Server(int port)throws IOException{
        this.name = "God_" + port;
        welcomeSocket = new ServerSocket(port);
        players = new ArrayList<>();
        gameWatchers = new ArrayList<>();
        observers = new ArrayList<>();
        sharedInbox = new ArrayBlockingQueue<>(200);
        Logger.log("server created." , LogLevels.INFO , getClass().getName());
    }

    /**
     * to access mafia chatroom
     * @return mafia chatroom
     */
    public ChatRoom getMafiaChatroom() {
        return mafiaChatroom;
    }

//
//    public void notifyMember(Player_ServerSide player ,String msg, MessageTypes msgType , Message createdMsg){
//        ClientHandler clHandler = players.get(player);
//        clHandler.sendMsg(msg,msgType,createdMsg);
//        Logger.log("end of trying to send msg to " + player.getName() , LogLevels.INFO , getClass().getName());
//    }

//    public void notifyList(List<Player_ServerSide> list, String msg , MessageTypes msgType, Message createdMsg){
//        for (Player_ServerSide player: list)
//        {
//            ClientHandler clHandler = players.get(player);
//            clHandler.sendMsg(msg,msgType,createdMsg);
//        }
//        Logger.log("end of trying to send msg to players in list" , LogLevels.INFO , getClass().getName());
//    }

    /**
     * this method must be used before using chatroom methods
     * @param type is the type of chatroom
     */
    public void makeChatRoom(ChatroomType type , ArrayList<Player_ServerSide> members){
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

    /**
     * setter for game config
     * @param gameConfig is the config created in JoinServer
     */
    public void setGameConfig(Config gameConfig) {
        this.gameConfig = gameConfig;
    }

    /**
     * to access number of current joined players
     * @return number of joined players
     */
    public int currentPlyNum(){
        return players.size();
    }

    /**
     * to check if this name exists or not
     * @param name the name to check
     * @return true if exists , false if not
     */
    public boolean isNameExist(String name){
        if (players.size() == 0)
            return false;
        for(Player_ServerSide player : players)
        {
            if (player.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * to add player
     * @param player entered player
     */
    public void addPlayer(Player_ServerSide player){
        players.add(player);
    }

    /**
     * getter for sharedInbox
     * @return sharedInbox
     */
    public ArrayBlockingQueue<Message> getSharedInbox() {
        return sharedInbox;
    }
}
