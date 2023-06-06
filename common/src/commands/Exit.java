package commands;

import manager.CollectionManager;
import manager.requestManager.Response;

/**
 * Command 'exit', ends the program without saving collection
 */
public class Exit extends Command{
    public Exit(){
        super("exit", "finish program", null);
    }


    @Override
    public Response execute(CollectionManager collectionManager){
        collectionManager.saveCollection();
        return new Response("Program is finished!");

    }


}
