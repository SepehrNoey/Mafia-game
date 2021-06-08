package client_side;

import utils.ChatroomType;
import utils.MessageTypes;
import utils.Role_Group;
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
            scanner.nextLine();
            String userInput = scanner.nextLine();
            String[] split = userInput.trim().split(" ");
            String target = null;
            if (split.length >= 2)
            {
                for (int i = 1 ; i < split.length ; i++)
                    target += split[i] + " ";
                target = target.trim();
            }

            // what command is entered , or a normal chat

            // one word commands
            if (userInput.trim().equalsIgnoreCase("exit")) // exiting keyword // maybe should be handled later by god
            {
                player.sendMsg(player.getName() + " exited",ChatroomType.TO_GOD , MessageTypes.ACTIONS_EXIT , null);
                Logger.log(player.getName() + " exited." , LogLevels.INFO , this.getClass().getName());
                System.out.println("Thanks for playing with us :)");
                System.exit(0);
            }
            else if (userInput.trim().equalsIgnoreCase("ready"))
            {
                player.sendMsg(player.getName() + " is ready to start." , ChatroomType.TO_GOD ,MessageTypes.PLAYER_IS_READY , null);
            }
            else if (split.length >= 2 && split[0].equalsIgnoreCase("cancel") && player.getRole() == Role_Group.MAYOR)
            {
                player.sendMsg(player.getName() + " requests for cancel voting." , ChatroomType.TO_GOD , MessageTypes.ACTIONS_MAYOR_ORDERED_CANCEL_VOTING , null);
            }

            // two word commands

            else if (split.length >= 2 && split[0].equalsIgnoreCase("vote")) // invalid player name should be handled
            {
                player.sendMsg(player.getName() + " voted " + target, ChatroomType.TO_GOD
                        ,MessageTypes.ACTIONS_PLAYER_VOTED , target);
            }
            else if (split.length >= 2 && split[0].equalsIgnoreCase("kill"))
            {
                if (player.getRole() == Role_Group.GODFATHER)
                    player.sendMsg(player.getName() + " requests for killing." + target , ChatroomType.TO_GOD
                            , MessageTypes.ACTIONS_GODFATHER_ORDERED_KILL , target);
                else if (player.getRole() == Role_Group.SNIPER)
                    player.sendMsg(player.getName() + " requests for killing." + target , ChatroomType.TO_GOD
                    ,MessageTypes.ACTIONS_SNIPER_ORDERED_KILL , target);
                else {
                    System.out.println("Sorry ,you can't request for killing!");
                    Logger.log(player.getName() + " sent invalid request for killing " + target , LogLevels.WARN, getClass().getName());
                }
            }
            else if (split.length >= 2 && split[0].equalsIgnoreCase("save"))
            {
                if (player.getRole() == Role_Group.DOCTOR_LECTER)
                    player.sendMsg(player.getName() + " requests for saving " + target + "." , ChatroomType.TO_GOD , MessageTypes.ACTIONS_LECTER_ORDERED_SAVE , target);
                else if (player.getRole() == Role_Group.DOCTOR)
                    player.sendMsg(player.getName() + " requests for saving " + target + "." , ChatroomType.TO_GOD , MessageTypes.ACTIONS_DOCTOR_ORDERED_SAVE , target);
                else {
                    System.out.println("Sorry ,you can't request for saving!");
                    Logger.log(player.getName() + " sent invalid request for saving " + target , LogLevels.WARN, getClass().getName());
                }
            }
            else if (split.length >= 2 && split[0].equalsIgnoreCase("silence") && player.getRole() == Role_Group.PSYCHOLOGIST)
            {
                player.sendMsg(player.getName() + " requests for silencing " + target , ChatroomType.TO_GOD , MessageTypes.ACTIONS_PSYCHOLOGIST_ORDERED_SILENCE , target);
            }

            // inquiry can be one word or two word command , depending on player's role

            else if (userInput.trim().equalsIgnoreCase("inquiry"))
            {
                if (player.getRole() == Role_Group.DETECTIVE)
                    player.sendMsg(player.getName() + " requests for inquiry for " + target + "." ,ChatroomType.TO_GOD , MessageTypes.ACTIONS_DETECTIVE_ORDERED_INQUIRY , target);
                else if (player.getRole() == Role_Group.DIE_HARD)
                    player.sendMsg(player.getName() + " requests for inquiry." ,ChatroomType.TO_GOD , MessageTypes.ACTIONS_DIEHARD_ORDERED_INQUIRY , null);
                else
                {
                    System.out.println("Sorry ,you can't request inquiry!");
                    Logger.log("invalid request for inquiry" , LogLevels.WARN, getClass().getName());
                }
            }

            else{ // normal chat
                player.sendMsg(userInput , ChatroomType.TO_GOD , MessageTypes.NORMAL_CHAT , null);
            }
        }
    }
}
