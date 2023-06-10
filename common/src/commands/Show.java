package commands;

import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "show", displays all elements from collection
 */
public class Show extends Command{

    public Show(){
        super("show", "display all elements from collection", null);
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user) {
        if (collectionManager.collectionSize() == 0) return new Response("Collection is empty");
        String message = collectionManager.printCollection();
        return new Response(message.substring(0, message.length()-2));
    }
}
