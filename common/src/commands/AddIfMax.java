package commands;

import data.StudyGroup;
import exceptions.DatabaseException;
import manager.*;
import manager.requestManager.*;
import manager.users.User;

/**
 * Command 'add_if_max', adds a new element to the collection if his students count is more than other groups in collection
 */
public class AddIfMax extends Command{
    private final StudyGroup studyGroup;

    public AddIfMax(StudyGroup studyGroup){
        super("add_if_max <study_group>",
                "add new element to collection, if this students count is more than max students count in collection", studyGroup);
        this.studyGroup = studyGroup;
    }


    @Override
    public Response execute(CollectionManager collectionManager, User user) {
        studyGroup.setId(1);
        studyGroup.setCreationDate(collectionManager.generateCreationDate());
        Integer max = collectionManager.getMaxGroup();
        try {
            if (studyGroup.getStudentsCount() > max) {
                collectionManager.addToCollection(studyGroup, user);
                return new Response("Study Group added to collection");
            } else {
                return new Response("Study Group s not added to collection, because this group is not max");
            }
        } catch (DatabaseException e){
            return new Response("\u001B[31m" + "Study group was not added to collection" + "\u001B[0m");
        }
    }


}
