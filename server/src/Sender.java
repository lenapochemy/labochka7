import commands.Command;
import manager.requestManager.Request;
import manager.requestManager.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Sender {

    public static Request getRequest(SocketChannel in) throws IOException, ClassNotFoundException{
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        in.read(byteBuffer);
        return (Request) Serializer.deserialize(byteBuffer);
    }
}
