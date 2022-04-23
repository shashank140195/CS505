package cs505pubsubcep.Topics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import cs505pubsubcep.Launcher;
import cs505pubsubcep.CEP.CEPEngine;
import cs505pubsubcep.CEP.accessRecord;
import cs505pubsubcep.Models.Hospital;
import cs505pubsubcep.Models.Patient;
import cs505pubsubcep.Models.Vaccination;
import org.bson.Document;

import javax.print.Doc;
import java.lang.reflect.Type;
import java.util.*;

import cs505pubsubcep.Utils.Constants;


public class TopicConnector {

    private Gson gson;
    //final Type typeOf = new TypeToken<List<Map<String,String>>>(){}.getType();\
    final Type typeListTestingData = new TypeToken<List<TestingData>>(){}.getType();

    final Type patient = new TypeToken<List<Patient>>(){}.getType();
    final Type hospital = new TypeToken<List<Hospital>>(){}.getType();
    final Type vaccination = new TypeToken<List<Vaccination>>(){}.getType();

    //private String EXCHANGE_NAME = "patient_data";
    Map<String,String> config;
    CEPEngine cepEngine;

    public TopicConnector(Map<String,String> config) {
        gson = new Gson();
        this.config = config;
    }

    public TopicConnector(Map<String,String> config, CEPEngine cepEngine) {
        gson = new Gson();
        this.config = config;
        this.cepEngine = cepEngine;
    }

