package client_side.sendGet;

import client_side.model.Player;
import utils.ChatroomType;
import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.util.concurrent.LinkedTransferQueue;

/**
 * this class is used to make it possible to get new messages while other tasks(for example sending message) are running
 *
 * Attention: this class must be interrupted from outside to stop working
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class MsgReceiver implements Runnable{
    private final Player player;
    private final Thread thread;
    private LinkedTransferQueue<Message> closeChatroomMsg;
    /**
     * constructor
     * @param player the player which wants to get message from server
     */
    public MsgReceiver(Player player , LinkedTransferQueue<Message> closeChatroomMsg){
        this.player = player;
        this.closeChatroomMsg = closeChatroomMsg;
        thread = new Thread(this);
    }

    /**
     * to access thread
     * @return thread of this msgReceiver
     */
    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        Message fakeLock = new Message("msgReceiver","fakeLock" , ChatroomType.TO_GOD ,MessageTypes.FAKE_MESSAGE , null);
        player.setStartMsg(fakeLock);

        while (!thread.isInterrupted())
        {
            synchronized (fakeLock){
                Message msg = player.getMsg();
                if(msg != null)
                {
                    if(msg.getMsgType() == MessageTypes.CLOSE_CHATROOM){
                        try {
                            closeChatroomMsg.transfer(msg);
                            Logger.log("close chatroom message sent for " + player.getName() , LogLevels.INFO , getClass().getName());
                        }catch (InterruptedException e){
                            System.out.println("interrupted in transferring msg to player in msgReceiver - shouldn't happen.");
                            Logger.log("interrupted in transferring msg to player in msgReceiver - shouldn't happen.",LogLevels.ERROR , getClass().getName());
                        }
                    }
                    else if (msg.getMsgType() == MessageTypes.SILENCE_PLAYER){
                            player.setSilenced(true);
                    }
                    else if(msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_START) {
                        Logger.log(player.getName() + " got start message - lock freed.", LogLevels.INFO , getClass().getName());
                        fakeLock = msg; // now , the lock(startMsg of player) is free , and the JoinServer can continue
                    }
                    else if (msg.getMsgType() == MessageTypes.ALL_PLAYERS_JOINED)
                    {
                        System.out.print("\033[0;32m");
                        System.out.println("All players joined.");
                        System.out.println("If you are ready for game, enter 'ready'");
                        System.out.print("\033[0m");
                    }
                    else if (msg.getMsgType() == MessageTypes.COMMAND_ACCEPTED)
                    {
                        System.out.print("\033[0;32m");
                        System.out.println("Done.");
                        System.out.print("\033[0m");
                        System.out.println(msg.getContent());

                    }
                    else if (msg.getMsgType() == MessageTypes.COMMAND_REFUSED){
                        System.out.print("\033[0;31m");
                        System.out.println("Rejected.");
                        System.out.print("\033[0m");
                        System.out.println(msg.getContent());
                    }
                    else if (msg.getMsgType() == MessageTypes.ACTIONS_PLAYER_VOTED){
                        System.out.print("\033[0;33m");
                        System.out.println(msg.getSender() + ": " + msg.getContent());
                        System.out.print("\033[0m");
                    }
                    else if (msg.getMsgType() == MessageTypes.QUESTION_TO_WATCH || msg.getMsgType() == MessageTypes.QUESTION_TO_CANCEL)
                    {
                        System.out.print("\033[0;31m");
                        System.out.println(msg.getContent());
                        System.out.print("\033[0m");
                        player.getMsgSender().answerQuestion(msg);  // may have bug here
                    }
                    else
                    {
                        if (msg.getChatroomType() == ChatroomType.MAFIA_CHATROOM) // red for mafia chatroom
                            System.out.print("\033[0;31m");
                        System.out.println(msg.getSender() + ": "  + msg.getContent());
                        System.out.print("\033[0m");
                    }
                }
            }
        }
    }
}
