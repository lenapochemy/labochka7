package data;

import exceptions.*;
import manager.ConsoleManager;
import manager.users.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Class represent study group and his description
 */
public class StudyGroup implements Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным
    // Значение этого поля должно генерироваться автоматически
    /**
     * Invalid id value, assigned id if the group description values are incorrect, reports an error
     */
    public static final int wrongId = -1;
    private static final int defaultID = 1;
    private static final LocalDate default_date = LocalDate.now();
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer studentsCount; //Значение поля должно быть больше 0, Поле может быть null
    private FormOfEducation formOfEducation; //Поле может быть null
    private Semester semesterEnum; //Поле может быть null
    private Person groupAdmin; //Поле может быть null

    public StudyGroup(){
    }

    public StudyGroup(int id, String name, Coordinates coordinates, LocalDate creationDate, Integer studentsCount,
                      FormOfEducation formOfEducation, Semester semesterEnum, Person groupAdmin) throws IncorrectGroupValueException{
        this.setId(id);
        this.setName(name);
        this.setCoordinates(coordinates);
        this.setCreationDate(creationDate);
        this.setStudentsCount(studentsCount);
        this.setFormOfEducation(formOfEducation);
        this.setSemesterEnum(semesterEnum);
        this.setGroupAdmin(groupAdmin);
    }


    /**
     * Method checks the correctness of group's name value
     * @param name verifiable name
     */
      public void setName(String name){
        try {
            if(name == null || name.isEmpty()) throw new NullException();
            this.name = name;
        } catch (NullException e){
            ConsoleManager.printError("Name can't be empty");
            this.id = wrongId;
        }
    }

    /**
     * Method checks the correctness of group's id value
     * @param id verifiable id
     */
    public void setId(int id){
        try {
            if(id <= 0) throw new IncorrectValueException();
            this.id = id;
        } catch (IncorrectValueException e){
            ConsoleManager.printError("ID can't be negative");
            this.id = wrongId;
        }
    }

    /**
     * Method checks the correctness of group's coordinates value
     * @param coordinates verifiable coordinates
     * @throws IncorrectGroupValueException не верное значение координат
     */
    public void setCoordinates(Coordinates coordinates) throws IncorrectGroupValueException{
        this.coordinates = coordinates;
        this.coordinates.setX(coordinates.getCoordinatesX());
        this.coordinates.setY(coordinates.getCoordinatesY());
    }

    /**
     * Method checks the correctness of group's creation date value
     * @param creationDate verifiable date
     */
    public void setCreationDate(LocalDate creationDate){
        try {
            if(creationDate == null) throw new NullException();
            this.creationDate = creationDate;
        } catch (NullException e){
            ConsoleManager.printError("Date can't be null");
            this.id = wrongId;
        }
    }

    /**
     * Method checks the correctness of students count value
     * @param studentsCount verifiable count
     */
    public void setStudentsCount(Integer studentsCount){
        try {
            if(studentsCount <= 0) throw new IncorrectValueException();
            this.studentsCount = studentsCount;
        } catch (IncorrectValueException e){
            ConsoleManager.printError("Students count can't be negative");
            this.id = wrongId;
        }
    }

    /**
     * Method assigns form of education value to group
     * @param formOfEducation form of education
     */
    public void setFormOfEducation(FormOfEducation formOfEducation){
        this.formOfEducation = formOfEducation;
    }

    /**
     * Method assigns semester value to group
     * @param semesterEnum semester
     */
    public void setSemesterEnum(Semester semesterEnum){
        this.semesterEnum = semesterEnum;
    }

    /**
     * Method assigns admin to group
     * @param groupAdmin group's admin
     */
    public void setGroupAdmin(Person groupAdmin){
        this.groupAdmin = groupAdmin;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates(){
        return coordinates;
    }
    public LocalDate getCreationDate(){
        return creationDate;
    }
    public Integer getStudentsCount(){
        return studentsCount;
    }
    public FormOfEducation getFormOfEducation(){
        return formOfEducation;
    }
    public Semester getSemesterEnum(){
        return semesterEnum;
    }
    public Person getGroupAdmin(){
        return groupAdmin;
    }

    @Override
    public String toString(){
        return "StudyGroup{" + "id=" + id + ", name=" + name + ", coordinates=" + coordinates +
                ", creationDate=" + creationDate + ", studentsCount=" + studentsCount +
                ", formOfEducation=" + formOfEducation + ", semesterEnum=" + semesterEnum +
                ", groupAdmin=" + groupAdmin + "}";
    }

    @Override
    public int hashCode(){
        return name.hashCode() + coordinates.hashCode() + studentsCount.hashCode() + formOfEducation.hashCode() + semesterEnum.hashCode() + groupAdmin.hashCode();
    }
}
