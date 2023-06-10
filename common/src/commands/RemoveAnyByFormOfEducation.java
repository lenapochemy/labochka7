package commands;

import data.FormOfEducation;
import exceptions.DatabaseException;
import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command "remove_by_any_form_of_education", deletes one element from the collection that has this form of education
 */
public class RemoveAnyByFormOfEducation extends Command{
    private FormOfEducation formOfEducation;

    public RemoveAnyByFormOfEducation(FormOfEducation formOfEducation){
        super("remove_any_by_form_of_education <form_of_education>", "remove one group from collection, which have the same form of education", formOfEducation);
        this.formOfEducation = formOfEducation;
    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){

        if(collectionManager.removeByFormOfEducation(formOfEducation, user)) {
            return new Response("Group with this form of education removed from collection!");
        } else return new Response("\u001B[31m" + "Study group was not removed to collection" + "\u001B[0m");

    }

}
