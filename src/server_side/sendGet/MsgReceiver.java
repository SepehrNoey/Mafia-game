package server_side.sendGet;

import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class MsgReceiver implements Runnable {
    private Thread thread;
    private ObjectInputStream inObj;
    private ArrayBlockingQueue<Message> sharedInbox;
    private LinkedTransferQueue<Message> readyMsgs;
    private String name;
    /**
     * constructor
     * @param inObj opened object input stream
     * @param sharedInbox is the sharedInbox
     */
    public MsgReceiver(String name , ObjectInputStream inObj , ArrayBlockingQueue<Message> sharedInbox , LinkedTransferQueue<Message> readyMsgs){
        this.inObj = inObj;
        this.name = name;
        thread = new Thread(this);
        this.sharedInbox = sharedInbox;
        this.readyMsgs = readyMsgs;
    }

    /**
     * to get message from client
     * @return received message from client
     */
    public Message getMsg(){
        try {
            return (Message) inObj.readObject();
        }catch (ClassNotFoundException e){
            Logger.log("can't find class Message" , LogLevels.ERROR , getClass().getName());
        }
        catch (IOException e){
            Logger.log("server can't get message from " + name , LogLevels.ERROR , getClass().getName());
        }
        return null;
    }

    @Override
    public void run(){
        boolean isReadySent = false;
        while (!thread.isInterrupted()){
            Message msg = getMsg();
            if (msg != null)
            {
                try {
                    if (msg.getMsgType() == MessageTypes.PLAYER_IS_READY && !isReadySent){ // to make sure , every player can send just one 'ready'
                        readyMsgs.transfer(msg);
                        isReadySent = true;
                    }
                    else
                        sharedInbox.put(msg); // to make a connection between server and messages got from msgReceiver , and also make sure that server sees all messages
                }catch (InterruptedException e)
                {
                    Logger.log("interrupted while putting msg to sharedInbox.", LogLevels.WARN , MsgReceiver.class.getName());
                    System.out.println("Warning! interrupted while putting msg to sharedInbox.");
                }
            }
        }
    }

    /**
     * to access thread of msgReceiver
     * @return Thread of msgReceiver
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * starting msgReceiver
     */
    public void startMsgReceiver(){
        thread.start();
    }

    /**
     * stop msgReceiver
     */
    public void stopMsgReceiver(){
        thread.interrupt();
    }
}
