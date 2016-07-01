public class Topic {

    private String name;
    private String msg_Type;

    public Topic(String name, String msg_Type) {
        this.name = name + "topic";
        this.msg_Type = msg_Type;
    }


    public String getName() {
        return this.name;
    }

    public String getMsg_Type() { return  this.msg_Type; }
}
