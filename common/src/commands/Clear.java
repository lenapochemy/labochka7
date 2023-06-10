package commands;

import exceptions.DatabaseException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command 'clear', clears the collection
 */
public class Clear extends Command{

    public Clear(){
        super("clear", "clear collection", null);

    }

    @Override
    public Response execute(CollectionManager collectionManager, User user) {
        try {
            collectionManager.clearCollection(user);
            return new Response("Your study groups was removed from collection");
        } catch (DatabaseException e){
            return new Response("\u001B[31m" + "Collection was not cleared" + "\u001B[0m");
        }
    }

}
