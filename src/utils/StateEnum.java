package utils;

import java.io.Serializable;

/**
 * state of game in each round will be specified by this enum - can be used for saving in file
 */
public enum StateEnum implements Serializable {
    FIRST_NIGHT,
    DAY,
    NIGHT,
    VOTING_TIME,

}
