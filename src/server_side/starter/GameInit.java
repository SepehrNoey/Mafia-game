package server_side.starter;

import server_side.manager.GameLoop;
import server_side.manager.GameState;
import server_side.manager.Logic;
import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.*;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * belongs to 'mafia game'
 * this class is written for initializing and giving the rest to Server class
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class GameInit {
    public static void main(String[] args) {
        System.out.println("Welcome to 'mafia game' in server side!");
        Scanner scanner = new Scanner(System.in);
        System.out.println("For creating new server enter port: ");
        int port = 0;
        Server server = null;
        Logic logic = null;
        while (true)
        {
            try {
                port = scanner.nextInt();
                try {
                    logic = new Logic(null , null);
                    server = new Server(port , logic);
                    System.out.println("Successful!");
                    System.out.println("Name : " + server.getName() + "\t\tip: 192.168.1.5(for local network) - 127.0.0.1(for one device)\t\tport: " + server.getWelcomeSocket().getLocalPort() + "\n");
                    break;
                }catch (IOException exc)
                {
                    System.out.println("Sorry , this port is busy. Try another port:");
                    scanner.nextLine();
                }
                catch (IllegalArgumentException exc2)
                {
                    scanner.nextLine();
                    System.out.println("Too large! Enter a smaller integer:");
                }
            }catch (InputMismatchException e)
            {
                scanner.nextLine();
                System.out.println("Invalid input. Enter a positive integer as port: ");
            }
        }
        // created successfully
        ArrayBlockingQueue<Message> sharedInbox = server.getSharedInbox();
        LinkedTransferQueue<Message> readyMsgs = new LinkedTransferQueue<>();
        server.getMsgSeparator().getThread().start();
        Config config = null;
        while (true)
        {
            try {
                System.out.println("Default game config is listed below , if you want to change it , enter 0 , else 1 :");
                System.out.println("Player numbers : 10\nDay time: 5 min\nEach role time at night: 15s\nVoting time: 30s\n" +
                                "Mafia number: 3\nCitizen number: 7");
                int choice = scanner.nextInt();
                if (choice == 1){
                    config = new Config();
                    Logger.log("Game config loaded." , LogLevels.INFO , GameInit.class.getName());
                    break;
                }
                else if(choice == 0) {
                    System.out.println("Choose from existing choices: ");
                    System.out.println("Number of Mafias and Citizens: \n1) 10 total , 3 mafia , 7 citizen\n2) 7 total , 2 mafia , 5 citizen\n3) 5 total , 1 mafia , 4 citizen");
                    int playersMode = scanner.nextInt();
                    scanner.nextLine();
                    if (playersMode > 3 || playersMode < 1)
                        throw new InputMismatchException();
                    System.out.println("Enter 'day time(in minute)' , 'each role time at night(in seconds)' and 'voting time(in seconds)' with one space between them");
                    String[] split = scanner.nextLine().trim().split(" ");
                    if (split.length != 3)
                        throw new InputMismatchException();
                    config = new Config(playersMode == 1 ? 10 : playersMode == 2 ? 7 : 5 , Integer.parseInt(split[0]) , Integer.parseInt(split[1]) ,
                            Integer.parseInt(split[2]) , playersMode == 1 ? 3 : playersMode == 2 ? 2 : 1 ,
                            playersMode == 1 ? 7 : playersMode == 2 ? 5 : 4);
                    Logger.log("Game config loaded." , LogLevels.INFO , GameInit.class.getName());
                    System.out.println("Game config loaded.");
                    break;
                }
                else {
                    throw new InputMismatchException();
                }
            }catch (InputMismatchException e)
            {
                System.out.println("Invalid input! Try again.");
                scanner.nextLine();
            }
        }
        server.setGameConfig(config);
        ServerSocket welcome = server.getWelcomeSocket();
        ObjectOutputStream outObj;
        ObjectInputStream inObj;
        Message msg;
        int joinedPlyNum = 0;
        ArrayList<Player_ServerSide> players = new ArrayList<>();

        while (joinedPlyNum < config.getPlayerNumbers())
        {
            Socket connection = null;
            try {
                System.out.println("Waiting for player " + (joinedPlyNum + 1) + "-th ..." );
                connection = welcome.accept();
                outObj = new ObjectOutputStream(connection.getOutputStream());
                inObj = new ObjectInputStream(connection.getInputStream());
                String emptyOrFull = server.currentPlyNum() >= config.getPlayerNumbers() ? "full" : "empty";
                outObj.writeObject(new Message(server.getName(),server.getName() + "," + emptyOrFull + ",players: "
                        + server.currentPlyNum() + "/" + config.getPlayerNumbers()
                        , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                msg = (Message) inObj.readObject();
                if (msg.getMsgType() == MessageTypes.ACTIONS_EXIT){
                    continue; // skipping this time
                }
                else if (msg.getMsgType() == MessageTypes.JOIN_REQUEST){
                    boolean res = server.isNameExist(msg.getContent());
                    if (!res) // player accepted
                    {
                        joinedPlyNum++;
                        Player_ServerSide player = new Player_ServerSide(msg.getContent() , connection ,inObj , outObj, sharedInbox ,readyMsgs);
                        server.addPlayer(player);
                        player.getMsgReceiver().getThread().start();
                        outObj.writeObject(new Message(server.getName(), "allow" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                        outObj.writeObject(config);
                        players.add(player);
                        System.out.println(player.getName() + " joined.");
                        if (!(joinedPlyNum == config.getPlayerNumbers()))
                            System.out.println("Waiting for other players...");
                    }
                    else
                    {
                        outObj.writeObject(new Message(server.getName() , "deny" , ChatroomType.TO_CLIENT , MessageTypes.INFO , null));
                    }
                }

            }
            catch (IOException e)
            {
                Logger.log("ioException in accept method. or creating streams" , LogLevels.ERROR , GameInit.class.getName());
            }
            catch (ClassNotFoundException e)
            {
                Logger.log("can't find class Message." , LogLevels.ERROR , GameInit.class.getName());
            }
        }

        System.out.println("All players joined.");
        System.out.println("Waiting for all players to send 'ready'...");
        for (Player_ServerSide player:players)
        {
            player.getMsgSender().sendMsg(new Message(server.getName(),"All players joined." , ChatroomType.TO_CLIENT , MessageTypes.ALL_PLAYERS_JOINED , null));
        }

        int readyGotNum = 0;
        while (readyGotNum < config.getPlayerNumbers())
        {
            try {
                Message rdMsg = readyMsgs.take();
                server.notifyList(players ,rdMsg);
                readyGotNum++;
            }catch (InterruptedException e)
            {
                Logger.log("interrupted in ready msg getting." , LogLevels.WARN , GameInit.class.getName());
                System.out.println("interrupted in ready msg getting.");
            }
        }
        System.out.println("all players are ready now . going to start the game.");
        Logger.log("all players are ready now . going to start the game." , LogLevels.INFO , GameInit.class.getName());

        server.setRoles();
        server.notifyList(server.getPlayers(), new Message(server.getName() , "start message to players." , ChatroomType.TO_CLIENT , MessageTypes.ACTIONS_GOD_ORDERED_START , null));
        System.out.println("Start message sent to players.");
        GameState gameState = new GameState(config , server , StateEnum.FIRST_NIGHT , null); // at first , turnPlayer should be null
        logic.setGameState(gameState);
        logic.setServer(server);
        GameLoop gameLoop = new GameLoop(server,logic ,gameState);
        gameLoop.playLoop();
    }

}
