package server_side.connector;

import server_side.manager.Logic;
import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.ChatroomType;
import utils.Message;
import utils.MessageTypes;
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
                Player_ServerSide toAct = server.getPlayerByName(msg.getTarget());
                Player_ServerSide actor ; // continue from here!!!!!!
                if (msg.getMsgType() == MessageTypes.ACTIONS_GODFATHER_ORDERED_KILL && logic.godFatherOrderedKill(msg))
                {

                    if (toAct != null)
                    {
                        server.getRoleToPlayer().remove(toAct.getRole());
                        server.getPlayers().remove(toAct);
                        Logger.log("Player with name " + msg.getTarget() + " removed." , LogLevels.INFO , MsgSeparator.class.getName());
                        System.out.println("Player with name " + msg.getTarget() + " removed.");
                    }
                    else {
                        toAct.getMsgSender().sendMsg(new Message(server.getName(), "Sorry , no players with that name!" ,ChatroomType.TO_CLIENT,MessageTypes.COMMAND_REFUSED ,null ));
                        Logger.log("Error in removing player. player with name " + msg.getTarget() + " doesn't exist" , LogLevels.ERROR , MsgSeparator.class.getName());
                        System.out.println("Error in removing player. player with name " + msg.getTarget() + " doesn't exist");
                    }

                }
            }catch (InterruptedException e)
            {
                Logger.log("Interrupted in taking message from sharedInbox." , LogLevels.WARN , MsgSeparator.class.getName());
            }

        }
    }
}
