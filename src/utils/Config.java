package utils;

public class Config {
    private static int PLAYER_NUMBERS = 10;
    private static int DAY_TIME = 5; // 5 minute
    private static int EACH_ROLE_NIGHT_ACTING_TIME = 15; // for example , doctor have 15 seconds to decide who to save
    private static int VOTING_TIME = 30; // 30 seconds
    private static int MAFIA_NUMBER = 3;
    private static int CITIZEN_NUMBER = 7;
    // maybe more

    /**
     * getter
     * @return PLAYER_NUMBERS
     */
    public static int getPlayerNumbers() {
        return PLAYER_NUMBERS;
    }
    /**
     * getter
     * @return DAY_TIME
     */
    public static int getDayTime() {
        return DAY_TIME;
    }
    /**
     * getter
     * @return EACH_ROLE_NIGHT_ACTING_TIME
     */
    public static int getEachRoleNightActingTime() {
        return EACH_ROLE_NIGHT_ACTING_TIME;
    }
    /**
     * getter
     * @return VOTING_TIME
     */
    public static int getVotingTime() {
        return VOTING_TIME;
    }

    /**
     * getter
     * @return MAFIA_NUMBER
     */
    public static int getMafiaNumber() {
        return MAFIA_NUMBER;
    }

    /**
     * getter
     * @return CITIZEN_NUMBER
     */
    public static int getCitizenNumber() {
        return CITIZEN_NUMBER;
    }
}
