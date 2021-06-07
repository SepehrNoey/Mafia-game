package server_side;

import utils.ChatroomType;
import utils.Message;
import utils.MessageTypes;
import utils.logClasses.LogLevels;
import utils.logClasses.Logger;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket connection;
    private OutputStream outputStream;
    private ObjectOutputStream outObj;
    private InputStream inputStream;
    private ObjectInputStream inObj;

    /**
     * constructor
     * @param socket is the connection socket to the client
     */
    public ClientHandler(Socket socket){
        try {
            connection = socket;
            outputStream = connection.getOutputStream();
            outObj = new ObjectOutputStream(outputStream);
            inputStream = connection.getInputStream();
            inObj = new ObjectInputStream(inputStream);
            Logger.log("client handler made successfully" , LogLevels.INFO , getClass().getName());
        }catch (IOException e){
            Logger.log("cant make input or output stream in server side" , LogLevels.ERROR , getClass().getName());
        }
    }

    /**
     * to send message to client , must be used in this way :
     *          msg == null && msgType == null && createdMsg == 'a created before message object'  ---> to send a created before message object
     *          or
     *          msg == 'some string' && msgType == 'a MessageType enum' && createdMsg == null  ---> to create a new message object
     *
     *          otherwise ---> it doesn't do anything
     *
     * @param msg is the message to be sent
     * @param msgType is the type of new Message from enum MessageTypes
     * @param createdMsg is the created before(not a new message) message object
     */
    public void sendMsg(String msg , MessageTypes msgType, Message createdMsg){
        try {
            if (msg != null && msgType != null && createdMsg == null)
            {
                outObj.writeObject(new Message("God" , msg  , ChatroomType.TO_CLIENT , msgType));
            }
            else if (msg == null && msgType == null && createdMsg != null)
            {
                outObj.writeObject(createdMsg);
            }
            else {
                Logger.log("trying to create a new message , though a created message exists." , LogLevels.WARN , getClass().getName());
            }
        }catch (IOException e){
            Logger.log( "can't send message to client" , LogLevels.ERROR, getClass().getName());
        }
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
            Logger.log("server can't get message from client" , LogLevels.ERROR , getClass().getName());
        }
        return null;
    }

}
