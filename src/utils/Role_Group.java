package utils;

import java.io.Serializable;

/**
 * belongs to 'mafia game'
 * an enum to store character kinds
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public enum Role_Group implements Serializable {
    MAFIA_GROUP,

    GOD_FATHER,
    DOCTOR_LECTER,
    NORMAL_MAFIA,

    CITIZEN_GROUP,

    DOCTOR,
    DETECTIVE,
    SNIPER,
    CITIZEN,
    MAYOR,
    PSYCHOLOGIST,
    DIE_HARD

}
