import commands.Command;
import manager.requestManager.Request;
import manager.requestManager.Response;
import manager.requestManager.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Sender {
    private static final int SLEEP_TIME = 500;

    public static void send(Request request, SocketChannel channel, Selector selector) throws InterruptedException, IOException{
        ByteBuffer buffer = Serializer.serialize(request);
        channel.write(buffer);
        channel.register(selector, SelectionKey.OP_READ);
        Thread.sleep(SLEEP_TIME);
    }

    public static Response receive(SocketChannel channel, Selector selector, SelectionKey key) throws IOException, ClassNotFoundException{
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(socketChannel.socket().getReceiveBufferSize());
        socketChannel.read(byteBuffer);
        channel.register(selector, SelectionKey.OP_WRITE);
        return (Response) Serializer.deserialize(byteBuffer);
     }
}
