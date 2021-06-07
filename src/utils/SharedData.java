//package utils;
//
//import client_side.Player;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * belongs to 'mafia game'
// * a class to store the most needed data of objects to access in any other class
// *          and probably just this class will be saved in a file
// *
// * @author Sepehr Noey
// * @version 1.0
// */
//public class SharedData implements Serializable {
//    private static SharedData sharedData;
//    private List<Player> players;
////    private StateEnum stateEnum; // i must update this field in game loop!!
//
//    /**
//     * constructor
//     *      is used just at the start of the game
//     */
//    private SharedData(){
//        players = new ArrayList<>();
//    }
//
//    /**
//     * singleton pattern for shared data
//     * @return existing sharedData , if doesn't exist , creates one
//     */
//    public static SharedData getSharedData(){
//        if (sharedData == null)
//            sharedData = new SharedData();
//        return sharedData;
//
//    }
//
//    /**
//     * to add player when player is created.
//     * @param player is the entered player
//     */
//    public void addPlayer(Player player){
//        sharedData.players.add(player);
//    }
//
//    /**
//     * to remove player when is kicked out of the game or killed
//     * @param player to remove
//     */
//    public void removePlayer(Player player){
//        sharedData.players.remove(player);
//    }
//
//    /**
//     * to access players in other classes
//     * @return players
//     */
//    public List<Player> getPlayers() {
//        return players;
//    }
//
//    /**
//     * to access alive players
//     *
//     * @return a List of alive players(for using , cast to ArrayList)
//     */
//    public List<Player> getAlives(){
//        List<Player> alives = new ArrayList<>();
//        for (Player player:players)
//        {
//            if (player.isAlive())
//                alives.add(player);
//        }
//        return alives;
//    }
//
//    /**
//     * to access alive mafias of the game
//     * @return a List of alive mafias (for using , cast to ArrayList)
//     */
//    public List<Player> getAliveMafias(){
//        List<Player> aliveMafs = new ArrayList<>();
//        for (Player player: players)
//        {
//            if (player.isAlive() && player.getGroup() == Role_Group.MAFIA_GROUP)
//                aliveMafs.add(player);
//        }
//        return aliveMafs;
//    }
//
//    /**
//     * to access alive citizens of the game
//     *
//     * @return a List of alive citizens (for using , cast to ArrayList)
//     */
//    public List<Player> getAliveCitizens(){
//        List<Player> aliveCits = new ArrayList<>();
//        for (Player player: players)
//        {
//            if (player.isAlive() && player.getGroup() == Role_Group.CITIZEN_GROUP)
//                aliveCits.add(player);
//        }
//        return aliveCits;
//    }
////
////    /**
////     * to know from other classes that it is night or day or voting time
////     * @return stateEnum
////     */
////    public StateEnum getStateEnum() {
////        return stateEnum;
////    }
////
////    /**
////     * to update state of game in other classes
////     * @param stateEnum new state of game
////     */
////    public void setStateEnum(StateEnum stateEnum) {
////        this.stateEnum = stateEnum;
////    }
//}
