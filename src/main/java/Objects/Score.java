package Objects;

/**
 * Contains the data from the test scores and test retake scores csv files
 */
public class Score {

    public Score(String studentId, double score) {
        this.studentId = studentId;
        this.score = score;
    }

    public String studentId;
    public double score;

}
