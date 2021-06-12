package server_side.model;

import server_side.connector.MsgSeparator;
import server_side.manager.Logic;
import utils.Config;
import utils.Message;
import utils.Role_Group;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {
    private final String name;
    private ServerSocket welcomeSocket;
    private List<Player_ServerSide> players;
    private List<Message> beforeStartChats;
    private List<Message> publicChats;
    private List<Message> mafiaChats;
    private List<Player_ServerSide> gameWatchers; // dead players , which want to see the rest of game
    private Config gameConfig;
    private ArrayBlockingQueue<Message> sharedInbox;
    private Map<Role_Group , Player_ServerSide> roleToPlayer;
    private MsgSeparator msgSeparator;
    private Logic logic;
    private List<Message> events;
    private List<Player_ServerSide> outOfGame;

    /**
     * constructor
     *
     * @param port is the port of server
     */
    public Server(int port)throws IOException{
        this.name = "God_" + port;
        welcomeSocket = new ServerSocket(port);
        players = new ArrayList<>();
        gameWatchers = new ArrayList<>();
        sharedInbox = new ArrayBlockingQueue<>(200);
        roleToPlayer = new HashMap<>();
        beforeStartChats = new ArrayList<>();
        publicChats = new ArrayList<>();
        mafiaChats = new ArrayList<>();
        Logger.log("server created." , LogLevels.INFO , getClass().getName());
        msgSeparator = new MsgSeparator(this,logic);
        events = new ArrayList<>();
        outOfGame = new ArrayList<>();
    }

    /**
     * to set roles in a random way
     */
    public void setRoles(){
        Random random = new Random();

        // plan 1 for 10 players
        ArrayList<Role_Group> roles1 = new ArrayList<>();
        roles1.add(Role_Group.GODFATHER);
        roles1.add(Role_Group.DOCTOR_LECTER);
        roles1.add(Role_Group.NORMAL_MAFIA);
        roles1.add(Role_Group.DOCTOR);
        roles1.add(Role_Group.DETECTIVE);
        roles1.add(Role_Group.SNIPER); // just one bullet
        roles1.add(Role_Group.CITIZEN);
        roles1.add(Role_Group.MAYOR);
        roles1.add(Role_Group.PSYCHOLOGIST);
        roles1.add(Role_Group.DIE_HARD);

        // plan 2 for 7 players
        ArrayList<Role_Group> roles2 = new ArrayList<>();
        roles2.add(Role_Group.GODFATHER);
        roles2.add(Role_Group.DOCTOR_LECTER);
        roles2.add(Role_Group.DETECTIVE);
        roles2.add(Role_Group.SNIPER); // just one bullet
        roles2.add(Role_Group.CITIZEN);
        roles2.add(Role_Group.DOCTOR);
        roles2.add(Role_Group.DIE_HARD);

        // plan 3 for 5 players
        ArrayList<Role_Group> roles3 = new ArrayList<>();
        roles3.add(Role_Group.GODFATHER);
        roles3.add(Role_Group.CITIZEN);
        roles3.add(Role_Group.DOCTOR);
        roles3.add(Role_Group.DETECTIVE);
        roles3.add(Role_Group.MAYOR);

        int num = gameConfig.getPlayerNumbers();
        ArrayList<Role_Group> roles = num == 10 ? roles1 : num == 7 ? roles2 : roles3;

        for (int i = 0 ; i < num ; i++)
        {
            Role_Group toUse = roles.get(random.nextInt(roles.size()));
            players.get(i).setRole(toUse);
            roleToPlayer.put(toUse , players.get(i));
            if (toUse == Role_Group.GODFATHER || toUse == Role_Group.DOCTOR_LECTER || toUse == Role_Group.NORMAL_MAFIA)
                players.get(i).setGroup(Role_Group.MAFIA_GROUP);
            else {
                players.get(i).setGroup(Role_Group.CITIZEN_GROUP);
            }
            roles.remove(toUse);
            Logger.log(players.get(i).getName() + " role: " + toUse + " group: " + players.get(i).getGroup() , LogLevels.INFO , getClass().getName());
            System.out.println("done: "+ players.get(i).getName() + " role: " + toUse + " group: " + players.get(i).getGroup());
        }
    }

    /**
     * to send message to a list
     * @param list to send message
     * @param msg the message to send
     */
    public void notifyList(List<Player_ServerSide> list , Message msg){
        for (Player_ServerSide player:list)
        {
            player.getMsgSender().sendMsg(msg);
            Logger.log("sent to " + player.getName() + " :" + msg.getContent() , LogLevels.INFO , getClass().getName());
            System.out.println("sent to " + player.getName() + " :" + msg.getContent());
        }
    }

    /**
     * to notify a single player
     * @param player the player to send message
     * @param msg the message to send
     */
    public void notifyMember(Player_ServerSide player , Message msg){
        player.getMsgSender().sendMsg(msg);
    }

    public void msgSeparator(){

    }




    /**
     * getter
     * @return alive players of game
     */
    public List<Player_ServerSide> getPlayers() {
        return players;
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

    /**
     * getter
     * @return HashMap of roles mapped to players
     */
    public Map<Role_Group, Player_ServerSide> getRoleToPlayer() {
        return roleToPlayer;
    }

    /**
     * getter
     * @return beforeStart chats
     */
    public List<Message> getBeforeStartChats() {
        return beforeStartChats;
    }

    /**
     * getter
     * @return mafia chats
     */
    public List<Message> getMafiaChats() {
        return mafiaChats;
    }

    /**
     * getter
     * @return public chats
     */
    public List<Message> getPublicChats() {
        return publicChats;
    }

    /**
     * to get player by name
     * @param name of player
     * @return player
     */
    public Player_ServerSide getPlayerByName(String name){
        for (Player_ServerSide player:players)
        {
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }

    /**
     * to set logic
     * @param logic created logic to add
     */
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    /**
     * to add a valid event(validity should be checked before this)
     * @param msg the new event
     */
    public void addEvent(Message msg){
        events.add(msg);
    }

    /**
     * to handle events after each state in gameLoop
     *      - the entered events are valid , no need to check
     */
    public void handleEvents(){

    }

    /**
     * to access players which are out of game
     * @return outOfGame players
     */
    public List<Player_ServerSide> getOutOfGame() {
        return outOfGame;
    }

    /**
     * getter
     * @return gameWatchers
     */
    public List<Player_ServerSide> getGameWatchers() {
        return gameWatchers;
    }

    /**
     * getter
     * @return msgSeparator of this server
     */
    public MsgSeparator getMsgSeparator() {
        return msgSeparator;
    }
}
