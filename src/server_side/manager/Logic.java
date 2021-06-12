package server_side.manager;

import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.Message;
import utils.MessageTypes;
import utils.Role_Group;

import java.io.Serializable;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

/**
 * a class to monitor logical rules of game
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Logic implements Serializable {
    private Map<MessageTypes , Integer> usedLimitedCommands;
    private Server server;
    private GameState gameState;

    /**
     * constructor
     * @param server of game
     * @param gameState state of this round
     */
    public Logic(Server server , GameState gameState){
        usedLimitedCommands = new HashMap<>();
        this.server = server;
        this.gameState = gameState;
    }

    /**
     * checking validity of kill order
     * @param msg entered by player
     * @return true if valid , false if not
     */
    public boolean isKillValid(Message msg){
        boolean isFound = isTargetFound(msg);
        if (!isFound)
            return false;
        if (msg.getMsgType() == MessageTypes.ACTIONS_GODFATHER_ORDERED_KILL){
            if (!msg.getTarget().equals(msg.getSender()) &&
                    server.getPlayerByName(msg.getTarget()).getGroup() != Role_Group.MAFIA_GROUP)  // godfather ordered kill - can't kill himself or any of mafias
                return true;
            else return false;
        }
        else // sniper ordered kill
        {
            if (usedLimitedCommands.containsKey(MessageTypes.ACTIONS_SNIPER_ORDERED_KILL)){
                return false;
            }
            else if (!usedLimitedCommands.containsKey(MessageTypes.ACTIONS_SNIPER_ORDERED_KILL) && !msg.getSender().equals(msg.getTarget()))
            {
                usedLimitedCommands.put(MessageTypes.ACTIONS_SNIPER_ORDERED_KILL , 1);
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * to know if is valid or not
     * @param msg entered by player
     * @return true if valid , false if not
     */
    public boolean isSaveValid(Message msg){
        boolean isFound = isTargetFound(msg);
        if (!isFound)
            return false;
        if (msg.getTarget().equals(msg.getSender()) && usedLimitedCommands.containsKey(msg.getMsgType()))
            return false;
        if (msg.getTarget().equals(msg.getSender())) {
            usedLimitedCommands.put(msg.getMsgType(), 1); // to know that this command has been used for saving himself
            return true;
        }
        // others are true
        return true;
    }

    /**
     * checking validity - psychologist can't silence himself
     * @param msg entered by player
     * @return false if target is himself , else true
     */
    public boolean isSilenceValid(Message msg){
        return !msg.getTarget().equals(msg.getSender());
    }

    /**
     * to do inquiry
     * @param msg entered msg
     * @return a string in special format: accepted(or refused),some data....
     */
    public String inquiry(Message msg){
        if (msg.getMsgType() == MessageTypes.ACTIONS_DETECTIVE_ORDERED_INQUIRY)
        {
            boolean isFound = isTargetFound(msg);
            if (!isFound)
                return "refused,Player not found!";
            Player_ServerSide target = server.getPlayerByName(msg.getTarget());
            if (target.getRole() == Role_Group.GODFATHER || target.getGroup() == Role_Group.CITIZEN_GROUP){
                return "accepted,No ... This player isn't mafia.";
            }
            else return "accepted,Yes! ... This player is mafia!";
        }
        else { // die hard inquiry
            int num = 0;
            try {
                num = usedLimitedCommands.get(MessageTypes.ACTIONS_DIEHARD_ORDERED_INQUIRY);
            }catch (InputMismatchException e)
            {
                num = 0;
            }
            if (num == 0 || num == 1){
                usedLimitedCommands.replace(MessageTypes.ACTIONS_DIEHARD_ORDERED_INQUIRY , num == 0 ? 1 : 2);
                if (server.getOutOfGame().size() == 0)
                    return "refused,No players out of game yet!";
                String str = "accepted,";
                for (Player_ServerSide player: server.getOutOfGame())
                {
                    str += "Name : " + player.getName() + "\tRole: " + player.getRole() + "\n";
                }
                return str;
            }
            else {
                return "refused,You have used this command for 2 times. No more is allowed!";
            }
        }
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

    /**
     * to search between players
     * @param msg the entered message
     * @return true if found , false if not
     */
    private boolean isTargetFound(Message msg)
    {
        for (Player_ServerSide player: server.getPlayers())
        {
            if (player.getName().equals(msg.getTarget()) && player.getGroup() == Role_Group.CITIZEN_GROUP){ // null exception???
                return true;
            }
        }
        return false;
    }
}
