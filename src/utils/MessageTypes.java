package utils;

import java.io.Serializable;

public enum MessageTypes implements Serializable {
    NORMAL_CHAT,
    ACTIONS_VOTE,
    ACTIONS_EXIT,
    ACTIONS_JOIN,
    GOD_TO_CLIENT_STATE_DESCRIPTION,

    //... SHOULD BE MORE
}
