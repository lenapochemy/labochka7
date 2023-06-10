package commands;

import exceptions.DatabaseException;
import exceptions.NullCollectionException;
import exceptions.NullException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "remove_greater", deletes elements from collection with more students
 */
public class RemoveGreater extends Command{
    private Integer count;
    public RemoveGreater(Integer count){
        super("remove_greater <count>", "remove all groups from collection that have more students", count);
        this.count = count;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user) {
        try {
            int number = collectionManager.removeGreater(count, user);
            return new Response(number + " bigger groups removed from collection!");
        } catch (NullCollectionException e){
            return new Response("Collection is empty");
        //} catch (DatabaseException e){
          //  return new Response("\u001B[31m" + "Study groups was not removed to collection" + "\u001B[0m");
        } catch (NullException e){
            return new Response("Bigger groups are not exists");
        }
    }

}
