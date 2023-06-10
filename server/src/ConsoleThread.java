import commands.Command;
import commands.Exit;
import commands.Save;
import exceptions.*;
import manager.CommandManager;
import manager.ConsoleManager;
import manager.requestManager.Response;
import manager.users.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ConsoleThread extends Thread{

    private volatile boolean running = true;
    //User user = ServerConfig.admin;
    @Override
    public void run(){
        while (running){
            String input = ServerConfig.scanner.nextLine();
            Command command;
            try {
                if(input.equals("save")){
                   command = new Save();
                } else if(input.equals("exit")){
                    ConsoleManager.printSuccess("Program is finished");
                    command = new Exit();
                    System.exit(0);
                } else {
                    List<String> splitLine = ServerConfig.commandManager.stringSplit(input);
                    if (splitLine.get(0).equals("execute_script") && splitLine.size() == 2) {
                        String fileName = splitLine.get(1);
                        scriptReader(fileName, ServerConfig.commandManager);

                        command = null;
                    } else {
                        command = ServerConfig.commandManager.buildCommand(input, ServerConfig.scannerManager, true);
                    }
                }
            } catch (CommandException e){
                ConsoleManager.printError("This is not a command, print 'help'");
                command = null;
            } catch (IncorrectScriptException e){
                ConsoleManager.printError("Incorrect script");
                command = null;
            } catch (AuthorizedException e){
                command = null;
            }

            if(command != null) {
                Response resp = command.execute(ServerConfig.collectionManager, ServerConfig.admin);
                ConsoleManager.printSuccess(resp.getMessage());
            }
        }

    }

    public void shutdown(){
        this.running = false;
    }

    private void scriptReader(String fileName, CommandManager commandManager){
        try {
            HashSet<String> scriptCollection = new HashSet<>();
            scriptCollection.add(fileName);
            String path = System.getenv("STUDY_GROUP_PATH") + fileName;
            File file = new File(path);
            if(file.exists() && !file.canRead()) throw new FileException();
            Scanner scriptScan = new Scanner(file);

            Scanner scannerOld = ServerConfig.scannerManager.getScanner();
            ServerConfig.scannerManager.setScanner(scriptScan);
            ServerConfig.scannerManager.setFileMode();
            while (scriptScan.hasNext()) {
                String input = scriptScan.nextLine().trim();
                System.out.println(input);

                if(input.equals("exit")){
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
                        scriptReader(newFileName, commandManager);
                    } else {
                        Command command = commandManager.buildCommand(input, ServerConfig.scannerManager, true);
                        if (command != null) {
                            Response resp = command.execute(ServerConfig.collectionManager, ServerConfig.admin);
                            ConsoleManager.printSuccess(resp.getMessage());
                        }
                        //Response commandResp = start(command);
                        //ConsoleManager.printSuccess(commandResp.getMessage());
                    }
                }catch (RecurentScriptException e){
                    ConsoleManager.printError("Recurse in script");
                } catch (IncorrectScriptException e){
                    ConsoleManager.printError("Script is incorrect");
                } catch (CommandException e){
                    ConsoleManager.printError("Incorrect command in script");
                } catch (AuthorizedException e){

                }

            }

            ServerConfig.scannerManager.setUserMode();
            ServerConfig.scannerManager.setScanner(scannerOld);
        } catch (FileNotFoundException e){
            ConsoleManager.printError("File is not found");
        } catch (FileException e){
            ConsoleManager.printError("No commands in file");
        }

    }

}
