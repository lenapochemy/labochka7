package commands;

import exceptions.DatabaseException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "remove_by_id", deletes one element from the collection with this id
 */
public class RemoveByID extends Command{

    private int id;
    public RemoveByID(int id){
        super("remove_by_id <id>", "remove element from collection by id", id);
        this.id = id;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){
        try {
            if (collectionManager.removeByID(id, user)) return new Response("Element removed from collection!");
            else return new Response("Study group with this ID is not exists");
        } catch (DatabaseException e){
            return new Response("\u001B[31m" + "Study group was not removed from collection, because is not your group" + "\u001B[0m");
        }
    }

}
