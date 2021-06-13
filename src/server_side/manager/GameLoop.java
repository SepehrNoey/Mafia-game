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
        firstNight();
        while (!logic.isFinished()){
            if (!(gameState.getState() == StateEnum.FIRST_NIGHT)){
                night(); // should change game state
                server.handleEvents();
            }
            gameState.setState(StateEnum.DAY);
            if (!logic.isFinished())
                break;
            day();
            gameState.setState(StateEnum.VOTING_TIME);
            voting();
            gameState.setState(StateEnum.NIGHT);
        }
        String result = logic.getEndingResult();
        server.notifyList(server.getPlayers() , new Message(server.getName(), "\nHey Guys!\nGame ended. The result is as the following:\n" + result , ChatroomType.TO_CLIENT , MessageTypes.END_OF_GAME , null));
        server.closeAll();
        // the end
    }

    public void firstNight(){
        server.notifyList(server.getPlayers() , new Message(server.getName(),"First night greeting." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_FIRST_NIGHT_GREETING , null));
        sleep(2000 , "interrupted in sleeping after first night.");
        if (server.getRoleToPlayer().containsKey(Role_Group.MAYOR))
        {
            Player_ServerSide mayor = server.getRoleToPlayer().get(Role_Group.MAYOR);
            mayor.getMsgSender().sendMsg(new Message(server.getName(), server.getRoleToPlayer().containsKey(Role_Group.DOCTOR) ?
                    server.getRoleToPlayer().get(Role_Group.DOCTOR).getName() + "_as_Doctor," : "nobody," , ChatroomType.TO_CLIENT,
                    MessageTypes.INFO , null));
        }
        if (server.getRoleToPlayer().containsKey(Role_Group.DOCTOR))
        {
            Player_ServerSide doctor = server.getRoleToPlayer().get(Role_Group.DOCTOR);
            doctor.getMsgSender().sendMsg(new Message(server.getName(), server.getRoleToPlayer().containsKey(Role_Group.DOCTOR) ?
                    server.getRoleToPlayer().get(Role_Group.MAYOR).getName() + "_as_Mayor," : "nobody,",ChatroomType.TO_CLIENT,
                    MessageTypes.INFO,
                    null));
        }
        Player_ServerSide godfather = server.getRoleToPlayer().get(Role_Group.GODFATHER);
        Player_ServerSide lecter = server.getRoleToPlayer().get(Role_Group.DOCTOR_LECTER);
        Player_ServerSide normalMaf = server.getRoleToPlayer().get(Role_Group.NORMAL_MAFIA);
        godfather.getMsgSender().sendMsg(new Message(server.getName(), (lecter != null && normalMaf != null) ? lecter.getName() + "_as_Doctor Lecter," + normalMaf + "_as_Normal Mafia," :
                lecter != null ? lecter.getName() + "_as_Doctor Lecter," : normalMaf != null ? normalMaf.getName() + "_as_Normal Mafia," : "nobody," ,
                ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
        if (lecter != null)
            lecter.getMsgSender().sendMsg(new Message(server.getName(), normalMaf!= null ? godfather.getName() + "_as_Godfather," + normalMaf.getName() + "_as_Normal Mafia," :
                    godfather.getName() + "_as_Godfather," , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
        // we have normal mafia just in first config mode
        if (gameState.getConfig().getPlayerNumbers() == 10)
            normalMaf.getMsgSender().sendMsg(new Message(server.getName(), godfather.getName() + "_as_Godfathere," + lecter.getName() + "_as_Doctor Lecter," , ChatroomType.TO_CLIENT ,
                    MessageTypes.INFO , null));

        sleep(3000 , "interrupted in end of first night.");
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
        if (gameState.getConfig().getPlayerNumbers() != 5) { // in the third plan , we don't have doctor lecter
            System.out.println("Waiting for lecter ...");
            sleep(time, "interrupted while sleeping - in night method - after lecter acting.");
        }

        if(roleToPlayer.containsKey(Role_Group.DOCTOR))
            server.notifyMember(roleToPlayer.get(Role_Group.DOCTOR) , new Message(server.getName() , "order to doctor to save someone." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null));
        System.out.println("Waiting for doctor...");
        sleep(time , "interrupted in sleeping - in night method - after doctor acting.");

        if (roleToPlayer.containsKey(Role_Group.DETECTIVE))
            server.notifyMember(roleToPlayer.get(Role_Group.DETECTIVE) , new Message(server.getName(), "order to detective to do inquiry." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null));
        System.out.println("Waiting for detective...");
        sleep(time , "interrupted in sleeping - in night method - after detective acting.");

        if (roleToPlayer.containsKey(Role_Group.SNIPER))
            server.notifyMember(roleToPlayer.get(Role_Group.SNIPER) , new Message(server.getName(), "order to sniper to snipe if can." ,ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null));
        if (gameState.getConfig().getPlayerNumbers() != 5)
        {
            System.out.println("Waiting for sniper...");
            sleep(time , "interrupted in sleeping - in night method - after sniper acting.");
        }

        if (roleToPlayer.containsKey(Role_Group.PSYCHOLOGIST))
            server.notifyMember(roleToPlayer.get(Role_Group.PSYCHOLOGIST) , new Message(server.getName(), "order to do psychologist act." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null));
        if (gameState.getConfig().getPlayerNumbers() == 10) {
            System.out.println("Waiting for psychologist...");
            sleep(time,"interrupted in sleeping - in night method - after psychologist acting.");
        }

        if (roleToPlayer.containsKey(Role_Group.DIE_HARD))
            server.notifyMember(roleToPlayer.get(Role_Group.DIE_HARD) , new Message(server.getName(), "order to die hard to act." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT , null));
        if (gameState.getConfig().getPlayerNumbers() != 5)
        {
            System.out.println("Waiting for die hard...");
            sleep(time,"interrupted in sleeping - in night method - after die hard acting.");
        }

    }

    public void day(){
        server.notifyList(server.getPlayers(), new Message(server.getName() ,"order for day" , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_DAY_PUBLIC_CHAT , null));
        server.notifyList(server.getGameWatchers(), new Message(server.getName() ,"order for day" , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_DAY_PUBLIC_CHAT , null));

        sleep(gameState.getConfig().getDayTime() * 60 * 1000 , "interrupted in day sleep.");

        server.notifyList(server.getPlayers() , new Message(server.getName(),  "Day ended! going for voting..." , ChatroomType.TO_CLIENT , MessageTypes.CLOSE_CHATROOM , null));
        server.notifyList(server.getGameWatchers() , new Message(server.getName(),  "Day ended! going for voting..." , ChatroomType.TO_CLIENT , MessageTypes.CLOSE_CHATROOM , null));

        sleep(1500 , "interrupted in end of day");
    }

    public void voting(){
        String names = "";
        for (Player_ServerSide player: server.getPlayers())
        {
            names += player.getName() + ",";
        }
        server.notifyList(server.getPlayers() , new Message(server.getName(), names , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_VOTE , null));

        sleep(gameState.getConfig().getVotingTime() * 1000 , "interrupted in voting sleep.");

        server.notifyList(server.getPlayers() , new Message(server.getName(), "end voting" , ChatroomType.TO_CLIENT , MessageTypes.CLOSE_CHATROOM , null));

        String[] result = logic.countVotes(server.getEvents()).split("_");

        if (!result[0].equalsIgnoreCase("nobody") && server.getRoleToPlayer().containsKey(Role_Group.MAYOR))
        {
            server.notifyList(server.getPlayers() , new Message(server.getName(), "Now , asking from mayor if wants to cancel the voting..." ,
                    ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
            server.notifyList(server.getGameWatchers() , new Message(server.getName(), "Now , asking from mayor if wants to cancel the voting..." ,
                    ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
            server.getRoleToPlayer().get(Role_Group.MAYOR).getMsgSender().sendMsg(new Message(server.getName(), "According to the result of voting , " + result[1] + " will be kicked out. Do you want to cancel voting?\n1) yes\n2) no" , ChatroomType.TO_CLIENT , MessageTypes.QUESTION_TO_CANCEL,null));
            try {
                Message cancelOrNot = server.getCancelMsg().take();
                if (cancelOrNot.getContent().equals("yes"))
                {
                    server.notifyList(server.getPlayers() , new Message(server.getName(), "Mayor canceled the voting this round!" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                    server.notifyList(server.getGameWatchers() , new Message(server.getName(), "Mayor canceled the voting this round!" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                }
                else {
                    server.notifyList(server.getPlayers() , new Message(server.getName() , "Mayor didn't cancel the voting." , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                    server.notifyList(server.getGameWatchers() , new Message(server.getName() , "Mayor didn't cancel the voting." , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));

                    server.kickPlayer(result[0] , "Other players voted you to get out of game, Do you want to watch the rest of game?\n1) yes\n2) no");
                    server.notifyList(server.getPlayers() , new Message(server.getName() , result[1] + " with "  + result[0] + " votes kicked out!" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                    server.notifyList(server.getGameWatchers() , new Message(server.getName() , result[1] + " with "  + result[0] + " votes kicked out!" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                }

            }catch (InterruptedException e){
                System.out.println("Interrupted in taking cancel voting message.");
                Logger.log("Interrupted in taking cancel voting message." , LogLevels.ERROR , getClass().getName());
            }
        }
        else {
            server.notifyList(server.getPlayers() , new Message(server.getName(), "Nobody gets out of game because of same number of votes ! Going for night..." , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
            server.notifyList(server.getGameWatchers() , new Message(server.getName(), "Nobody gets out of game because of same number of votes ! Going for night..." , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
        }
        sleep(3000 , "interrupted in end of voting sleep.");
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
