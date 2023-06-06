import manager.CollectionManager;
import manager.CommandManager;
import manager.FileManager;
import manager.ScannerManager;

import java.util.Collection;
import java.util.Scanner;

public class ServerConfig {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static final ScannerManager scannerManager = new ScannerManager(scanner);
    protected static final FileManager fileManager = new FileManager();
    protected static final CollectionManager collectionManager = new CollectionManager(fileManager);
    protected static final CommandManager commandManager = new CommandManager();
}
