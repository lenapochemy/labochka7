package manager.users;

import commands.Command;
import manager.CollectionManager;
import manager.ConsoleManager;
import manager.requestManager.Response;

import java.io.Serializable;
import java.sql.SQLException;

public class Registration extends Command implements Serializable {
    private User user;

    public Registration(User user){
        super("registration", "registration new user", user);
        this.user = user;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user1){
        try {
            if (collectionManager.dataBaseHandler.registrationUser(user.getLogin(), user.getPassword())) {
                return new Response("Registration was successful");
            } else {
                return new Response("This login is already exists, try another one");
            }
        } catch (SQLException e){
            ConsoleManager.printError("problem with database");
            System.exit(-1);
            return null;
        }
    }
}
