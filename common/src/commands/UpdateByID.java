package commands;

import data.StudyGroup;
import manager.CollectionManager;
import manager.requestManager.Response;

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
    public Response execute(CollectionManager collectionManager){
        if(collectionManager.collectionSize() == 0) return new Response("Collection is empty");
        int id = studyGroup.getId();
        StudyGroup group = collectionManager.getByID(id);
        if(group == null) return new Response("Study group with this ID is not exists");
        studyGroup.setCreationDate(group.getCreationDate());
        collectionManager.removeFromCollection(group);
        collectionManager.addToCollection(studyGroup);
        return new Response("Element from collection was updated!");

    }

}
