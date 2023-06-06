import commands.Command;
import manager.*;
import manager.requestManager.Response;
import manager.requestManager.Serializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Server {

    private static Selector selector;

    public static void main(String[] args){

        try {
            startServer();
            selector = Selector.open();
            ServerSocketChannel server = openChannel(selector);

            ConsoleThread consoleThread = new ConsoleThread();
            ConsoleManager.printInfo("Print command (maybe 'help')");
            consoleThread.start();
            selectorLoop(server);

            consoleThread.shutdown();
        } catch (IOException e){
            ConsoleManager.printError("IO probblem");
        } catch (ClassNotFoundException e){
            ConsoleManager.printError("class not found");
        } catch (InterruptedException e){
            ConsoleManager.printError("interrupted");
        }
    }

    private static void startServer(){
        String resp;
        if(ServerConfig.fileManager.isFileEmpty()){
            ServerConfig.collectionManager.createCollection();
            resp = "File 'study_groups.json' is not found or empty, so collection is empty";
        } else{
            ServerConfig.collectionManager.readFromFile();
            resp = "Collection is filled from file 'study_groups.json'!";
        }
        ConsoleManager.printInfo(resp);

    }

    private static ServerSocketChannel openChannel(Selector selector) throws IOException{
        ServerSocketChannel server = ServerSocketChannel.open();
        int port = ServerConfig.scannerManager.sayPort();
        server.socket().bind(new InetSocketAddress(port));
        ConsoleManager.printSuccess("Server is started with port " + port + "!");
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        return server;
    }

    private static void selectorLoop(ServerSocketChannel channel) throws ClassNotFoundException, InterruptedException{
        while (channel.isOpen()){
            try {
                selector.select();

            } catch (IOException e){
                ConsoleManager.printError("io io io io io ");
            }
                iteratorLoop(channel);

        }
    }

    private static void iteratorLoop(ServerSocketChannel channel) throws ClassNotFoundException{
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();

        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();

            try {
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    Command command = Sender.getCommand(socketChannel);
                    ConsoleManager.printInfo("Receive command: " + command.getName());

                    Response response = command.execute(ServerConfig.collectionManager);
                    ByteBuffer buffer = Serializer.serializeResponse(response);
                    socketChannel.write(buffer);
                    ConsoleManager.printSuccess("Sending response: " + response.getMessage());
                }
            }catch (IOException e){
               // ConsoleManager.printError("ochen' ploho");
            }
        }
    }


}
