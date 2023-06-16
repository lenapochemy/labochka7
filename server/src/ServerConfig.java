import manager.*;
import manager.database.DataBaseHandler;
import manager.users.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ServerConfig {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static final ScannerManager scannerManager = new ScannerManager(scanner);
    //protected static final DataBaseHandler dataBaseHandler = new DataBaseHandler(URL, login, password);
    protected static final DataBaseHandler dataBaseHandler = giveDatabase();
    protected static final CollectionManager collectionManager = new CollectionManager(dataBaseHandler);
    protected static final CommandManager commandManager = new CommandManager();
    protected static final User admin = new User("admin", "admin");


    private static DataBaseHandler giveDatabase(){
        try {
            Scanner file = new Scanner(new FileReader(System.getenv("STUDY_GROUP_PATH") + "forDatabase.txt"));
            String URL = file.nextLine().trim();
            String login = file.nextLine().trim();
            String password = file.nextLine().trim();
            return new DataBaseHandler(URL, login, password);
        } catch (FileNotFoundException e){
            ConsoleManager.printError("Not found file 'forDatabase', so no connecting with database");
            System.exit(-1);
            return null;
        }
    }
}
