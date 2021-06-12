package server_side.manager;

import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.*;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.util.Map;

public class GameLoop {
    private Server server;
    private GameState gameState;
    private Logic logic;

    /**
     * constructor
     * @param server server of this game
     * @param gameState the game state where to begin game from - it can be a new created or a loaded one
     */
    public GameLoop(Server server  ,Logic logic, GameState gameState){
        this.server = server;
        this.logic = logic;
        this.gameState = gameState;
    }

    public void playLoop() {
        for (Player_ServerSide player: server.getPlayers())
        {
            server.notifyMember(player , new Message(server.getName(),player.getGroup() + " " + player.getRole() , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_SET_ROLE , null));
        }
        server.notifyList(server.getPlayers() , new Message(server.getName(),"First night greeting." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_FIRST_NIGHT_GREETING , null));
        while (!logic.isFinished()){
            if (!(gameState.getState() == StateEnum.FIRST_NIGHT)){
                night(); // should change game state
            }
            if (!logic.isFinished())
                break;
            day();
            voting();
        }
    }


    public void night(){
        // msgSeparator is working alongside this method
        int time = gameState.getConfig().getEachRoleNightActingTime() * 1000;
        Map<Role_Group , Player_ServerSide> roleToPlayer = server.getRoleToPlayer();
        if (roleToPlayer.containsKey(Role_Group.NORMAL_MAFIA))
            server.notifyMember(roleToPlayer.get(Role_Group.NORMAL_MAFIA) , new Message(server.getName(), "order to do night act." ,ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT  , null));
        if (roleToPlayer.containsKey(Role_Group.DOCTOR_LECTER))
            server.notifyMember(roleToPlayer.get(Role_Group.DOCTOR_LECTER) , new Message(server.getName(),"order to do night act." ,ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null ));
        if (roleToPlayer.containsKey(Role_Group.GODFATHER))
            server.notifyMember(roleToPlayer.get(Role_Group.GODFATHER) ,new Message(server.getName(),"order to do night act." ,ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT  , null));
        System.out.println("Waiting for mafias to do their act at night...");
        sleep(time , "interrupted while sleeping - in night method - after mafia acting.");

        if (roleToPlayer.containsKey(Role_Group.DOCTOR_LECTER))
            server.notifyMember(roleToPlayer.get(Role_Group.DOCTOR_LECTER) , new Message(server.getName() , "order to lecter to save someone." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null));
        System.out.println("Waiting for lecter ...");
        sleep(time , "interrupted while sleeping - in night method - after lecter acting.");






    }

    public void day(){

    }

    public void voting(){

    }

    /**
     * sleep method
     * @param time to sleep
     * @param msg error details
     */
    private void sleep(int time , String msg){
        try {
            Thread.sleep(time);
        }catch (InterruptedException e)
        {
            Logger.log(msg, LogLevels.ERROR , getClass().getName());
        }
    }

}
