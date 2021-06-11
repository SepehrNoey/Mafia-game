package server_side.manager;

import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.Message;
import utils.MessageTypes;
import utils.Role_Group;

import java.io.Serializable;
import java.util.ArrayList;

public class Logic implements Serializable {
    private ArrayList<MessageTypes> usedCommands;
    private Server server;
    private GameState gameState;

    public Logic(Server server , GameState gameState){
        usedCommands = new ArrayList<>();
        this.server = server;
        this.gameState = gameState;
    }

    /**
     * checking validity of kill order
     * @param msg entered by player
     * @return true if valid , false if not
     */
    public boolean godFatherOrderedKill(Message msg){
        for (Player_ServerSide player: server.getPlayers())
        {
            if (player.getName().equals(msg.getTarget()) && player.getGroup() == Role_Group.CITIZEN_GROUP && server){
                usedCommands.add(MessageTypes.ACTIONS_GODFATHER_ORDERED_KILL);
                return true;
            }
        }
        return false;
    }


    /**
     * to check if the game is finished or not
     * @return true if finished , false if not
     */
    public boolean isFinished()
    {
        int mafNum = 0;
        int citNum = 0;
        for (Player_ServerSide player: server.getPlayers())
        {
            if (player.getGroup() == Role_Group.MAFIA_GROUP)
                mafNum++;
            else
                citNum++;
        }
        return mafNum >= citNum || mafNum == 0;
    }


}
