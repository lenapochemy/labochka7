import commands.Command;
import manager.requestManager.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Sender {

    public static Command getCommand(SocketChannel in) throws IOException, ClassNotFoundException{
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        in.read(byteBuffer);
        return Serializer.deserializeCommand(byteBuffer);
    }
}
