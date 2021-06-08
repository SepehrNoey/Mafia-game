package client_side;

import utils.ChatroomType;
import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

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
    /**
     * constructor
     * @param player the player which wants to get message from server
     */
    public MsgReceiver(Player player){
        this.player = player;
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
        Message fakeLock = new Message("msgReceiver","fakeLock" , ChatroomType.TO_GOD ,MessageTypes.FAKE_MESSAGE);
        player.setStartMsg(fakeLock);

        while (!thread.isInterrupted())
        {
            synchronized (fakeLock){
                Message msg = player.getMsg();
                if(msg != null)
                {
                    if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_START) {
                        Logger.log(player.getName() + " got start message - lock freed.", LogLevels.INFO , getClass().getName());
                        fakeLock = msg; // now , the lock(startMsg of player) is free , and the JoinServer can continue
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
