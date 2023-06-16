package manager.database;

import data.*;
import exceptions.IncorrectGroupValueException;
import manager.ConsoleManager;
import manager.users.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBaseHandler {

    private static final String ADD_USER = "INSERT INTO users (login, password) VALUES (?,?) ";
    private static final String VALIDATE_USER = "SELECT COUNT(*) AS count FROM users WHERE login = ? AND password =?";
    private static final String FIND_LOGIN = "SELECT COUNT(*) AS count FROM users WHERE login = ?";
    private static final String USER_ID_BY_LOGIN = "SELECT id FROM users WHERE login = ?";
    //private static final String USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String ADD_STUDY_GROUP = "INSERT INTO study_groups (name, coordinate_x, coordinate_y, creation_date, students_count, " +
            "form_of_education, semester, admin_name, admin_height, admin_eye_color, admin_hair_color, admin_nationality, user_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String ADD_STUDY_GROUP_WITH_ID = "INSERT INTO study_groups " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String IS_OWNER = "SELECT COUNT(*) AS count FROM study_groups JOIN users ON study_groups.user_id = users.id" +
            " WHERE study_groups.id = ? AND users.login = ?";
    private static final String ALL_GROUPS = "SELECT * FROM study_groups";
    private static final String CLEAR = "TRUNCATE TABLE study_groups";
    private static final String REMOVE_BY_ID = "DELETE FROM study_groups WHERE id = ?";
    //private static final String ALL_USERS_GROUP = "SELECT * FROM study_groups WHERE user_id = ?";
    private static final String GROUP_ID = "SELECT * FROM study_groups WHERE name = ? AND coordinate_x = ? AND  coordinate_y = ? AND  " +
            "creation_date = ? AND  students_count = ? AND  form_of_education = ? AND  semester = ? AND  admin_name = ? AND  admin_height = ? AND  " +
            "admin_eye_color = ? AND  admin_hair_color = ? AND  admin_nationality = ? AND  user_id =?";


    private final String URL;
    private final String login;
    private final String password;
    private Connection connection;

    public DataBaseHandler(String URL, String login, String password){
        this.URL = URL;
        this.login = login;
        this.password = password;
    }

    public void connectToDatabase(){
        try{
            connection = DriverManager.getConnection(URL, login, password);
            ConsoleManager.printSuccess("Connect to database is successful");
        } catch (SQLException e){
            ConsoleManager.printError("Problem with connecting to database");
            System.exit(-1);
        }
    }

    public boolean clearCollectionAdmin() {
        try {
            PreparedStatement clearStatement = connection.prepareStatement(CLEAR);
            clearStatement.executeUpdate();
            clearStatement.close();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    /*private HashSet<StudyGroup> userGroups(User user){
        HashSet<StudyGroup> studyGroups = new HashSet<>();
        try {
            PreparedStatement userStatement = connection.prepareStatement(USER_ID_BY_LOGIN);
            userStatement.setString(1, user.getLogin());
            ResultSet result = userStatement.executeQuery();
            result.next();
            int user_id = result.getInt("id");
            userStatement.close();

            PreparedStatement groupStatement = connection.prepareStatement(ALL_USERS_GROUP);
            groupStatement.setInt(1, user_id);
            ResultSet resultSet = groupStatement.executeQuery();
            while (resultSet.next()) {
                studyGroups.add(studyGroupFromResult(result));
            }
            return studyGroups;

        } catch (SQLException e){
            ConsoleManager.printError("Problem with database");
            return null;
        } catch (IncorrectGroupValueException e){
            return null;
        }
    }

     */

    public HashSet<StudyGroup> clearCollection(User user){

            HashSet<StudyGroup> studyGroups = new HashSet<>();
            try {
                    connection.setAutoCommit(false);
                    connection.setSavepoint();
                    PreparedStatement allGroupsStatement = connection.prepareStatement(ALL_GROUPS);
                    ResultSet resultSet = allGroupsStatement.executeQuery();
                   // int id;
                    StudyGroup group;
                    while (resultSet.next()) {
                        group = studyGroupFromResult(resultSet);
                        if (isOwner(group.getId(), user.getLogin())) {
                            removeGroup(group, user);
                        } else {
                            studyGroups.add(group);
                        }
                    }
                    allGroupsStatement.close();
                    connection.commit();
                    connection.setAutoCommit(true);
                    return studyGroups;
            } catch (IncorrectGroupValueException e) {
                return null;
            } catch (SQLException e) {
                ConsoleManager.printError("Problem with clearing collection");
                rollback();
                return null;
            }
    }

    public boolean registrationUser(String login, String password)throws SQLException{
        if(isUserExist(login)) return false;
            PreparedStatement addStatement = connection.prepareStatement(ADD_USER);
            addStatement.setString(1, login);
            addStatement.setString(2, PasswordHasher.encryptPassword(password));
            addStatement.executeUpdate();
            addStatement.close();
            return true;
    }

    public boolean isUserExist(String login) throws SQLException{
        PreparedStatement findStatement = connection.prepareStatement(FIND_LOGIN);
        findStatement.setString(1, login);
        ResultSet result = findStatement.executeQuery();
        result.next();
        int count = result.getInt("count");
        findStatement.close();
        return count == 1;
    }

    public boolean validateUser(String login, String password) throws SQLException{
        PreparedStatement findUserStatement = connection.prepareStatement(VALIDATE_USER);
        String hashPassword = PasswordHasher.encryptPassword(password);
        findUserStatement.setString(1, login);
        findUserStatement.setString(2, hashPassword);
        ResultSet result = findUserStatement.executeQuery();
        result.next();
        int count = result.getInt("count");
        findUserStatement.close();
        return count == 1;
    }

    public boolean isOwner(int groupID, String login) throws SQLException{
        try {
            if(login.equals("admin")) return true;
            PreparedStatement ownerStatement = connection.prepareStatement(IS_OWNER);
            ownerStatement.setInt(1, groupID);
            ownerStatement.setString(2, login);
            ResultSet result = ownerStatement.executeQuery();
            result.next();
            return result.getInt("count") == 1;
        } catch (SQLException e){
            ConsoleManager.printError("Proble with database");
            return false;
        }
    }

    public boolean removeGroup(StudyGroup studyGroup, User user){
        try {
                if (isOwner(studyGroup.getId(), user.getLogin())) {
                    PreparedStatement removeStatement = connection.prepareStatement(REMOVE_BY_ID);
                    removeStatement.setInt(1, studyGroup.getId());
                    removeStatement.executeUpdate();
                    return true;
                } else {
                    return false;
                }
        } catch (SQLException e){
            return false;
        }

    }
    private StudyGroup studyGroupFromResult(ResultSet result) throws SQLException, IncorrectGroupValueException{
        StudyGroup studyGroup;
        try {
            int id = result.getInt("id");
            String name = result.getString("name");
            int x = result.getInt("coordinate_x");
            Double y = result.getDouble("coordinate_y");
            Integer studentsCount = result.getInt("students_count");
            LocalDate date = result.getDate("creation_date").toLocalDate();
            FormOfEducation form = FormOfEducation.valueOf(result.getString("form_of_education"));
            Semester semester = Semester.valueOf(result.getString("semester"));

            String admin_name = result.getString("admin_name");
            Integer height = result.getInt("admin_height");
            ColorEye eye = ColorEye.valueOf(result.getString("admin_eye_color"));
            ColorHair hair = ColorHair.valueOf(result.getString("admin_hair_color"));
            Country nationality = Country.valueOf(result.getString("admin_nationality"));

            /*int user_id = result.getInt("user_id");
            PreparedStatement userStatement = connection.prepareStatement(USER_BY_ID);
            userStatement.setInt(1, user_id);
            ResultSet resultSet = userStatement.executeQuery();
            resultSet.next();
            User user = new User(resultSet.getString("login"), resultSet.getString("password"));
            userStatement.close();
             */

            Person admin = new Person(admin_name, height, eye, hair, nationality);
            Coordinates coordinates = new Coordinates(x, y);
            studyGroup = new StudyGroup(id, name, coordinates, date, studentsCount, form, semester, admin);
        } catch (IllegalArgumentException e){
            throw new IncorrectGroupValueException();
        }
        return studyGroup;
    }

    public HashSet<StudyGroup> loadCollectionFromDatabase(){
        HashSet<StudyGroup> studyGroups = new HashSet<>();
        try{
            PreparedStatement dataStatement = connection.prepareStatement(ALL_GROUPS);
            ResultSet result = dataStatement.executeQuery();

            while (result.next()){
                try{
                    StudyGroup group = studyGroupFromResult(result);
                    studyGroups.add(group);
                } catch (IncorrectGroupValueException e){
                    ConsoleManager.printError("Incorrect group in data base");
                }
            }
            dataStatement.close();

        } catch (SQLException e){
            ConsoleManager.printError("Problem with loading collection from database. Program is finished");
            System.exit(-1);
        }
        return studyGroups;
    }

    public boolean addStudyGroupWithId(StudyGroup studyGroup, User user){
        try{
            PreparedStatement userStatement = connection.prepareStatement(USER_ID_BY_LOGIN);
            userStatement.setString(1, user.getLogin());
            ResultSet result = userStatement.executeQuery();
            result.next();
            int user_id = result.getInt("id");
            userStatement.close();

                connection.setAutoCommit(false);
                connection.setSavepoint();
                PreparedStatement addStatement = connection.prepareStatement(ADD_STUDY_GROUP_WITH_ID);
                addStatement.setInt(1, studyGroup.getId());
                addStatement.setString(2, studyGroup.getName());
                addStatement.setInt(3, studyGroup.getCoordinates().getCoordinatesX());
                addStatement.setDouble(4, studyGroup.getCoordinates().getCoordinatesY());
                addStatement.setString(5, studyGroup.getCreationDate().toString());
                addStatement.setInt(6, studyGroup.getStudentsCount());
                addStatement.setString(7, studyGroup.getFormOfEducation().toString());
                addStatement.setString(8, studyGroup.getSemesterEnum().toString());
                addStatement.setString(9, studyGroup.getGroupAdmin().getName());
                addStatement.setInt(10, studyGroup.getGroupAdmin().getHeight());
                addStatement.setString(11, studyGroup.getGroupAdmin().getEyeColor().name());
                addStatement.setString(12, studyGroup.getGroupAdmin().getHairColor().name());
                addStatement.setString(13, studyGroup.getGroupAdmin().getNationality().name());
                addStatement.setInt(14, user_id);

                addStatement.executeUpdate();
                addStatement.close();

                connection.commit();
                connection.setAutoCommit(true);
            return true;
        } catch (SQLException e){
            ConsoleManager.printError("Problem with insert group to database");
            rollback();
            return false;
        }
    }

    public int addStudyGroup(StudyGroup studyGroup, User user){
        try{
            PreparedStatement userStatement = connection.prepareStatement(USER_ID_BY_LOGIN);
            userStatement.setString(1, user.getLogin());
            ResultSet result = userStatement.executeQuery();
            result.next();
            int user_id = result.getInt("id");
            userStatement.close();

            connection.setAutoCommit(false);
            connection.setSavepoint();

                PreparedStatement addStatement = connection.prepareStatement(ADD_STUDY_GROUP);
                addStatement.setString(1, studyGroup.getName());
                addStatement.setInt(2, studyGroup.getCoordinates().getCoordinatesX());
                addStatement.setDouble(3, studyGroup.getCoordinates().getCoordinatesY());
                addStatement.setString(4, studyGroup.getCreationDate().toString());
                addStatement.setInt(5, studyGroup.getStudentsCount());
                addStatement.setString(6, studyGroup.getFormOfEducation().toString());
                addStatement.setString(7, studyGroup.getSemesterEnum().toString());
                addStatement.setString(8, studyGroup.getGroupAdmin().getName());
                addStatement.setInt(9, studyGroup.getGroupAdmin().getHeight());
                addStatement.setString(10, studyGroup.getGroupAdmin().getEyeColor().name());
                addStatement.setString(11, studyGroup.getGroupAdmin().getHairColor().name());
                addStatement.setString(12, studyGroup.getGroupAdmin().getNationality().name());
                addStatement.setInt(13, user_id);

                addStatement.executeUpdate();
                addStatement.close();

                PreparedStatement idStatement = connection.prepareStatement(GROUP_ID);
                idStatement.setString(1, studyGroup.getName());
                idStatement.setInt(2, studyGroup.getCoordinates().getCoordinatesX());
                idStatement.setDouble(3, studyGroup.getCoordinates().getCoordinatesY());
                idStatement.setString(4, studyGroup.getCreationDate().toString());
                idStatement.setInt(5, studyGroup.getStudentsCount());
                idStatement.setString(6, studyGroup.getFormOfEducation().toString());
                idStatement.setString(7, studyGroup.getSemesterEnum().toString());
                idStatement.setString(8, studyGroup.getGroupAdmin().getName());
                idStatement.setInt(9, studyGroup.getGroupAdmin().getHeight());
                idStatement.setString(10, studyGroup.getGroupAdmin().getEyeColor().name());
                idStatement.setString(11, studyGroup.getGroupAdmin().getHairColor().name());
                idStatement.setString(12, studyGroup.getGroupAdmin().getNationality().name());
                idStatement.setInt(13, user_id);
                ResultSet res = idStatement.executeQuery();
                res.next();
                int id = res.getInt("id");
                idStatement.close();

                connection.commit();
                connection.setAutoCommit(true);
                return id;
        } catch (SQLException e){
            ConsoleManager.printError("Problem with insert group to database");
            rollback();
        }
        return 0;
    }

    private void rollback(){
        try{
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e){
            ConsoleManager.printError("problem with rollback");
        }
    }
}
