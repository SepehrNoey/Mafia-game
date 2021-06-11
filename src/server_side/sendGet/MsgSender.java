package server_side.sendGet;

import utils.Message;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MsgSender {
    private Socket connection;
    private ObjectOutputStream outObj;

    /**
     * constructor
     * @param connection opened socket to player
     */
    public MsgSender(Socket connection)
    {
        this.connection = connection;
        try {
            outObj = new ObjectOutputStream(connection.getOutputStream());
        }catch (IOException e){
            Logger.log("can't make output object stream." , LogLevels.ERROR , MsgSender.class.getName());
            System.out.println("Error.can't make output object stream. exiting...");
            System.exit(-1);
        }
    }

    /**
     * sends a message to player
     * @param msg the message to be sent
     */
    public void sendMsg(Message msg){
        try {
            outObj.writeObject(msg);
        }catch (IOException e){
            Logger.log( "can't send message to client" , LogLevels.ERROR, getClass().getName());
        }
    }

}
