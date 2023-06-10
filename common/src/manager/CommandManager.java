package manager;

import commands.*;
import data.FormOfEducation;
import data.StudyGroup;
import exceptions.*;
import manager.users.Login;
import manager.users.Logout;
import manager.users.Registration;
import manager.users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {


    public List<String> stringSplit(String line){
        List<String> splitted = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean screening = false;
        for(char ch : line.toCharArray()){
            if(ch == ' ' && !screening){
                splitted.add(current.toString());
                current.setLength(0);
            } else if(ch == '"'){
                screening = !screening;
            } else {
                current.append(ch);
            }
        }
        splitted.add(current.toString());
        return splitted;
    }

    public String getCommandName(List<String> line){
        return line.get(0);
    }
    public List<String> getCommandArgs(List<String> line){
        line.remove(0);
        return line;
    }


    public Command buildCommand(String input, ScannerManager scannerManager, boolean authorized) throws IncorrectScriptException, CommandException, AuthorizedException {
        //try {
        Command command = initCommand(input, scannerManager, authorized);
        if (command == null) {
            throw new CommandException();
        }
        return command;
    }

    public Command initCommand(String line, ScannerManager scannerManager, boolean authorized) throws IncorrectScriptException, CommandException, AuthorizedException{
        List<String> commandsWithArgs = stringSplit(line);
        String commandName = getCommandName(commandsWithArgs);
        List<String> commandArgs = getCommandArgs(commandsWithArgs);
        return createCommand(commandName, commandArgs, scannerManager, authorized);
    }
    public Command createCommand(String name, List<String> args, ScannerManager scannerManager, boolean authorized) throws IncorrectScriptException, CommandException, AuthorizedException{
        int id;
        try{
            switch (name){
                case "add" -> {
                    if(authorized) {
                        if (!args.isEmpty()) {
                            throw new ArgumentException();
                        }
                        StudyGroup studyGroup = sayStudyGroup(scannerManager);
                        return new Add(studyGroup);
                    } else throw new AuthorizedException();
                }
                case "add_if_max" -> {
                    if(authorized) {
                        if (!args.isEmpty()) {
                            throw new ArgumentException();
                        }
                        StudyGroup studyGroup = sayStudyGroup(scannerManager);
                        return new AddIfMax(studyGroup);
                    } else throw new AuthorizedException();
                }
                case "clear" -> {
                    if(authorized) {
                        if (!args.isEmpty()) {
                            throw new ArgumentException();
                        }
                        return new Clear();
                    } else throw new AuthorizedException();
                }
                case "exit" -> {
                    if(!args.isEmpty()) {
                        throw new ArgumentException();
                    }
                    return new Exit();
                }
                case "filter_greater_than_group_admin" -> {
                    if(args.size() != 1){
                        throw new ArgumentException();
                    }
                    Integer height = Integer.parseInt(args.get(0));
                    return new FilterGreaterThanGroupAdmin(height);
                }
                case "help" -> {
                    if (!args.isEmpty()) {
                        throw new ArgumentException();
                    }
                    return new Help();
                }
                case "info" -> {
                    if(!args.isEmpty()){
                        throw new ArgumentException();
                    }
                    return new Info();
                }
                case "print_field_descending_form_of_education" -> {
                    if(!args.isEmpty()) {
                        throw new ArgumentException();
                    }
                    return new PrintFieldDescendingFormOfEducation();
                }
                case "remove_any_by_form_of_education" -> {
                    if(authorized) {
                        if (args.size() != 1) {
                            throw new ArgumentException();
                        }
                        FormOfEducation formOfEducation = FormOfEducation.valueOf(args.get(0));
                        return new RemoveAnyByFormOfEducation(formOfEducation);
                    } else throw new AuthorizedException();
                }

                case "remove_by_id" -> {
                    if(authorized) {
                        if (args.size() != 1) {
                            throw new ArgumentException();
                        }
                        id = Integer.parseInt(args.get(0));
                        return new RemoveByID(id);
                    } else throw new AuthorizedException();
                }
                case "remove_greater" -> {
                    if(authorized) {
                        if (args.size() != 1) {
                            throw new ArgumentException();
                        }
                        Integer count = Integer.parseInt(args.get(0));
                        return new RemoveGreater(count);
                    } else throw new AuthorizedException();
                }
                case "remove_lower" -> {
                    if(authorized) {
                        if (args.size() != 1) {
                            throw new ArgumentException();
                        }
                        Integer count = Integer.parseInt(args.get(0));
                        return new RemoveLower(count);
                    } else throw new AuthorizedException();
                }
                case "show" -> {
                    if(!args.isEmpty()) {
                        throw new ArgumentException();
                    }
                    return new Show();
                }
                case "update_by_id" ->{
                    if(authorized) {
                        if (args.size() != 1) {
                            throw new ArgumentException();
                        }
                        id = Integer.parseInt(args.get(0));
                        StudyGroup studyGroup = sayStudyGroup(scannerManager);
                        studyGroup.setId(id);
                        return new UpdateByID(studyGroup);
                    } else throw new AuthorizedException();
                }
                /*case "reg" ->{
                    if(!args.isEmpty()) throw new ArgumentException();
                    User user = new User(scannerManager.sayLogin(), scannerManager.sayPassword());
                    return new Registration(user);
                }
                case "login" ->{
                    if(!args.isEmpty()) throw new ArgumentException();
                    User user = new User(scannerManager.sayLogin(), scannerManager.sayPassword());
                    return new Login(user);
                }
                case "logout" -> {
                    if (!args.isEmpty()) throw new ArgumentException();
                    return new Logout();
                }

                 */
                default -> throw new CommandException();
            }
        } catch (ArgumentException e){
            ConsoleManager.printError("Incorrect command");
            return null;
       /* } catch (IncorrectValueException e) {
            ConsoleManager.printError("Incorrect command's argument");
            return null;
        */
        } catch (IncorrectGroupValueException e){
            return null;
        //} catch (AuthorizedException e){
          //  ConsoleManager.printError("You can't modification collection without authorization");
            //return null;
        } catch (IllegalArgumentException e){
            ConsoleManager.printError("Illegal enum value");
            return null;
        }
    }

    private StudyGroup sayStudyGroup(ScannerManager scannerManager) throws IncorrectScriptException ,IncorrectGroupValueException{
        return new StudyGroup(1, scannerManager.sayGroupName(), scannerManager.sayCoordinates(),
                LocalDate.now(), scannerManager.sayStudentsCount(), scannerManager.sayFormOfEducation(),
                scannerManager.saySemesterEnum(), scannerManager.sayPerson());
    }


}
