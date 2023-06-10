package manager.users;

import commands.Command;
import manager.CollectionManager;
import manager.requestManager.Response;

public class Logout extends Command {
    //private User user;

    public Logout(){
        super("logout", "logout from sistem", null);
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){
        return new Response("incorrect using command logout");
    }
}
