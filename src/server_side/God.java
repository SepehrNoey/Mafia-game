package server_side;

import java.util.ArrayList;
import java.util.List;

public class God {
    private Logic logic;
    private ChatRoom chatRoom;
    private List<ClientHandler> handlers;

    public God(){
        logic = new Logic();
        chatRoom = new ChatRoom();
        handlers = new ArrayList<>();
    }


}
