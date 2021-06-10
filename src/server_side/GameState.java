package server_side;

import utils.Config;
import utils.StateEnum;

import java.io.Serializable;

public class GameState implements Serializable {
    private Config config;
    private Server server;
    private StateEnum state;
    private Player_ServerSide turnPlayer;

    public GameState(Config config , Server server , StateEnum state , Player_ServerSide turnPlayer){
        this.config = config;
        this.server = server;
        this.state = state;
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
}
