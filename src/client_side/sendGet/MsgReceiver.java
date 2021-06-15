package client_side.sendGet;

import client_side.model.Player;
import utils.ChatroomType;
import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.util.concurrent.ArrayBlockingQueue;
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
    private LinkedTransferQueue<Message> nextStepMsg;
    private LinkedTransferQueue<Message> startMsg;
    private LinkedTransferQueue<Message> loopMsg;
    private ArrayBlockingQueue<Message> answerQueue;


    /**
     * constructor
     * @param player the player which wants to get message from server
     * @param nextStepMsg next step msg
     * @param startMsg start msg
     * @param loopMsg loop msg
     */
    public MsgReceiver(Player player , LinkedTransferQueue<Message> nextStepMsg, LinkedTransferQueue<Message> startMsg , LinkedTransferQueue<Message> loopMsg , ArrayBlockingQueue<Message> answerQueue){
        this.answerQueue = answerQueue;
        this.startMsg = startMsg;
        this.player = player;
        this.nextStepMsg = nextStepMsg;
        this.loopMsg = loopMsg;
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
                if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_FIRST_NIGHT_GREETING || msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_NIGHT_ACT
                || msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_DAY_PUBLIC_CHAT || msg.getMsgType() == MessageTypes.ACTIONS_GOD_ORDERED_VOTE ||
                        msg.getMsgType() == MessageTypes.TEAMMATES || msg.getMsgType() == MessageTypes.VOTE_RESULT || msg.getMsgType() == MessageTypes.END_OF_GAME)
                {
                    try {
                        loopMsg.transfer(msg);
                    }catch (InterruptedException e)
                    {
                        Logger.log("interrupted while transfering loopMsg." ,LogLevels.ERROR , getClass().getName());
                    }
                }
                else if (msg.getMsgType() == MessageTypes.ACTIONS_GOD_SET_ROLE ){
                    // must be in this format, for example:  'MAFIA_GROUP GODFATHER' , or it can be 'CITIZEN_GROUP DETECTIVE'
                    String[] split = msg.getContent().trim().split(" ");
                    player.setGroup(player.roleFromString(split[0]));
                    player.setRole(player.roleFromString(split[1]));
                }
                else if(msg.getMsgType() == MessageTypes.CLOSE_CHATROOM){
                    try {
                        nextStepMsg.transfer(msg);
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
                    Logger.log(player.getName() + " got start message - now in other thread playLoop should be executed.", LogLevels.INFO , getClass().getName());
                    try {
                        startMsg.transfer(msg);
                    }catch (InterruptedException e)
                    {
                        Logger.log(player.getName() + " Interrupted while transferring startMsg." ,LogLevels.ERROR , getClass().getName());
                    }
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
                    try {
                        answerQueue.put(msg);
                    }catch (InterruptedException e)
                    {
                        Logger.log(player.getName() +  " interrupted in answerQueue transferring" , LogLevels.ERROR ,getClass().getName());
                    }
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
