package server_side.manager;

import server_side.model.Player_ServerSide;
import server_side.model.Server;
import utils.Config;
import utils.StateEnum;

import java.io.Serializable;

/**
 * this class will be used for saving data in file and also accessing to needed data in any class in server side
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class GameState implements Serializable {
    private Config config;
    private Server server;
    private StateEnum state;
    private Player_ServerSide turnPlayer;

    public GameState(Config config , Server server , StateEnum state , Player_ServerSide turnPlayer){
        this.config = config;
        this.server = server;
        this.state = state;
        if (turnPlayer != null)
            this.turnPlayer = turnPlayer;
    }

    /**
     * getter
     * @return config of saved game
     */
    public Config getConfig() {
        return config;
    }

    /**
     * setter
     * @param server to set server when loading
     */
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * getter
     * @return to get state when loading
     */
    public StateEnum getState() {
        return state;
    }

    /**
     * setter
     * @param state the new state of game (Day , night ,..)
     */
    public void setState(StateEnum state) {
        this.state = state;
    }

    /**
     * getter
     * @return for loading game
     */
    public Player_ServerSide getTurnPlayer() {
        return turnPlayer;
    }

    /**
     * setter
     * @param turnPlayer the new turnPlayer
     */
    public void setTurnPlayer(Player_ServerSide turnPlayer) {
        this.turnPlayer = turnPlayer;
    }

    /**
     * to access the data in server
     * @return server
     */
    public Server getServer() {
        return server;
    }

}
