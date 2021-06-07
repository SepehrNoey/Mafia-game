package client_side;

import utils.ChatroomType;
import utils.Role_Group;
import utils.SharedData;
import utils.StateEnum;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.util.Scanner;

/**
 * to send message in multi thread way
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class MsgSender implements Runnable{
    private Thread thread;
    private Player player;

    /**
     * constructor
     * @param player which player is sending message
     */
    public MsgSender(Player player){
        this.player = player;
        thread = new Thread(this);
    }

    /**
     * to access thread of this class
     * @return thread
     */
    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (!thread.isInterrupted())
        {
            String userInput = scanner.nextLine();
            if (userInput.trim().equalsIgnoreCase("exit")) // exiting keyword // maybe should be handled later
            {
                System.out.println("Thanks for playing with us!");
                Logger.log(player.getName() + " exited." , LogLevels.INFO , this.getClass().getName());
                System.exit(0);
            }
            // the message sender itself , recognizes where does this message wants to be go

//            if (stateEnum == StateEnum.FIRST_NIGHT)
//            {
//
//            }

//            String[] split = userInput.trim().split(" ");
//            if (split.length == 2 && split[0].equals("vote"))   // voting keyword
//            {
//                boolean state =  player.vote(split[1]);
//                if (!state)
//                {
//                    System.out.println("Didn't find player. Try again.");
//                    Logger.log("can't find player with this name: " + split[1] ,
//                            LogLevels.WARN , getClass().getName());
//                }
//                else {
//                    if(player.getGroup() == Role_Group.CITIZEN_GROUP)
//                        player.sendMsg(player.getName() + " voted " + split[1] ,ChatroomType.PUBLIC_CHATROOM);
//                    else   // god should recognize if it should be sent to public chatroom or mafia chatroom
//                        player.sendMsg(player.getName() + " voted " + split[1] , ChatroomType.TO_GOD);
//                }
//            }
//            else   // normal message
//                player.sendMsg(userInput ,chatroomType);
        }
    }
}
