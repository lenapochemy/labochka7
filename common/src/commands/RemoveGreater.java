package commands;

import exceptions.NullCollectionException;
import manager.CollectionManager;
import manager.requestManager.Response;

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
    public Response execute(CollectionManager collectionManager) {
        try {
            collectionManager.removeGreater(count);
            return new Response("Bigger groups removed from collection!");
        } catch (NullCollectionException e){
            return new Response("Collection is empty");
        }
    }

}
