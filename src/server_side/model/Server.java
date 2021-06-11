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
    private ArrayList<Message> beforeStartChats;
    private ArrayList<Message> publicChats;
    private ArrayList<Message> mafiaChats;
    private List<Player_ServerSide> gameWatchers; // dead players , which want to see the rest of game
    private List<Player_ServerSide> observers;
    private Config gameConfig;
    private ArrayBlockingQueue<Message> sharedInbox;
    private Map<Role_Group , Player_ServerSide> roleToPlayer;
    private MsgSeparator msgSeparator;
    private Logic logic;

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
        roleToPlayer = new HashMap<>();
        beforeStartChats = new ArrayList<>();
        publicChats = new ArrayList<>();
        mafiaChats = new ArrayList<>();
        Logger.log("server created." , LogLevels.INFO , getClass().getName());
        this.logic = new Logic(this);
        msgSeparator = new MsgSeparator(this,logic);
        msgSeparator.getThread().start();
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
        roles1.add(Role_Group.SNIPER);
        roles1.add(Role_Group.CITIZEN);
        roles1.add(Role_Group.MAYOR);
        roles1.add(Role_Group.PSYCHOLOGIST);
        roles1.add(Role_Group.DIE_HARD);

        // plan 2 for 7 players
        ArrayList<Role_Group> roles2 = new ArrayList<>();
        roles2.add(Role_Group.GODFATHER);
        roles2.add(Role_Group.DOCTOR_LECTER);
        roles2.add(Role_Group.DETECTIVE);
        roles2.add(Role_Group.SNIPER);
        roles2.add(Role_Group.CITIZEN);
        roles2.add(Role_Group.DOCTOR);
        roles2.add(Role_Group.DIE_HARD);

        // plan 3 for 5 players
        ArrayList<Role_Group> roles3 = new ArrayList<>();
        roles3.add(Role_Group.GODFATHER);
        roles3.add(Role_Group.CITIZEN);
        roles3.add(Role_Group.DOCTOR);
        roles3.add(Role_Group.DETECTIVE);
        roles3.add(Role_Group.SNIPER);

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
    public ArrayList<Message> getBeforeStartChats() {
        return beforeStartChats;
    }

    /**
     * getter
     * @return mafia chats
     */
    public ArrayList<Message> getMafiaChats() {
        return mafiaChats;
    }

    /**
     * getter
     * @return public chats
     */
    public ArrayList<Message> getPublicChats() {
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
}
