package utils;

import java.io.Serializable;

/**
 * types of chatroom
 */
public enum ChatroomType implements Serializable {
    BEFORE_START_CHATROOM,
    MAFIA_CHATROOM,
    PUBLIC_CHATROOM,
    TO_GOD,
    TO_CLIENT
}
