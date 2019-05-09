package Objects;

/**
 * Contains data to be converted into a json string and sent as http post body
 */
public class Output {

    public Output(int average, String[] studentIds){
        this.id = "tstef3@gmail.com";
        this.name = "Tyler Stefani";
        this.average = average;
        this.studentIds = studentIds.clone();
    }

    private String id;
    private String name;
    private int average;
    private String[] studentIds;

}
