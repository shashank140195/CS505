package cs505pubsubcep;

import cs505pubsubcep.CEP.CEPEngine;
import cs505pubsubcep.Topics.TopicConnector;
import cs505pubsubcep.database.DerbyDBEngine;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class Launcher {

    public static final String API_SERVICE_KEY = "12569587"; //Change this to your student id
    public static final int WEB_PORT = 8082;
    public static String inputStreamName = null;
    public static long accessCount = -1;

    public static TopicConnector topicConnector;

    public static CEPEngine cepEngine = null;

    public static void main(String[] args) throws IOException {

        // DerbyDBEngine derbyDBEngine = new DerbyDBEngine();

        
        System.out.println("Starting CEP...");

        cepEngine = new CEPEngine();


        //START MODIFY
        inputStreamName = "PatientInStream";
        String inputStreamAttributesString = "zip_code string, timestamp long";

        String outputStreamName = "PatientOutStream";
        String outputStreamAttributesString = "zip_code string, count long";


        String queryString = " " +
                "from PatientInStream#window.timeBatch(5 sec) " +
                "select zip_code, count() as count " +
                "group by zip_code " +
                "insert into PatientOutStream; ";

        //END MODIFY

        cepEngine.createCEP(inputStreamName, outputStreamName, inputStreamAttributesString, outputStreamAttributesString, queryString);

        System.out.println("CEP Started...");


         

        //starting pateint_data collector
        Map<String,String> message_config = new HashMap<>();
        message_config.put("hostname","128.163.202.50"); //Fill config for your team in
        message_config.put("username","student");
        message_config.put("password","student01");
        message_config.put("virtualhost","1");
        message_config.put("topicname", "patient_list");

        topicConnector = new TopicConnector(message_config);
        topicConnector.connect();

        //Embedded HTTP initialization
        startServer();

        try {
            while (true) {
                Thread.sleep(5000);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void startServer() throws IOException {

        final ResourceConfig rc = new ResourceConfig()
        .packages("cs505pubsubcep.httpcontrollers");
        //.register(AuthenticationFilter.class);

        System.out.println("Starting Web Server...");
        URI BASE_URI = UriBuilder.fromUri("http://0.0.0.0/").port(WEB_PORT).build();
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);

        try {
            httpServer.start();
            System.out.println("Web Server Started...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
