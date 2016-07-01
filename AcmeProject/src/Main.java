import java.io.IOException;

public class Main {


    public static void main(String args[]) {

        Model model = new Model("src/families", "src/turtle.acme", "Turtlebot");
        Server server;
        try {
            server = new Server(model);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

//
//        while (true) {
//            try {
//                System.in.read();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            model.createModel();
//            model.writeToFile();
//        }
    }

}
