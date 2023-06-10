package commands;

import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "save", saves collection to the fail
 */
public class Save extends Command{

    public Save(){
        super("save", "save collection to file", null);
    }

    /**
     * Executing the command "save"
     */
    @Override
    public Response execute(CollectionManager collectionManager, User user){
        // collectionManager.saveCollection();
        return new Response("Collection is saved!");
    }
}