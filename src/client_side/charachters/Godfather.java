package client_side.charachters;

import client_side.Player;
import utils.Role_Group;
import utils.SharedData;
import utils.logClasses.Logger;

import java.net.Socket;
import java.util.ArrayList;

public class Godfather extends Player {
    public Godfather(String name , Socket socket){
        super(name, socket, Role_Group.GOD_FATHER, Role_Group.MAFIA_GROUP);
    }

    @Override
    public void act() {
        ArrayList<Player> aliveCitizens = (ArrayList<Player>)SharedData.getSharedData().getAliveCitizens();
        int counter = 0;
        System.out.println("List of players you can choose to kill: ");
        for (Player player:aliveCitizens)
        {
            counter++;
            System.out.println(counter + ") " + player.getName());
        }
        System.out.println("Now you can chat with other mafias to decide who to kill: (30s)\nAfter deciding enter : 'vote playerName' ");
        // here the messages are coming in multi threaded way
        clearVote();
        startMsgReceiver();
        startMsgSender();
        try {
            Thread.sleep(30000);
        }catch (InterruptedException e)
        {

        }

    }
}
