package client_side;

import utils.ChatroomType;
import utils.Message;

/**
 * this class is used to make it possible to get new messages while other tasks(for example sending message) are running
 *
 * Attention: this class must be interrupted from outside to stop working
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class MsgReceiver implements Runnable{
    private Player player;
    private Thread thread;
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
        while (!thread.isInterrupted())
        {
            Message msg = player.getMsg();
            if(msg != null)
            {
                if(msg.getChatroomType() == ChatroomType.MAFIA_CHATROOM)
                    System.out.print("\033[0;31m");
                System.out.println(msg.getSender() + ": "  + msg.getContent());
                System.out.print("\033[0m");
            }
        }
    }
}