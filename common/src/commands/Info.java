package commands;
import manager.CollectionManager;
import manager.requestManager.Response;

/**
 * Command 'info', displays information about collection
 */
public class Info extends Command{

    public Info(){
        super("info", "displays information about collection", null);

    }

    @Override
    public Response execute(CollectionManager collectionManager){
        return new Response(collectionManager.showInfo());
    }


}