    public void connect() {

        try {

            //create connection factory, this can be used to create many connections
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.get("hostname"));
            factory.setUsername(config.get("username"));
            factory.setPassword(config.get("password"));
            factory.setVirtualHost(config.get("virtualhost"));
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(1000);
            factory.setRequestedHeartbeat(5);

            //create a connection, many channels can be created from a single connection
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            patientListChannel(channel);
            hospitalListChannel(channel);
            vaxListChannel(channel);

        } catch (Exception ex) {
            System.out.println("connect Error: " + ex.getMessage());
            ex.printStackTrace();
        }
}

    private void patientListChannel(Channel channel) {
        try {

            System.out.println("Creating patient_list channel");

            String topicName = Constants.PATIENT_TOPIC;//"patient_list";

            channel.exchangeDeclare(topicName, "topic");
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, topicName, "#");


            System.out.println(" [*] Paitent List Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received Patient List Batch'" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

                List<Patient> incomingList = gson.fromJson(message, patient);
                for (Patient patientData : incomingList) {
                    System.out.println("*Java Class*");
                    System.out.println("\ttesting_id = " + patientData.getTesting_id());
                    System.out.println("\tpatient_name = " + patientData.getPatient_name());
                    System.out.println("\tpatient_mrn = " + patientData.getPatient_mrn());
                    System.out.println("\tpatient_zipcode = " + patientData.getPatient_zipcode());
                    System.out.println("\tpatient_status = " + patientData.getPatient_status());
                    System.out.println("\tcontact_list = " + patientData.getContact_list());
                    System.out.println("\tevent_list = " + patientData.getEvent_list());

                    if(patientData.getPatient_status() == 1){
                        System.out.println("Patient : "+patientData.getPatient_mrn()+" is +++++++. Zip code is: "+patientData.getPatient_zipcode());
                        //generate event based on access
                        String inputEvent = gson.toJson(new accessRecord(String.valueOf(patientData.getPatient_zipcode()), System.currentTimeMillis()));
                        System.out.println("inputEvent: " + inputEvent);

                        //send input event to CEP
                        Launcher.cepEngine.input(Launcher.inputStreamName, inputEvent);
                    }
                    Document document = new Document();
                    document.append("testing_id",patientData.getTesting_id());
                    document.append("patient_name",patientData.getPatient_name());
                    document.append("patient_mrn",patientData.getPatient_mrn());
                    document.append("patient_zipcode",patientData.getPatient_zipcode());
                    document.append("patient_status",patientData.getPatient_status());
                    document.append("contact_list",patientData.getContact_list());
                    document.append("event_list",patientData.getEvent_list());
//                    if(Launcher.mongoEngine.insert(document, Launcher.mongoDatabase, "patient")){
//                        System.out.println("Successfully inserted patient");
//                    }else{
//                        System.out.println("Not Successfully inserted patient");
//                    }

                    //Make a map for the contact list
                    Map<String, Set<String>> contactMap = new HashMap<String, Set<String>>();
                    Set<String> tmp = new TreeSet<String>();
                    if(patientData !=null && patientData.getContact_list()!=null) {
                        for (String mrn1 : patientData.getContact_list()) {
                            if (!mrn1.equals(patientData.getPatient_mrn())) {
                                tmp.add(mrn1);
                            }
                        }
                    }
                    if(tmp.size()>0){
                        contactMap.put(patientData.getPatient_mrn(), tmp);
                        for(String mrns : tmp){
                            Set<String> set1 = new TreeSet<String>();
                            set1.add(patientData.getPatient_mrn());
                            contactMap.put(mrns, set1);
                        }
                        Launcher.contactMongo.setContactMap(contactMap);
                        Launcher.contactMongo.update();
                    }

                    //make map for event list
                    Map<String, String > eventMap = new HashMap<String, String >();
                    if(patientData.getEvent_list()!=null) {
                        for (String ev : patientData.getEvent_list()) {
                            eventMap.put(ev, patientData.getPatient_mrn());
                        }
                    }
                    if(eventMap.size()>0){
                        Launcher.eventMongo.setEventMap(eventMap);
                        Launcher.eventMongo.update();
                    }
                }

//                List<TestingData> incomingList = gson.fromJson(message, typeListTestingData);
//                for (TestingData testingData : incomingList) {
//                    System.out.println("*Java Class*");
//                    System.out.println("\ttesting_id = " + testingData.testing_id);
//                    System.out.println("\tpatient_name = " + testingData.patient_name);
//                    System.out.println("\tpatient_mrn = " + testingData.patient_mrn);
//                    System.out.println("\tpatient_zipcode = " + testingData.patient_zipcode);
//                    System.out.println("\tpatient_status = " + testingData.patient_status);
//                    System.out.println("\tcontact_list = " + testingData.contact_list);
//                    System.out.println("\tevent_list = " + testingData.event_list);
//
//                    if(testingData.patient_status == 1){
//                        System.out.println("Patient : "+testingData.patient_mrn+" is +++++++. Zip code is: "+testingData.patient_zipcode);
//                        //generate event based on access
//                        String inputEvent = gson.toJson(new accessRecord(String.valueOf(testingData.patient_zipcode), System.currentTimeMillis()));
//                        System.out.println("inputEvent: " + inputEvent);
//
//                        //send input event to CEP
//                        Launcher.cepEngine.input(Launcher.inputStreamName, inputEvent);
//                    }
//                }
                //List<Map<String,String>> incomingList = gson.fromJson(message, typeOf);
                //for(Map<String,String> map : incomingList) {
                //    System.out.println("INPUT CEP EVENT: " +  map);
                //Launcher.cepEngine.input(Launcher.inputStreamName, gson.toJson(map));
                //}
                System.out.println("");
                System.out.println("");

            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

        } catch (Exception ex) {
            System.out.println("patientListChannel Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void hospitalListChannel(Channel channel) {
        try {

            String topicName = Constants.HOSPITAL_TOPIC;// "hospital_list";

            System.out.println("Creating hospital_list channel");

            channel.exchangeDeclare(topicName, "topic");
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, topicName, "#");


            System.out.println(" [*] Hospital List Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received Hospital List Batch'" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

                List<Hospital> incomingList = gson.fromJson(message, hospital);
                for (Hospital hospitalData : incomingList) {
                    System.out.println("*Java Class*");
                    System.out.println("\thospital_id = " + hospitalData.getHospital_id());
                    System.out.println("\tpatient_name = " + hospitalData.getPatient_name());
                    System.out.println("\tpatient_mrn = " + hospitalData.getPatient_mrn());
                    System.out.println("\tpatient_status = " + hospitalData.getPatient_status());

                    Document hospitalDocument = new Document();
                    hospitalDocument.append("hospital_id", hospitalData.getHospital_id());
                    hospitalDocument.append("patient_mrn", hospitalData.getPatient_mrn());
                    hospitalDocument.append("patient_status", hospitalData.getPatient_status());

                    if(Launcher.hospitalMongo.insert(hospitalDocument)){
                        System.out.println("Inserted successfully into "+ Launcher.hospitalMongo.COLLECTION_NAME);
                    }else{
                        System.out.println("Error inserting data into "+ Launcher.hospitalMongo.COLLECTION_NAME);
                    }


                }

                };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

        } catch (Exception ex) {
            System.out.println("hospitalListChannel Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void vaxListChannel(Channel channel) {
        try {

            String topicName = Constants.VACCINATION_TOPIC;// "vax_list";

            System.out.println("Creating vax_list channel");

            channel.exchangeDeclare(topicName, "topic");
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, topicName, "#");


            System.out.println(" [*] Vax List Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received Vax Batch'" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

                List<Vaccination> incomingList = gson.fromJson(message, vaccination);
                for (Vaccination vaxData : incomingList) {
                    System.out.println("*Java Class*");
                    System.out.println("\tvaccination_id = " + vaxData.getVaccination_id());
                    System.out.println("\tpatient_name = " + vaxData.getPatient_name());
                    System.out.println("\tpatient_mrn = " + vaxData.getPatient_mrn());

                    Document vaxDocument = new Document();
                    vaxDocument.append("vaccination_id", vaxData.getVaccination_id());
                    vaxDocument.append("patient_mrn", vaxData.getPatient_mrn());

                    if(Launcher.vaccineMongo.insert(vaxDocument)){
                        System.out.println("Successfully inserted data into "+Launcher.vaccineMongo.COLLECTION_NAME);
                    }else{
                        System.out.println("Error inserting data into "+ Launcher.vaccineMongo.COLLECTION_NAME);
                    }
                }

            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

        } catch (Exception ex) {
            System.out.println("vaxListChannel Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
