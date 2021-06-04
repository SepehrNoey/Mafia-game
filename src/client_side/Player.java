package client_side;

import utils.Role_Group;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

/**
 * belongs to 'mafia game'
 * a class to store players' data and play
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Player implements Serializable {
    private String name;
    private Role_Group role;
    private Role_Group group;
    private boolean isAlive;
    private Socket liveConnection;
    private InputStream inputStream;
    private OutputStream out;
    private Scanner in;

    /**
     * constructor
     * @param name is the name of player
     */
    public Player(String name , Socket socket){
        this.name = name;
        isAlive = true;
        liveConnection = socket;
        try {
            inputStream = liveConnection.getInputStream();
            in = new Scanner(inputStream);
            out = liveConnection.getOutputStream();
        }catch (IOException e){
            Logger.log("cannot make input or output stream" , LogLevels.ERROR , Player.class.getName());
            System.out.println("ERROR - exiting");
            System.exit(-1);
        }
    }

    /**
     * is needed for loading game from file
     * @param liveConnection  is the new live connection
     */
    public void setLiveConnection(Socket liveConnection) {
        this.liveConnection = liveConnection;
    }

    public void play(){
        System.out.println("Waiting for other players to join...");
        String start = in.nextLine();

        // play
    }

    /**
     * to access name
     * @return name of player
     */
    public String getName() {
        return name;
    }

    public void act(){

    }

}
