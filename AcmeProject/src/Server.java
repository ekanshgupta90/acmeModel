import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class Server {
    Model model;
    HttpServer server;

    public Server(Model model) throws Exception {
        this.model = model;
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/data", new DataHandler());
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("Server started");
    }


    class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            try {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder(512);
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                br.close();
                isr.close();

                JSONObject jObject = new JSONObject(buf.toString());
                model.initialize(jObject);
                model.createModel();
                model.writeToFile();

                String response = "OK";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

}