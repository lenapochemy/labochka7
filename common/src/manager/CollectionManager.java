package manager;

import data.FormOfEducation;
import data.StudyGroup;
import exceptions.*;
import manager.database.DataBaseHandler;
import manager.users.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class responsible for working with the collection
 */
public class CollectionManager {

    public DataBaseHandler dataBaseHandler;

    /**
     * Collection of groups
     */
    private HashSet<StudyGroup> studyGroupCollection = new HashSet<>();
    /**
     * Collection id values
     */
    private Set<Integer> idCollection = new HashSet<>();
    private int newId = 1;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Time when the collection was last modified
     */
    private LocalDate lastInitDate;
    /**
     * The time when the collection was last saved to a file
     */
    private LocalDate lastSaveDate;

    public CollectionManager(DataBaseHandler dataBaseHandler){
        this.lastInitDate = null;
        this.lastSaveDate = null;
        this.dataBaseHandler = dataBaseHandler;
    }

    /**
     * Method creates an empty collection
     */
    public void createCollection(){
        this.studyGroupCollection = new HashSet<>();
    }


    /**
     * Method counts the number of elements in the collection
     * @return elements count
     */
    public int collectionSize(){
        try {
            lock.readLock().lock();
            try {
                if (studyGroupCollection == null) throw new NullCollectionException();
                return studyGroupCollection.size();
            } catch (NullCollectionException e) {
                return 0;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void loadFromDatabase(){
        this.studyGroupCollection = dataBaseHandler.loadCollectionFromDatabase();
        ConsoleManager.printInfo("Collection was filled from database, groups count: "+ collectionSize());
    }

   /* /**
     * Method generates a new unique id for the group
     * @return group's id
     */
   /* public int generateId(){
        while(!idCollection.add(newId)){
            newId++;
        }
        return newId;
    }

    */

    /**
     * Method adds a new element to the collection
     * @param studyGroup new element for collection
     */
    public void addToCollection(StudyGroup studyGroup, User user) throws DatabaseException {
        try {
            lock.writeLock().lock();
            int id = dataBaseHandler.addStudyGroup(studyGroup, user);
            if (id != 0) {
                studyGroup.setId(id);
                studyGroupCollection.add(studyGroup);
                lastInitDate = LocalDate.now();
                lastSaveDate = LocalDate.now();
            } else throw new DatabaseException();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addToCollectionWithId(StudyGroup studyGroup, User user) throws DatabaseException{
        if(dataBaseHandler.addStudyGroupWithId(studyGroup, user)){
            studyGroupCollection.add(studyGroup);
            lastInitDate = LocalDate.now();
            lastSaveDate = LocalDate.now();
        } else throw new DatabaseException();
    }

    /**
     * Method clears the collection
     */
    public void clearCollection(User user) throws DatabaseException{
        try {
            lock.writeLock().lock();
            if (user.getLogin().equals("admin")) {
                if (dataBaseHandler.clearCollectionAdmin()) {
                    studyGroupCollection.clear();
                    idCollection.clear();
                } else throw new DatabaseException();
            } else {
                this.studyGroupCollection = dataBaseHandler.clearCollection(user);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Method finds the group with the maximum number of students in the collection
     * @return max students count
     */
    public Integer getMaxGroup(){
        try {
            lock.readLock().lock();
            Integer max = 0;
            for (StudyGroup group : studyGroupCollection) {
                if (group.getStudentsCount() > max) max = group.getStudentsCount();
            }
            return max;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Method finds a group with the given id
     * @param id id of the desired group
     * @return desired group
     */
    public StudyGroup getByID(int id){
        try {
            lock.readLock().lock();
            for (StudyGroup group : studyGroupCollection) {
                if (group.getId() == id) return group;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateByID(StudyGroup oldGroup, StudyGroup newGroup, User user) throws DatabaseException{
        try {
            lock.writeLock().lock();
            removeFromCollection(oldGroup, user);
            addToCollectionWithId(newGroup, user);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Method deletes group from the collection
     * @param studyGroup the group to delete
     */
    public void removeFromCollection(StudyGroup studyGroup, User user) throws DatabaseException{
        if(dataBaseHandler.removeGroup(studyGroup, user)) {
            idCollection.remove(studyGroup.getId());
            studyGroupCollection.remove(studyGroup);
        } else throw new DatabaseException();
    }

    /**
     * Method removes a group by the id from the collection
     * @param id id of the group to delete
     */
    public boolean removeByID(int id, User user) throws DatabaseException{
        try {
            lock.writeLock().lock();
            StudyGroup studyGroup = getByID(id);
            try {
                if (studyGroup == null) throw new NullException();
                removeFromCollection(studyGroup, user);
                return true;
            } catch (NullException e) {
                ConsoleManager.printError("Study group with this ID is not exists");
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Method removes groups with a larger number of students from the collection
     * @param count max number of students in groups that remain in the collection
     */
    public int removeGreater(Integer count, User user) throws NullCollectionException, NullException{
            if (studyGroupCollection.isEmpty()) throw new NullCollectionException();
            HashSet<Integer> idSet = new HashSet<>();
            for (StudyGroup group : studyGroupCollection) {
                if (group.getStudentsCount() > count) {
                    idSet.add(group.getId());
                }
            }

            if (idSet.isEmpty()) throw new NullException();
            int number = idSet.size();
            for (Integer id : idSet) {
                try {
                    removeByID(id, user);
                } catch (DatabaseException e) {
                    number--;
                }
            }
            return number;
    }


    /**
     * Method removes groups with a smaller number of students from the collection
     * @param count min number of students in groups that remain in the collection
     */
    public int removeLower(Integer count, User user) throws NullCollectionException, NullException{
            if (studyGroupCollection.isEmpty()) throw new NullCollectionException();
            HashSet<Integer> idSet = new HashSet<>();
            for (StudyGroup group : studyGroupCollection) {
                if (group.getStudentsCount() < count) {
                    idSet.add(group.getId());
                }
            }
            if (idSet.isEmpty()) throw new NullException();
            int number = idSet.size();
            for (Integer id : idSet) {
                try {
                    removeByID(id, user);
                } catch (DatabaseException e) {
                    number--;
                }
            }
            return number;
    }

    /**
     * Method removes a group with this form of education from the collection
     * @param formOfEducation form of education
     */
    public boolean removeByFormOfEducation(FormOfEducation formOfEducation, User user)  {
        try {
            lock.readLock().lock();
            try {
                for (StudyGroup group : studyGroupCollection) {
                    if (group.getFormOfEducation().equals(formOfEducation) && dataBaseHandler.isOwner(group.getId(), user.getLogin())) {
                        removeFromCollection(group, user);
                        return true;
                        //break;
                    }
                }
                return false;
            } catch (SQLException e) {
                ConsoleManager.printError("problem with data base");
                return false;
            } catch (DatabaseException e) {
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Method displays all elements from collection
     */
    public String printCollection(){
        try {
            lock.readLock().lock();
            StringBuilder print = new StringBuilder();
            for (StudyGroup group : studyGroupCollection) {
                print.append(group.toString() + "\n");
            }
            return print.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    public String showInfo() {
        try {
            lock.readLock().lock();
            LocalDate lastInitDate = getLastInitDate();
            String sLastInitDate;
            if (lastInitDate == null) sLastInitDate = "No command in this session";
            else sLastInitDate = lastInitDate.toString();
            LocalDate lastSaveDate = getLastSaveDate();
            String sLastSaveDate;
            if (lastSaveDate == null) sLastSaveDate = "No saved in this session";
            else sLastSaveDate = lastSaveDate.toString();
            String info = "Collection info:\n" +
                    "  Type: " + collectionType() + "\n" +
                    "  Last save: " + sLastSaveDate + "\n" +
                    "  Last init: " + sLastInitDate + "\n" +
                    "  Number of elements: " + collectionSize();
            return info;
        } finally {
            lock.readLock().unlock();
        }
    }
    /**
     * Method displays groups whose admins are above this height
     * @param height max height of admins of groups that remain in the collection
     */
    public String getGreater(Integer height){
        try {
            lock.readLock().lock();
            StringBuilder groups = new StringBuilder();
            for (StudyGroup group : studyGroupCollection) {
                if (group.getGroupAdmin().getHeight() > height) {
                    groups.append("\n");
                    groups.append(group.toString());
                }
            }
            return groups.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Method displays the forms of education of all groups from the collection in descending order
     */
    public String printFromOfEducation(){
        try {
            lock.readLock().lock();
            StringBuilder print = new StringBuilder();
            int countDistance = 0;
            int countFullTime = 0;
            int countEventing = 0;
            for (StudyGroup group : studyGroupCollection) {
                if (group.getFormOfEducation().equals(FormOfEducation.DISTANCE_EDUCATION)) countDistance++;
                if (group.getFormOfEducation().equals(FormOfEducation.FULL_TIME_EDUCATION)) countFullTime++;
                if (group.getFormOfEducation().equals(FormOfEducation.EVENTING_CLASSES)) countEventing++;
            }
            for (int i = 0; i < countEventing; i++) {
                print.append(FormOfEducation.EVENTING_CLASSES).append("\n");
            }
            for (int i = 0; i < countFullTime; i++) {
                print.append(FormOfEducation.FULL_TIME_EDUCATION).append("\n");
            }
            for (int i = 0; i < countDistance; i++) {
                print.append(FormOfEducation.DISTANCE_EDUCATION).append("\n");
            }
            return print.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Method finds the collection type
     * @return type of collection
     */
    public String collectionType(){
        try{
            if(studyGroupCollection.isEmpty()) throw new NullCollectionException();
            return studyGroupCollection.getClass().getName();
        } catch (NullCollectionException e){
            return "empty";
        }
    }

    /**
     * Method generates the date the group was created
     * @return creation date
     */
    public LocalDate generateCreationDate(){
        return LocalDate.now();
    }

    public LocalDate getLastInitDate(){
        return lastInitDate;
    }
    public LocalDate getLastSaveDate(){
        return lastSaveDate;
    }

    public HashSet<StudyGroup> getStudyGroupCollection(){
        return studyGroupCollection;
    }

}

