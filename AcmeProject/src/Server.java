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

    static Boolean flag = false;
    Model model;
    HttpServer server;

    public Server(Model model) throws Exception {
        this.model = model;
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/data", new DataHandler());
//        server.createContext("/pub", new PubHandler());
//        server.createContext("/sub", new SubHandler());
//        server.createContext("/nodes", new NodesHandler());
//        server.createContext("/topics", new TopicsHandler());
        server.setExecutor(null); // creates a default executor
    }

    public void start() {
        server.start();
    }


    class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

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

            Thread t1 = new Thread(() -> {
                model.createTopicList(jObject.getJSONArray("topics"));
            });
            t1.start();

            Thread t2 = new Thread(() -> {
                model.createNodeList(jObject.getJSONArray("nodes"));
            });
            t2.start();

            Thread t3 = new Thread(() -> {
                model.addPublish(jObject.get("pub").toString());
            });
            t3.start();

            Thread t4 = new Thread(() -> {
                model.addSubscribe(jObject.get("sub").toString());
            });
            t4.start();


            String response = "Response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }
    }


//    class PubHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//
//            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
//            BufferedReader br = new BufferedReader(isr);
//            int b;
//            StringBuilder buf = new StringBuilder(512);
//            while ((b = br.read()) != -1) {
//                buf.append((char) b);
//            }
//
//            br.close();
//            isr.close();
//
//
//            Thread t1 = new Thread(() -> { model.addPublish(buf.toString()); });
//            t1.start();
//
//            String response = "Pub Response";
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }
//    }
//
//     class SubHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//
//            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
//            BufferedReader br = new BufferedReader(isr);
//            int b;
//            StringBuilder buf = new StringBuilder(512);
//            while ((b = br.read()) != -1) {
//                buf.append((char) b);
//            }
//
//            br.close();
//            isr.close();
//
//
//            Thread t1 = new Thread(() -> { model.addSubscribe(buf.toString()); });
//            t1.start();
//
//
//            String response = "Sub response";
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }
//    }
//
//     class NodesHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//
//
//            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
//            BufferedReader br = new BufferedReader(isr);
//            int b;
//            StringBuilder buf = new StringBuilder(512);
//            while ((b = br.read()) != -1) {
//                buf.append((char) b);
//            }
//
//            br.close();
//            isr.close();
//
//
//            Thread t1 = new Thread(() -> { model.createNodeList(buf.toString()); });
//            t1.start();
//
//            String response = "Nodes response";
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }
//    }
//
//     class TopicsHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//
//            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
//            BufferedReader br = new BufferedReader(isr);
//            int b;
//            StringBuilder buf = new StringBuilder(512);
//            while ((b = br.read()) != -1) {
//                buf.append((char) b);
//            }
//
//            br.close();
//            isr.close();
//
//
//            Thread t1 = new Thread(() -> { model.createTopicList(buf.toString()); });
//            t1.start();
//
//
//            String response = "Topics response";
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }
//    }


}