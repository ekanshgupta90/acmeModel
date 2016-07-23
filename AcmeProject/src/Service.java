import java.util.ArrayList;

public class Service {
    private String name;
    private String type;
    private ArrayList<String> args;


    public Service(String name, String type, ArrayList<String> args) {
        this.name = name;
        this.type = type;
        this.args = args;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public ArrayList<String> getArgs() {
        return this.args;
    }

}
