package manager.requestManager;

import commands.Command;

import java.io.*;
import java.nio.ByteBuffer;

public class Serializer {


    public static Command deserializeCommand(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Command command = (Command) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return command;
    }

    public static ByteBuffer serializeResponse(Response response) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(response);
        objectOutputStream.flush();
        objectOutputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer;
    }

    public static Response deserializeResponse(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Response response = (Response) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return response;
    }

    public static ByteBuffer serializeCommand(Object o) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
        objectOutputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer;
    }


}

