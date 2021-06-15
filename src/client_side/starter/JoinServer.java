package client_side.starter;

import client_side.model.Player;
import utils.ChatroomType;
import utils.Config;
import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;

/**
 * belongs to 'mafia game'
 * this is the starting point for a player , a single player to enter the game should run this class
 *
 * @author Sepehr Noey
 * @version 1.0
 *
 */
public class JoinServer {
    public static void main(String[] args)
    {
        Socket connection = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to mafia-game ! ");
        String[] data = new String[2];
        String name;

        while (true){
            try {
                System.out.println("Please enter 'server ip' and 'port' with one space between them:(for exit enter 'exit')");
                data = scanner.nextLine().trim().split(" ");
                if (data.length != 2) {
                    if (data.length == 1 && data[0].equalsIgnoreCase("exit")) {
                        System.out.println("Good Bye!");
                        System.exit(0);
                    }
                    throw new InputMismatchException("Invalid input! Make sure that there is just one space between each parameter!");
                }
                Logger.log("trying to connect to server." , LogLevels.INFO , JoinServer.class.getName());
                connection = new Socket(data[0] , Integer.parseInt(data[1]));
                Logger.log(data[0] + " connected" , LogLevels.INFO , JoinServer.class.getName());
                System.out.println("Connected to server.");
                try {
                    ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                    Message msg = (Message) in.readObject();
                    String[] split = msg.getContent().trim().split(",");
                    if (split[1].equalsIgnoreCase("empty"))
                    {
                        System.out.println("1) " + split[0] + "\t\t" + split[2]);// should be in this format : serverName,empty(or full),players: 7/10
                        System.out.println("Server has empty seat for new player.If you want to join enter your name , else enter 'exit'");
                        while (true) {
                            name = scanner.nextLine().trim();
                            if (name.equalsIgnoreCase("exit")) {
                                System.out.println("Good Bye!"); // maybe need to notify that the player exited!
                                out.writeObject(new Message("JoinServer" , "player exited." , ChatroomType.TO_GOD , MessageTypes.ACTIONS_EXIT , null));
                                System.exit(0);
                            }
                            out.writeObject(new Message("JoinServer", name, ChatroomType.TO_GOD, MessageTypes.JOIN_REQUEST , null));
                            msg = (Message) in.readObject(); // should has this content : allow(or deny)
                            if (msg.getContent().equalsIgnoreCase("allow"))
                            {
                                System.out.println("Accepted.Getting settings from server...");
                                LinkedTransferQueue<Message> startMsg = new LinkedTransferQueue<>();
                                Player player = new Player(name , connection ,in ,out, null , null ,startMsg);
                                Config config = (Config)in.readObject();
                                if (config != null)
                                {
                                    player.setConfig(config);
                                    player.startMsgSender();
                                    player.startMsgReceiver();
                                    System.out.println("Waiting for other players to join...\nYou can chat with others until all players join:");
                                    try {
                                        Thread.sleep(1000); // is used to make sure that the start message is locked in msgReceiver
                                    }catch (InterruptedException e)
                                    {
                                        Logger.log("interrupted in sleeping for 1000 ms." , LogLevels.WARN , JoinServer.class.getName());
                                    }
                                    try {
                                        startMsg.take();
                                    }catch (InterruptedException e)
                                    {
                                        System.out.println("Interrupted in startMsg taking. for " + player.getName());
                                        Logger.log("Interrupted in startMsg taking. for " + player.getName() , LogLevels.ERROR , JoinServer.class.getName());
                                    }
//                                    player.stopMsgReceiver();
                                    player.playLoop();  // starting point for game in client side

                                }
                                else {
                                    Logger.log(player.getName() + " can't get settings from server." , LogLevels.ERROR , JoinServer.class.getName());
                                    System.out.println("Couldn't get settings from server, exiting...");
                                    System.exit(-1);
                                }
                                break;
                            }
                            else
                                System.out.println("This name already exists. Try another name: ");
                                // name exists

                        }
                    }
                    else {
                        System.out.println("Sorry,No empty seat for new players!");
                        System.exit(0);
                    }

                }catch (IOException e)
                {
                    Logger.log("can't make object input or output stream , or can't get message object from server." , LogLevels.ERROR , JoinServer.class.getName());
                }catch (ClassNotFoundException e){
                    Logger.log("can't find class Message or class Config" , LogLevels.ERROR , JoinServer.class.getName());
                }
            break;

            }catch (InputMismatchException e){
                System.out.println(e.getMessage());
            }
            catch (IOException e){
                Logger.log(data[0] + " can't connect to server" , LogLevels.ERROR , JoinServer.class.getName());
                System.out.println("Sorry! - Server doesn't exist or no seat for new players!. Try again...");
            }
        }

    }
}
