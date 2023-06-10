package commands;

import data.StudyGroup;
import exceptions.DatabaseException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command 'add', adds a new element to the collection
 */
public class Add extends Command {

    private final StudyGroup studyGroup;

    public Add(StudyGroup studyGroup){
        super("add <study_group>", "add a new element to collection", studyGroup);
        this.studyGroup = studyGroup;
    }


    @Override
    public Response execute(CollectionManager collectionManager, User user) {
        studyGroup.setId(1);
        studyGroup.setCreationDate(collectionManager.generateCreationDate());
        try {
            collectionManager.addToCollection(studyGroup, user);
            return new Response("New Study Group added to collection!");
        } catch (DatabaseException e){
            return new Response("\u001B[31m" + "Study group was not added to collection" + "\u001B[0m");
        }
    }

}
