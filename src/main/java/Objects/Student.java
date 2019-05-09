package Objects;

/**
 * Contains data from the student info csv file
 */
public class Student {

    public Student(String studentId, String major, String gender) {
        this.studentId = studentId;
        this.major = major;
        this.gender = gender;
    }

    public String studentId;
    public String major;
    public String gender;
}
