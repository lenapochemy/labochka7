import commands.Command;
import exceptions.*;
import manager.*;
import manager.requestManager.Request;
import manager.requestManager.Response;
import manager.users.Login;
import manager.users.Registration;
import manager.users.User;

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
    private static int port = scannerManager.sayPort();
    private static String host = scannerManager.sayHost();
    private static boolean authorized = false;
    private static User clientUser = null;


    public static void main(String[] args){

        InetSocketAddress hostAddress = new InetSocketAddress(host, port);
        try{
            selector = Selector.open();
            ConsoleManager.printInfo("Try connecting to server");
            SocketChannel client = SocketChannel.open(hostAddress);
            ConsoleManager.printSuccess("Connecting successful");
            ConsoleManager.printInfo("You need to login to modify the collection");
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE);
            selectorLoop(client, scanner);

        } catch (IOException e){
            ConsoleManager.printError("Server is dead");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1){
            }
            main(args);
        } catch (ClassNotFoundException e){
            ConsoleManager.printError("Class is not found");
        } catch (InterruptedException e){
            ConsoleManager.printError("Thread was interrupt while sleeping. Restart client");
        } catch (ConnectingException e){
            main(args);
        }
    }

    private static void selectorLoop(SocketChannel channel, Scanner scanner) throws IOException, ClassNotFoundException, InterruptedException, ConnectingException {
        while (true){
            selector.select();
            if(!iteratorLoop(channel, scanner)){
                break;
            }
        }
    }

    private static boolean iteratorLoop(SocketChannel channel, Scanner scanner) throws IOException, ClassNotFoundException, ConnectingException{
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();

        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();

            if(key.isWritable()){
                String input = scanner.nextLine().trim();
                List<String> splitLine = commandManager.stringSplit(input);
                if(splitLine.get(0).equals("login")){
                    login(channel, key);
                } else if(splitLine.get(0).equals("reg")){
                    registration(channel, key);
                } else if(splitLine.get(0).equals("logout")){
                    logout();
                } else {

                    if (splitLine.get(0).equals("execute_script") && splitLine.size() == 2) {
                        String fileName = splitLine.get(1);
                        scriptReader(channel, fileName, commandManager, key);
                    } else {
                        commandHandler(channel, input);
                    }
                }
            } else {
                if (key.isReadable()) {
                    Response response = getResponse(channel, key);
                    ConsoleManager.printSuccess(response.getMessage());
                }
            }

        }
        return true;
    }

    private static void registration(SocketChannel channel, SelectionKey key) throws IOException{
        if(!authorized) {
            User user = new User(scannerManager.sayLogin(), scannerManager.sayPassword());
            Command registration = new Registration(user);
            sendCommand(registration, user, channel);

            Response response = getResponse(channel, key);

            if (response.getMessage().equals("Registration was successful")) {
                ConsoleManager.printSuccess(response.getMessage());
                //authorization();
            } else {
                ConsoleManager.printError(response.getMessage());
                registration(channel, key);
            }
        } else ConsoleManager.printError("You are already authorized");
    }

    private static void login(SocketChannel channel, SelectionKey key) throws IOException{
        if(!authorized) {
            User user = new User(scannerManager.sayLogin(), scannerManager.sayPassword());
            Command login = new Login(user);
            sendCommand(login, user, channel);

            Response response = getResponse(channel, key);
            if (response.getMessage().equals("Authorization was successful, now you can change collection")) {
                ConsoleManager.printSuccess(response.getMessage());
                authorized = true;
                clientUser = user;
            } else {
                ConsoleManager.printError(response.getMessage());
            }
        } else {
            ConsoleManager.printError("You are already authorized");
        }
    }

    private static void logout(){
        if(authorized) {
            authorized = false;
            clientUser = null;
            ConsoleManager.printSuccess("logout was successful, now you can't change the collection");
        } else ConsoleManager.printError("You are not authorized");
    }



    private static void sendCommand(Command command, User user, SocketChannel channel) throws IOException{
        try {
            Sender.send(new Request(command, user), channel, selector);
            ConsoleManager.printInfo("Sending command: " + command.getName());
        } catch (InterruptedException e){
            ConsoleManager.printError("interrupted exception");
        }
    }

    private static Response getResponse(SocketChannel channel, SelectionKey key) throws IOException{
        try {
            return Sender.receive(channel, selector, key);
        } catch (ClassNotFoundException e){
            return null;
        }
    }


    private static void commandHandler(SocketChannel channel, String input) throws ConnectingException{
        Command command;
        try {
            command = commandManager.buildCommand(input, scannerManager, authorized);
        } catch (IncorrectScriptException e){
            command = null;
        } catch (CommandException e){
            ConsoleManager.printError("This is not a command, print 'help'");
            command = null;
        } catch (AuthorizedException e){
            ConsoleManager.printError("You can't modification collection without authorization");
            command = null;
        }
        try {
            if (command != null) {
                sendCommand(command, clientUser, channel);
            }
        } catch (IOException e){
            ConsoleManager.printError("Problem with sending command");
            throw new ConnectingException();
        }
    }


    private static void scriptReader(SocketChannel channel, String fileName, CommandManager commandManager, SelectionKey key) throws ConnectingException{
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
                    Response response = getResponse(channel, key);
                    ConsoleManager.printSuccess(response.getMessage());
                }catch (IOException e){
                    //ConsoleManager.printError("ioioio");
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
