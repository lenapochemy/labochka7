package commands;

import manager.CollectionManager;
import manager.requestManager.Response;
import manager.users.User;

/**
 * Command 'print_field_descending_form_of_education", displays all forms of education from collection in descending order
 */
public class PrintFieldDescendingFormOfEducation extends Command{


    public PrintFieldDescendingFormOfEducation(){
        super("print_field_descending_form_of_education","display all forms of education from collection in descending order", null);

    }

    @Override
    public Response execute(CollectionManager collectionManager, User user){
        if(collectionManager.collectionSize() == 0) return new Response("Collection is empty");
        return new Response("Form of education: \n" + collectionManager.printFromOfEducation());
    }


}

