package server_side.sendGet;

import utils.Message;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * a class to send message to a client - runs on main thread - not a separate thread
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class MsgSender {
    private ObjectOutputStream outObj;

    /**
     * constructor
     * @param outObj opened object output stream
     */
    public MsgSender(ObjectOutputStream outObj)
    {
        this.outObj = outObj;
    }

    /**
     * sends a message to player
     * @param msg the message to be sent
     */
    public void sendMsg(Message msg){
        synchronized (outObj)
        {
            try {
                outObj.writeObject((Object) msg);
            }catch (IOException e){
                Logger.log( "can't send message to client" , LogLevels.ERROR, getClass().getName());
            }
        }
    }

    /**
     * getter
     * @return object input stream
     */
    public ObjectOutputStream getOutObj() {
        return outObj;
    }
}
