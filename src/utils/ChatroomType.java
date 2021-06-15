package utils;

import java.io.Serializable;

/**
 * types of chatroom - will be used for saving in file(but i didn't get time to implement saving!!)
 */
public enum ChatroomType implements Serializable {

    MAFIA_CHATROOM,
    PUBLIC_CHATROOM,
    TO_GOD,
    TO_CLIENT;

}
