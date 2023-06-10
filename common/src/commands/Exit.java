package commands;

import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command 'exit', ends the program without saving collection
 */
public class Exit extends Command{
    public Exit(){
        super("exit", "finish program", null);
    }


    @Override
    public Response execute(CollectionManager collectionManager, User user){
       // collectionManager.saveCollection();
        return new Response("Program is finished!");

    }


}
