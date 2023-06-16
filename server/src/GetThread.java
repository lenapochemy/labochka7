import manager.CollectionManager;
import manager.ConsoleManager;
import manager.database.DataBaseHandler;
import manager.requestManager.Request;
import manager.requestManager.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class GetThread extends Thread{

    private SocketChannel socket;
    private DataBaseHandler dataBaseHandler;


    public GetThread(SocketChannel socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        try{
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            socket.read(buffer);
            Request request = null;
            try{
                request = (Request) Serializer.deserialize(buffer);
            } catch (ClassNotFoundException e){
            }
            ConsoleManager.printInfo("Receive command: " + request.getCommand().getName());
            SendThread sendThread = new SendThread(request, socket);
            sendThread.start();
        } catch (IOException e){
            //ConsoleManager.printError("Problem in get thread");
        }
    }
}
