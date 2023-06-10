package manager.requestManager;

import commands.Command;
import manager.users.User;

import java.io.Serializable;

public class Request implements Serializable{
    private Command command;
    private User user;

    public Request(Command command, User user){
        this.user = user;
        this.command = command;
    }

    public Command getCommand(){
        return command;
    }

    public User getUser() {
        return user;
    }
}
