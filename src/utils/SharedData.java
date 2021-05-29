package utils;

import client_side.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * belongs to 'mafia game'
 * a class to store the most needed data of objects to access in any other class
 *          and probably just this class will be saved in a file
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class SharedData implements Serializable {
    private static SharedData sharedData;
    private List<Player> players;
    private List<Message> chats;
    // and turn...

    /**
     * constructor
     *      is used just at the start of the game
     */
    private SharedData(){
        players = new ArrayList<>();
        chats = new ArrayList<>();
    }

    /**
     * singleton pattern for shared data
     * @return existing sharedData , if doesn't exist , creates one
     */
    public static SharedData getSharedData(){
        if (sharedData == null)
            return new SharedData();
        else
            return sharedData;
    }




    /**
     * to add player when player is created.
     * @param player is the entered player
     */
    public void addPlayer(Player player){
        sharedData.players.add(player);
    }

    /**
     * to remove player when is kicked out of the game or killed
     * @param player to remove
     */
    public void removePlayer(Player player){
        sharedData.players.remove(player);
    }

    /**
     * to add a chat to the list of chats
     * @param chat is the entered chat
     */
    public void addChat(Message chat){
        sharedData.chats.add(chat);
    }

    /**
     * to access players in other classes
     * @return players
     */
    public List<Player> getPlayers() {
        return players;
    }
}
