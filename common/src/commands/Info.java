package commands;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command 'info', displays information about collection
 */
public class Info extends Command{

    public Info(){
        super("info", "displays information about collection", null);

    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){
        return new Response(collectionManager.showInfo());
    }


}
