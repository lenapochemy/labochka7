package manager.users;

import commands.Command;
import manager.CollectionManager;
import manager.ConsoleManager;
import manager.requestManager.Response;

import java.io.Serializable;
import java.sql.SQLException;

public class Login extends Command implements Serializable {
    private User user;

    public Login(User user){
        super("login", "authorization, login to sistem", user);
        this.user = user;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user1){
        try {
            if (collectionManager.dataBaseHandler.validateUser(user.getLogin(), user.getPassword())) {
                return new Response("Authorization was successful, now you can change collection");
            } else return new Response("Incorrect login or password, try again");
        } catch (SQLException e){
            ConsoleManager.printError("problem with database");
            System.exit(-1);
            return null;
        }


    }

}
