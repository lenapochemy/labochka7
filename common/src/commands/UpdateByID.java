package commands;

import data.StudyGroup;
import exceptions.DatabaseException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "update_by_id", updates one element from collection by id
 */
public class UpdateByID extends Command{

    private StudyGroup studyGroup;

    public UpdateByID(StudyGroup studyGroup){
        super("update_by_id <id, study_group>", "update element from collection by id", studyGroup);
        this.studyGroup = studyGroup;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){
        if(collectionManager.collectionSize() == 0) return new Response("Collection is empty");
        int id = studyGroup.getId();
        StudyGroup group = collectionManager.getByID(id);
        if(group == null) return new Response("Study group with this ID is not exists");
        studyGroup.setCreationDate(group.getCreationDate());
        try {
            collectionManager.updateByID(group, studyGroup, user);
            return new Response("Element from collection was updated!");
        } catch (DatabaseException e){
            return new Response("\u001B[31m" + "Study group was not updated in collection" + "\u001B[0m");
        }
    }

}
