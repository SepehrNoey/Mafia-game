package utils;

import java.io.Serializable;

public class Config implements Serializable {
    private int PLAYER_NUMBERS = 10;
    private int DAY_TIME = 5; // 5 minute
    private int EACH_ROLE_NIGHT_ACTING_TIME = 15; // for example , doctor have 15 seconds to decide who to save
    private int VOTING_TIME = 30; // 30 seconds
    private int MAFIA_NUMBER = 3;
    private int CITIZEN_NUMBER = 7;
    private int DEFENSE_TIME = 1; // 1 minute
    // maybe more

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

    /**
     * getter
     * @return defense time(in minute)
     */
    public int getDEFENSE_TIME() {
        return DEFENSE_TIME;
    }
}
