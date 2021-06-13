package server_side.connector;

import server_side.manager.Logic;
import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.*;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * this class separates the incoming commands or chats from clients
 *          as the package name says , it's a connector class between manager classes and sendGet classes
 */
public class MsgSeparator implements Runnable {
    private Thread thread;
    private Server server;
    private Logic logic;

    public MsgSeparator(Server server , Logic logic){
        thread = new Thread(this);
        this.server = server;
        this.logic = logic;
    }

    /**
     * getter
     * @return thread of message separator
     */
    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        Message msg = null;
        ArrayBlockingQueue<Message> sharedInbox = server.getSharedInbox();
        while (!Thread.currentThread().isInterrupted()){
            try {
                msg = sharedInbox.take();
                Player_ServerSide actor = server.getPlayerByName(server.getPlayers(), msg.getSender());
                boolean state = false;
                if (msg.getMsgType() == MessageTypes.ACTIONS_GODFATHER_ORDERED_KILL || msg.getMsgType() == MessageTypes.ACTIONS_SNIPER_ORDERED_KILL)
                {
                    state = logic.isKillValid(msg);
                    if (state)
                    {
                        server.addEvent(msg);
                        actor.getMsgSender().sendMsg(new Message(server.getName(),"If doctor doesn't save this player , then will be killed!", ChatroomType.TO_CLIENT , MessageTypes.COMMAND_ACCEPTED , null));
                        Logger.log("Event killing " + msg.getTarget() + " by " + msg.getSender() + " added." , LogLevels.INFO , MsgSeparator.class.getName());
                        System.out.println("Event killing " + msg.getTarget() + " by " + msg.getSender() + " added.");
                    }
                    else {
                        actor.getMsgSender().sendMsg(new Message(server.getName(), "Command or target not supported!" ,ChatroomType.TO_CLIENT,MessageTypes.COMMAND_REFUSED ,null ));
                        Logger.log("Command or target not supported! - entered by " + msg.getSender() , LogLevels.INFO , MsgSeparator.class.getName());
                        System.out.println("Command or target not supported! - entered by " + msg.getSender());
                    }
                }
                else if (msg.getMsgType() == MessageTypes.ACTIONS_DETECTIVE_ORDERED_INQUIRY || msg.getMsgType() == MessageTypes.ACTIONS_DIEHARD_ORDERED_INQUIRY)
                {
                    String[] res = logic.inquiry(msg).split(",");
                    actor.getMsgSender().sendMsg(new Message(server.getName(), res[1] , ChatroomType.TO_CLIENT ,
                            res[0].equals("accepted") ? MessageTypes.COMMAND_ACCEPTED : MessageTypes.COMMAND_REFUSED , null));
                    Logger.log(msg.getSender() + " did inquiry." , LogLevels.INFO ,getClass().getName());
                    System.out.println(msg.getSender() + " did inquiry.");
                }
                else if ((msg.getMsgType() == MessageTypes.ACTIONS_DOCTOR_ORDERED_SAVE || msg.getMsgType() == MessageTypes.ACTIONS_LECTER_ORDERED_SAVE) && logic.isSaveValid(msg))
                {
                    state = logic.isSaveValid(msg);
                    if (state)
                    {
                        server.addEvent(msg);
                        actor.getMsgSender().sendMsg(new Message(server.getName(), msg.getTarget() + " will be saved." , ChatroomType.TO_CLIENT , MessageTypes.COMMAND_ACCEPTED , null));
                        Logger.log("Event: " + msg.getSender() + " wants to save " + msg.getTarget() , LogLevels.INFO , getClass().getName());
                        System.out.println("Event: " + msg.getSender() + " wants to save " + msg.getTarget());
                    }
                    else {
                        if (msg.getTarget().equals(msg.getSender()))
                        {
                            actor.getMsgSender().sendMsg(new Message(server.getName(), "You can't save yourself anymore!" , ChatroomType.TO_CLIENT , MessageTypes.COMMAND_REFUSED , null));
                            Logger.log(msg.getSender() + " tries to save himself more than limit." , LogLevels.INFO  , getClass().getName());
                            System.out.println("Trying to save more than limit.");
                        }
                        else {
                            actor.getMsgSender().sendMsg(new Message(server.getName(), "Command or target not supported!" ,ChatroomType.TO_CLIENT,MessageTypes.COMMAND_REFUSED ,null ));
                            Logger.log("Command or target not supported! - entered by " + msg.getSender() , LogLevels.INFO , MsgSeparator.class.getName());
                            System.out.println("Command or target not supported! - entered by " + msg.getSender());
                        }
                    }
                }
                else if (msg.getMsgType() == MessageTypes.ACTIONS_PSYCHOLOGIST_ORDERED_SILENCE)
                {
                    state = logic.isSilenceValid(msg);
                    if (state)
                    {
                        server.addEvent(msg);
                        actor.getMsgSender().sendMsg(new Message(server.getName(), msg.getTarget() + " will be silenced at day." , ChatroomType.TO_CLIENT , MessageTypes.COMMAND_ACCEPTED , null));
                        Logger.log("Event: " + msg.getSender() + " wants to silence " + msg.getTarget() , LogLevels.INFO , getClass().getName());
                        System.out.println("Event: " + msg.getSender() + " wants to silence " + msg.getTarget());
                    }
                    else {
                        actor.getMsgSender().sendMsg(new Message(server.getName(), "You can't silence yourself!", ChatroomType.TO_CLIENT , MessageTypes.COMMAND_REFUSED , null));
                        Logger.log(msg.getSender() + " tries to silence himself. Refused" , LogLevels.INFO , getClass().getName());
                        System.out.println(msg.getSender() + " tries to silence himself. Refused");
                    }
                }
                else if (msg.getMsgType() == MessageTypes.ACTIONS_PLAYER_VOTED) // just in voting time
                {
                    String[] str = logic.vote(msg).split(",");
                    if (str[0].equals("accepted"))
                    {
                        server.addEvent(msg);
                        actor.getMsgSender().sendMsg(new Message(server.getName(), str[1] , ChatroomType.TO_CLIENT , MessageTypes.COMMAND_ACCEPTED , null));
                        server.notifyList(server.getPlayers() , new Message(msg.getSender(), "I voted " + msg.getTarget() + "." , ChatroomType.PUBLIC_CHATROOM , MessageTypes.ACTIONS_PLAYER_VOTED , null));
                        server.notifyList(server.getGameWatchers() , new Message(msg.getSender(), "I voted " + msg.getTarget() + "." , ChatroomType.PUBLIC_CHATROOM , MessageTypes.ACTIONS_PLAYER_VOTED , null));
                        System.out.println("Sent : " + actor.getName() + " voted " + msg.getTarget() + " to all players and watchers.");
                        Logger.log("Sent : " + actor.getName() + " voted " + msg.getTarget() + " to all players and watchers." , LogLevels.INFO , getClass().getName());
                    }
                    else {
                        actor.getMsgSender().sendMsg(new Message(server.getName() , str[1] , ChatroomType.TO_CLIENT , MessageTypes.COMMAND_REFUSED , null));
                        System.out.println("Vote of " + msg.getSender() + " refused.because of : " + str[1]);
                        Logger.log("Vote of " + msg.getSender() + " refused.because of : " + str[1] , LogLevels.INFO , getClass().getName());
                    }
                }
                else if(msg.getMsgType() == MessageTypes.ANSWER_TO_WATCH)
                {
                    if (msg.getContent().equalsIgnoreCase("yes"))
                        server.getGameWatchers().add(server.getPlayerByName(server.getOutOfGame(), msg.getSender()));
                }
                else if (msg.getMsgType() == MessageTypes.ANSWER_TO_CANCEL)
                {
                    server.getCancelMsg().transfer(msg);
                }
                else { // normal chat
                    if (actor.getRole() == null){  // a trick to know if the game is started or not ( role == null means the game hasn't started yet)
                        server.getBeforeStartChats().add(msg);
                        server.notifyList(server.getPlayers() , msg);
                        Logger.log("Sent: '" + msg.getContent() + "' to all players - before start chats" , LogLevels.INFO , getClass().getName());
                        System.out.println("Sent: '" + msg.getContent() + "' to all players - before start chats");
                    }
                    else if((logic.getGameState().getState() == StateEnum.FIRST_NIGHT && actor.getGroup() == Role_Group.MAFIA_GROUP) || (logic.getGameState().getState() == StateEnum.NIGHT && actor.getGroup() == Role_Group.MAFIA_GROUP))
                    {
                        msg.setChatroomType(ChatroomType.MAFIA_CHATROOM);
                        server.getMafiaChats().add(msg);
                        server.notifyList(server.getGameWatchers() , msg); // game watchers
                        System.out.println("Sent: '" + msg.getContent() + "' to game watchers.");
                        Logger.log("Sent: '" + msg.getContent() + "' to game watchers." , LogLevels.INFO , getClass().getName());
                        for (Player_ServerSide player:server.getPlayers())
                        {
                            if (player.getGroup() == Role_Group.MAFIA_GROUP) {
                                server.notifyMember(player, msg);
                                Logger.log("Sent: '" + msg.getContent() + "' to " + player.getName() , LogLevels.INFO , getClass().getName());
                                System.out.println("Sent: '" + msg.getContent() + "' to " + player.getName());
                            }
                        }
                    }
                    else if (logic.getGameState().getState() == StateEnum.DAY)
                    {
                        msg.setChatroomType(ChatroomType.PUBLIC_CHATROOM);
                        server.getPublicChats().add(msg);
                        server.notifyList(server.getPlayers() , msg);
                        server.notifyList(server.getGameWatchers() , msg);
                        System.out.println("Sent: '" + msg.getContent() + "' to all players.");
                        Logger.log("Sent: '" + msg.getContent() + "' to all players." , LogLevels.INFO , getClass().getName());
                        System.out.println("Sent: '" + msg.getContent() + "' to game watchers.");
                        Logger.log("Sent: '" + msg.getContent() + "' to game watchers." , LogLevels.INFO , getClass().getName());
                    }
                    else { // may have bug here
                        server.getPlayerByName(server.getPlayers() , msg.getSender()).getMsgSender().sendMsg(new Message(server.getName(),
                                "You are not allowed to chat now!" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                    }
                }

            }catch (InterruptedException e)
            {
                Logger.log("Interrupted in taking message from sharedInbox." , LogLevels.WARN , MsgSeparator.class.getName());
            }

        }
    }
}
