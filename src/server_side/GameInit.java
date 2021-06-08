package server_side;

import utils.Config;
import utils.Message;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.Format;
import java.util.Formatter;
import java.util.InputMismatchException;
import java.util.Scanner;

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
        System.out.println("For creating new server enter port: ");
        Scanner scanner = new Scanner(System.in);
        int port = 0;
        Server server = null;
        while (true)
        {
            try {
                port = scanner.nextInt();
                try {
                    server = new Server(port);
                    System.out.println("Successful!");
                    System.out.println("Name : " + server.getName() + "\t\tip: 192.168.1.4\t\tport: " + server.getWelcomeSocket().getLocalPort());
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
        Config config = null;
        while (true)
        {
            try {
                System.out.println("Default game config is listed below , if you want to change it , enter 0 , else 1 :");
                System.out.println("Player numbers : 10\nDay time: 5 min\nEach role time at night: 15s" +
                        "\nVoting time: 30s\nMafia number: 3\nCitizen number: 7\n");
                int choice = scanner.nextInt();
                if (choice == 1){
                    config = new Config();
                    Logger.log("Game config loaded." , LogLevels.INFO , GameInit.class.getName());
                }
                else if(choice == 0) {
                    System.out.println("Choose from existing choices: ");
                    System.out.println("Number of Mafias and Citizens: \n1) 10 total , 3 mafia , 7 citizen\n2) 7 total , 2 mafia , 5 citizen\n3) 5 total , 1 mafia , 4 citizen");
                    int playersMode = scanner.nextInt();
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
                }
                else {
                    throw new InputMismatchException();
                }
            }catch (InputMismatchException e)
            {
                System.out.println("Invalid input! Try again.");
            }
        }



        ServerSocket welcome = server.getWelcomeSocket();
        ObjectOutputStream outObj;
        ObjectInputStream inObj;
        while (true)
        {
            Socket connection = null;
            try {
                connection = welcome.accept();
                outObj = new ObjectOutputStream(connection.getOutputStream());
                inObj = new ObjectInputStream(connection.getInputStream());
                outObj.writeObject(new Message(server.getName() + ","));

            }
            catch (IOException e)
            {
                Logger.log("ioException in accept method. or creating streams" , LogLevels.ERROR , GameInit.class.getName());
            }
        }





    }

}
