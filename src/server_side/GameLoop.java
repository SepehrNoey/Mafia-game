package server_side;

public class GameLoop {
    private Server server;
    private GameState gameState;

    /**
     * constructor
     * @param server server of this game
     * @param gameState the game state where to begin game from - it can be a new created or a loaded one
     */
    public GameLoop(Server server , GameState gameState){
        this.server = server;
        this.gameState = gameState;
    }



}
