package server_side.manager;

import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.Message;
import utils.MessageTypes;
import utils.Role_Group;
import utils.StateEnum;
import utils.logClasses.Logger;

import java.io.Serializable;
import java.util.*;

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
                    server.getPlayerByName(server.getPlayers(), msg.getTarget()).getGroup() != Role_Group.MAFIA_GROUP)  // godfather ordered kill - can't kill himself or any of mafias
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
            Player_ServerSide target = server.getPlayerByName(server.getPlayers(),msg.getTarget());
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
     * to vote
     * @param msg entered by player
     * @return a result string in this format : accepted(or refused), and data about
     */
    public String vote(Message msg){
        boolean isFound = isTargetFound(msg);
        if (!isFound)
            return "refused,Player not found!";
        if (!(gameState.getState() == StateEnum.VOTING_TIME))
            return "refused,Not allowed to vote now!";
        if (msg.getSender().equals(msg.getTarget()))
            return "refused,You can't vote to yourself!";
        return "accepted,Your vote recorded.";
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
            if (player.getName().equals(msg.getTarget())){ // null exception???
                return true;
            }
        }
        return false;
    }

    /**
     * getter
     * @return current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * to handle events of night - voting events are ignored and after this method , the only events in events list , would just be of voting type
     * @param events of night
     * @return a string in a special format to split and use in other classes
     */
    public String handleEvents(List<Message> events)
    {
        Player_ServerSide killedByMaf  = null;
        Player_ServerSide savedByDoc = null;
        Player_ServerSide killedBySniper = null;
        Player_ServerSide savedByLec = null;
        Player_ServerSide silenced = null;
        Iterator<Message> it = events.listIterator();
        while (it.hasNext())
        {
            Message event = it.next();
            if (event.getMsgType() != MessageTypes.ACTIONS_PLAYER_VOTED) // keeping vote events and deleting rest
            {
                if (event.getMsgType() == MessageTypes.ACTIONS_GODFATHER_ORDERED_KILL)
                    killedByMaf = server.getPlayerByName(server.getPlayers(), event.getTarget());
                else if (event.getMsgType() == MessageTypes.ACTIONS_SNIPER_ORDERED_KILL)
                    killedBySniper = server.getPlayerByName(server.getPlayers(),event.getTarget());
                else if (event.getMsgType() == MessageTypes.ACTIONS_DOCTOR_ORDERED_SAVE)
                    savedByDoc = server.getPlayerByName(server.getPlayers(),event.getTarget());
                else if (event.getMsgType() == MessageTypes.ACTIONS_LECTER_ORDERED_SAVE)
                    savedByLec = server.getPlayerByName(server.getPlayers(),event.getTarget());
                else if (event.getMsgType() == MessageTypes.ACTIONS_PSYCHOLOGIST_ORDERED_SILENCE)
                    silenced = server.getPlayerByName(server.getPlayers(),event.getTarget());
                it.remove();
            }
        }

        String result = "";
        if (killedByMaf != null) {
            if (savedByDoc != null)
            {
                if (savedByDoc.getName().equals(killedByMaf.getName()))
                    result += "info-Mafias tried to kill someone but doctor saved him!,";
                else{
                    result += "kill-" + killedByMaf.getName() + ",";
                    result += "info-Mafias killed " + killedByMaf.getName() + "!,";
                }
            }
            else {
                result += "kill-" + killedByMaf.getName() + ",";
                result += "info-Mafias killed " + killedByMaf.getName() + "!,";
            }
        }
        if (killedBySniper != null){
            if (killedBySniper.getGroup() == Role_Group.CITIZEN_GROUP)
            {
                result += "kick-" + server.getRoleToPlayer().get(Role_Group.SNIPER).getName() + ",";
                result += "info-Sniper tried to kill a citizen so sniper kicked out! " + server.getRoleToPlayer().get(Role_Group.SNIPER).getName() + " was sniper.,";
            }
            else {
                if ((savedByDoc != null && savedByDoc.getName().equals(killedBySniper.getName())) || (savedByLec != null && savedByLec.getName().equals(killedBySniper.getName()))) // doctor has saved him
                    result += "info-Sniper tried to kill someone but doctor saved him!,";
                else {
                    result += "kill-" + killedBySniper.getName() + ",";
                    result += "info-Sniper killed " + killedBySniper.getName() + " which was a mafia!,";
                }
            }
        }
        if (silenced != null) {
            result += "silence-" + silenced.getName() + ",";
            result += "Psychologist silenced " + silenced.getName() + " for this round!,";
        }
        return result;
    }

    /**
     * the votes entered are valid - no need to check
     * @param votes list of vote messages
     * @return the result string
     */
    public String countVotes(List<Message> votes){
        HashMap<String , String> voterTarget = new HashMap<>();
        HashMap<String , Integer> targetVotes = new HashMap<>();

        for (Message msg: votes)
        {
            if (voterTarget.containsKey(msg.getSender()))
                voterTarget.replace(voterTarget.get(msg.getSender()) , msg.getTarget());
            else
                voterTarget.put(msg.getSender() , msg.getTarget());
        }

        for (String str : voterTarget.values())
        {
            if (targetVotes.containsKey(str))
                targetVotes.replace(str , targetVotes.get(str) + 1);
            else
                targetVotes.put(str , 1);
        }

        String candidate1name = null;
        int candidate1votes = 0;
        String candidate2name = null;
        int candidate2votes = 0;

        for (Map.Entry<String , Integer> entry:targetVotes.entrySet())
        {
            if (candidate1name == null)
            {
                candidate1name = entry.getKey();
                candidate1votes = entry.getValue();
            }
            else if (entry.getValue() > candidate1votes)
            {
                candidate1name = entry.getKey();
                candidate1votes = entry.getValue();
            }
            else if (entry.getValue() == candidate1votes)
            {
                candidate2name = entry.getKey();
                candidate2votes = entry.getValue();
            }
        }

        if (candidate1votes > candidate2votes)
        {
            return candidate1name + "_" + candidate1votes;
        }
        else {
            return "nobody_same number votes.";
        }
    }

    /**
     * to get the details the winner group and others at the end of game
     * @return the result string
     */
    public String getEndingResult(){
        String winner = "";
        int mafNum = 0;
        int citNum = 0;
        for (Player_ServerSide player: server.getPlayers())
        {
            if (player.getGroup() == Role_Group.MAFIA_GROUP)
                mafNum++;
            else
                citNum++;
        }
        winner = mafNum >= citNum ? "Mafia group is the winner!" : "Citizen group is the winner!";

        String result = "";
        for (Player_ServerSide player: server.getOutOfGame())
        {
            result += player.getName() + " was " + player.getRole() + "\n";
        }
        for (Player_ServerSide player: server.getPlayers())
        {
            result += player.getName() + " was " + player.getRole() + "\n";
        }
        return winner + "\n\n" + result;
    }

    /**
     * setter
     * @param server created server
     */
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * setter
     * @param gameState created gameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
