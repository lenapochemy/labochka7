package commands;

import manager.CollectionManager;
import manager.requestManager.Response;

public class Connect extends Command{

    public Connect(){
        super("connect", "connecting client with server", null);
    }

    @Override
    public Response execute(CollectionManager collectionManager){
        return new Response("Connecting successful");
    }

}
