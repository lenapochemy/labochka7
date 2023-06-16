import commands.Command;
import manager.CollectionManager;
import manager.ConsoleManager;
import manager.requestManager.Request;
import manager.requestManager.Response;
import manager.requestManager.Serializer;
import manager.users.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendThread extends Thread{
    private Request request;
    private SocketChannel socket;
    private CollectionManager collectionManager;

    public SendThread(Request request, SocketChannel socket){
        this.request = request;
        this.socket = socket;
        this.collectionManager = ServerConfig.collectionManager;
    }

    @Override
    public void run(){
        try{
            Command command = request.getCommand();
            User user = request.getUser();
            Response response = command.execute(collectionManager, user);
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            buffer.put(Serializer.serialize(response));
            buffer.flip();
            socket.write(buffer);
            buffer.clear();
            ConsoleManager.printSuccess("Sending response: " + response.getMessage());
        } catch (IOException e){
            ConsoleManager.printError("Problem in send thread");
        }
    }
}
