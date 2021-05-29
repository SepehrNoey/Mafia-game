package client_side;

import utils.SharedData;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * belongs to 'mafia game'
 * this is the starting point for a player , a single player to enter the game should run this class
 *
 * @author Sepehr Noey
 * @version 1.0
 *
 */
public class RunClient {
    public static void main(String[] args)
    {
        if (SharedData.getSharedData().getPlayers().size() >= 10)
        {
            System.out.println("Sorry! No empty seat for a new player.");
            System.exit(0);
        }

        // empty seat exists // joining server

        Socket connection = null;
        Scanner scanner = new Scanner(System.in);
        String[] data = new String[3];
        System.out.println("Welcome to mafia-game ! ");

        while (true){
            try {
                System.out.println("Please enter 'your name' 'server ip' and 'port' with one space between them:");
                data = scanner.nextLine().trim().split(" ");
                if (data.length != 3) {
                    Logger.log("more or less input from client for connecting to server" , LogLevels.INFO , RunClient.class.getName());
                    throw new InputMismatchException("Invalid input! Make sure that there is just one space between each parameter!");
                }
                Logger.log(data[0] + " trying to connect to server." , LogLevels.INFO , RunClient.class.getName());
                connection = new Socket(data[1] , Integer.parseInt(data[2]));
                Logger.log(data[0] + " connected" , LogLevels.INFO , RunClient.class.getName());
                break;
            }catch (InputMismatchException e){
                System.out.println(e.getMessage());
            }
            catch (IOException e){
                Logger.log(data[0] + " can't connect to server" , LogLevels.ERROR , RunClient.class.getName());
                System.out.println("Cannot connect to server. Try again.");
            }
        }

        Player player = new Player(data[0] ,connection);
        SharedData.getSharedData().addPlayer(player);
        Logger.log(player.getName() + " added - start playing", LogLevels.INFO , RunClient.class.getName());
        player.play();
    }
}
