import Objects.*;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final String STUDENT_FILE = "Student Info.xlsx";
    private static final String TEST_FILE = "Test Scores.xlsx";
    private static final String RETAKE_FILE = "Test Retake Scores.xlsx";
    private static final String URL = "http://3.86.140.38:5000/challenge";

    public static void main(String[] args) throws IOException {
        File dataDir = new File("./src/main/Data/");

        List<Student> students = readStudentFile(dataDir);
        List<Score> testScores = readScoreFile(dataDir, true);
        List<Score> retakeScores = readScoreFile(dataDir, false);

        Hashtable<String, Double> topScores = new Hashtable<>();

        for (Score score : testScores){
            topScores.put(score.studentId, score.score);
        }

        for (Score score : retakeScores){
            if (topScores.get(score.studentId) < score.score){
                topScores.put(score.studentId, score.score);
            }
        }

        double average = averageScores(topScores.values());

        ArrayList<String> femaleCsIds = new ArrayList<>();
        for (Student student : students) {
            if (student.gender.equals("F") && student.major.equals("computer science")) {
                femaleCsIds.add(Integer.toString(
                        Math.toIntExact(
                                Math.round(Double.parseDouble(student.studentId)))
                        )
                );
            }
        }

        Collections.sort(femaleCsIds);

        HttpResponse response = executePost(new Output(
                Math.toIntExact(Math.round(average)),
                femaleCsIds.toArray(new String[femaleCsIds.size()])
        ));
    }

    /**
     * @param dataDir The directory in which the data is stored
     * @return  List of student objects parsed from the data file
     */
    private static List<Student> readStudentFile(File dataDir) throws IOException {
        List<Student> students = new ArrayList<Student>();

        FileInputStream studentStream = new FileInputStream(new File(dataDir, STUDENT_FILE));
        XSSFSheet studentSheet = new XSSFWorkbook(studentStream).getSheetAt(0);

        Iterator<Row> it = studentSheet.rowIterator();
        Row headers = it.next();

        while (it.hasNext()){
            Row curr = it.next();

            students.add(new Student(
                    curr.getCell(0).toString(),
                    curr.getCell(1).toString(),
                    curr.getCell(2).toString()
            ));
        }
        return students;
    }

    /**
     * @param dataDir The directory in which the data is stored
     * @param initial Whether the initial scores will be parsed (otherwise the retakes are parsed)
     * @return  List of score objects parsed from the data file
     */
    private static List<Score> readScoreFile(File dataDir, boolean initial) throws IOException {
        List<Score> scores = new ArrayList<Score>();

        FileInputStream scoreStream;

        if (initial){
            scoreStream = new FileInputStream(new File(dataDir, TEST_FILE));
        }
        else{
            scoreStream = new FileInputStream(new File(dataDir, RETAKE_FILE));
        }

        XSSFSheet testSheet = new XSSFWorkbook(scoreStream).getSheetAt(0);

        Iterator<Row> it = testSheet.rowIterator();
        Row headers = it.next();

        while (it.hasNext()){
            Row curr = it.next();

            scores.add(new Score(
                    curr.getCell(0).toString(),
                    curr.getCell(1).getNumericCellValue()
            ));
        }

        return scores;
    }

    /**
     * @param topScores Collection of the top scores achieved by students on both tests
     * @return The average of the scores
     */
    private static double averageScores(Collection<Double> topScores){
        double total = 0;
        double count = 0;
        for (double score : topScores){
            total += score;
            count += 1;
        }
        return total/count;
    }

    /**
     * @param output The object to be converted into a json string and sent as an http post body
     */
    private static HttpResponse executePost(Output output) throws IOException {
        Gson gson = new Gson();

        String json = gson.toJson(output);
        StringEntity entity = new StringEntity(json);

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(URL);
        post.setEntity(entity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        return client.execute(post);
    }
}
