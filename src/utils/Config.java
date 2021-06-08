package utils;

import java.io.Serializable;

public class Config implements Serializable {
    private int PLAYER_NUMBERS;
    private int DAY_TIME ; // in minutes
    private int EACH_ROLE_NIGHT_ACTING_TIME ; // in seconds , for example , doctor have 15 seconds to decide who to save
    private int VOTING_TIME ; // in seconds
    private int MAFIA_NUMBER;
    private int CITIZEN_NUMBER ;

    /**
     * constructor with default configs
     */
    public Config(){
        PLAYER_NUMBERS = 10;
        DAY_TIME = 5;
        EACH_ROLE_NIGHT_ACTING_TIME = 15;
        VOTING_TIME = 30;
        MAFIA_NUMBER = 3;
        CITIZEN_NUMBER = 7;
    }

    /**
     * to make a new config
     * @param PLAYER_NUMBERS player num
     * @param DAY_TIME day time
     * @param EACH_ROLE_NIGHT_ACTING_TIME it's just what its name says
     * @param VOTING_TIME vote time
     * @param MAFIA_NUMBER mafia number
     * @param CITIZEN_NUMBER citizen number
     */
    public Config(int PLAYER_NUMBERS , int DAY_TIME , int EACH_ROLE_NIGHT_ACTING_TIME , int VOTING_TIME , int MAFIA_NUMBER , int CITIZEN_NUMBER) {
        this.PLAYER_NUMBERS = PLAYER_NUMBERS;
        this.DAY_TIME = DAY_TIME;
        this.EACH_ROLE_NIGHT_ACTING_TIME = EACH_ROLE_NIGHT_ACTING_TIME;
        this.VOTING_TIME = VOTING_TIME;
        this.MAFIA_NUMBER = MAFIA_NUMBER;
        this.CITIZEN_NUMBER = CITIZEN_NUMBER;

    }




    /**
     * getter
     * @return PLAYER_NUMBERS
     */
    public int getPlayerNumbers() {
        return PLAYER_NUMBERS;
    }
    /**
     * getter
     * @return DAY_TIME
     */
    public int getDayTime() {
        return DAY_TIME;
    }
    /**
     * getter
     * @return EACH_ROLE_NIGHT_ACTING_TIME
     */
    public int getEachRoleNightActingTime() {
        return EACH_ROLE_NIGHT_ACTING_TIME;
    }
    /**
     * getter
     * @return VOTING_TIME
     */
    public int getVotingTime() {
        return VOTING_TIME;
    }

    /**
     * getter
     * @return MAFIA_NUMBER
     */
    public int getMafiaNumber() {
        return MAFIA_NUMBER;
    }

    /**
     * getter
     * @return CITIZEN_NUMBER
     */
    public int getCitizenNumber() {
        return CITIZEN_NUMBER;
    }

}
