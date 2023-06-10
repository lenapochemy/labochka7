package commands;

import exceptions.DatabaseException;
import exceptions.NullCollectionException;
import exceptions.NullException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "remove_lower", deletes elements from collection with less students
 */
public class RemoveLower extends Command{
    private Integer count;
    public RemoveLower(Integer count){
        super("remove_lower <count>", "remove all groups from collection that have less students", count);
        this.count = count;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){
        try {
            int number = collectionManager.removeLower(count, user);
            return new Response(number + " smaller groups removed from collection");
        } catch (NullCollectionException e){
            return new Response("Collection is empty");
        //} catch (DatabaseException e){
          //  return new Response("\u001B[31m" + "Study groups was not removed from collection" + "\u001B[0m");
        } catch (NullException e){
            return new Response("Smaller groups are not exists");
        }
    }

}
