import commands.Command;
import exceptions.*;
import manager.*;
import manager.requestManager.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Client {


    private static final CommandManager commandManager = new CommandManager();
    private static Selector selector;
    private static Scanner scanner = new Scanner(System.in);
    private static ScannerManager scannerManager = new ScannerManager(scanner);


    public static void main(String[] args){
        int port = scannerManager.sayPort();
        String host = scannerManager.sayHost();
        InetSocketAddress hostAddress = new InetSocketAddress(host, port);
        try{
            selector = Selector.open();
            ConsoleManager.printInfo("Try connecting to server");
            SocketChannel client = SocketChannel.open(hostAddress);
            ConsoleManager.printSuccess("Connecting successful");
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE);
            selectorLoop(client, scanner);

        } catch (IOException e){
            ConsoleManager.printError("Server is dead, try restart program later");
        } catch (ClassNotFoundException e){
            ConsoleManager.printError("Class is not found");
        } catch (InterruptedException e){
            ConsoleManager.printError("Thread was interrupt while sleeping. Restart client");
        }
    }

    private static void selectorLoop(SocketChannel channel, Scanner scanner) throws IOException, ClassNotFoundException, InterruptedException {
        while (true){
            selector.select();
            if(!iteratorLoop(channel, scanner)){
                break;
            }
        }
    }

    private static boolean iteratorLoop(SocketChannel channel, Scanner scanner) throws IOException, ClassNotFoundException, InterruptedException{
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();

        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();

            if(key.isReadable()){
                Response response = Sender.receive(channel, selector, key);
                ConsoleManager.printSuccess(response.getMessage());
            } else if(key.isWritable()){

                String input = scanner.nextLine().trim();

                        List<String> splitLine = commandManager.stringSplit(input);
                        if (splitLine.get(0).equals("execute_script") && splitLine.size() == 2) {
                            String fileName = splitLine.get(1);
                            scriptReader(channel, fileName, commandManager, key);
                        } else {
                            commandHandler(channel, input);
                        }
            }
        }
        return true;
    }


    private static void commandHandler(SocketChannel channel, String input){
        Command command;
        try {
            command = commandManager.buildCommand(input, scannerManager);
        } catch (IncorrectScriptException e){
            command = null;
        } catch (CommandException e){
            ConsoleManager.printError("This is not a command, print 'help'");
            command = null;
        }
        try {
            if (command != null) {
                Sender.send(command, channel, selector);
                ConsoleManager.printInfo("Sending command: " + command.getName());
            }
        } catch (IOException e){
            ConsoleManager.printError("Problem with sending command");
        } catch (InterruptedException e){
            ConsoleManager.printError("interrupted");
        }
    }


    private static void scriptReader(SocketChannel channel, String fileName, CommandManager commandManager, SelectionKey key){
        try {
            HashSet<String> scriptCollection = new HashSet<>();
            scriptCollection.add(fileName);
            String path = System.getenv("STUDY_GROUP_PATH") + fileName;
            File file = new File(path);
            if(file.exists() && !file.canRead()) throw new FileException();
            Scanner scriptScan = new Scanner(file);

            Scanner scannerOld = scannerManager.getScanner();
            scannerManager.setScanner(scriptScan);
            scannerManager.setFileMode();

            while (scriptScan.hasNext()) {

                String input = scriptScan.nextLine().trim();
                System.out.println(input);

                if (input.equals("exit")) {
                    ConsoleManager.printSuccess("Program is finished");
                    System.exit(0);
                }
                try {
                    List<String> splitLine = commandManager.stringSplit(input);
                    if (splitLine.get(0).equals("execute_script") && splitLine.size() == 2) {
                        String newFileName = splitLine.get(1);
                        for (String script : scriptCollection) {
                            if (script.equals(newFileName)) throw new RecurentScriptException();
                        }
                        scriptCollection.add(newFileName);

                        scriptReader(channel, newFileName, commandManager, key);
                    }

                    commandHandler(channel, input);

                } catch (RecurentScriptException e) {
                    ConsoleManager.printError("Recurse in script");

                }
                try {
                    Response response = Sender.receive(channel, selector, key);
                    ConsoleManager.printSuccess(response.getMessage());
                }catch (IOException e){
                    //ConsoleManager.printError("ioioio");
                } catch (ClassNotFoundException e){
                    //ConsoleManager.printError("classclass");
                }
            }
            scannerManager.setUserMode();
            scannerManager.setScanner(scannerOld);

        } catch (FileNotFoundException e){
            ConsoleManager.printError("File is not found");
        } catch (FileException e){
            ConsoleManager.printError("No commands in file");
        }
    }


}
