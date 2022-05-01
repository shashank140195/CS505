package cs505pubsubcep;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import cs505pubsubcep.Utils.ContactMongo;
import cs505pubsubcep.Utils.EventMongo;
import cs505pubsubcep.Utils.HospitalMongo;
import cs505pubsubcep.Utils.VaccineMongo;
import cs505pubsubcep.database.MongoEngine;
import cs505pubsubcep.database.NeoEngine;
import org.bson.Document;
import org.neo4j.driver.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String args[]) throws Exception {
        System.out.println("test");

        /*


        neo4j
         */

//        String mainMRN = "b47197a2-c31b-11ec-859a-3af9d3a61d88";
//
//        List<String> contactList = new ArrayList<String>();
//        List<String> eventList = new ArrayList<String>();
//
//        contactList.add("4225fbc0-c2c1-11ec-ae31-3af9d3a61d88");
////        contactList.add("4225fbf2-c2c1-11ec-ae31-3af9d3a61d88");
//
//
//        eventList.add("4225fa1c-c2c1-11ec-ae31-3af9d3a61d88");
//        eventList.add("4225fa58-c2c1-11ec-ae31-3af9d3a61d88");
//        eventList.add("4225fa58-c2c1-11ec-ae31-3af9d3a61d88");
//        eventList.add("4225fa1c-c2c1-11ec-ae31-3af9d3a61d88");


        // Aura queries use an encrypted connection using the "neo4j+s" protocol
        String uri = "neo4j+s://9d9f2391.databases.neo4j.io";

        String user = "neo4j";
        String password = "O9OG4BQLcYCrJ70Dc4JsjXhVWwxhnKClOLaXk0881uM";

        NeoEngine app = new NeoEngine(uri, user, password, Config.defaultConfig());

        Map<String, ArrayList<String>> tmp;
        tmp = app.getContactEvents("p7");
        System.out.println(tmp);

//        app.getContactEvents("eb1f22b8-c340-11ec-b618-3af9d3a61d88");
//        app.close();

//        List<String> contactResultList = app.getContactMrn(mainMRN);
//        System.out.println(contactResultList);
//        app.close();
//
//
//        app = new NeoEngine(uri, user, password, Config.defaultConfig());
//        contactResultList = app.getContactMrn(mainMRN);
//        System.out.println(contactResultList);
//        app.close();

//        String q = "match (n) return n";
//        Map<String, Object> p = new HashMap<String, Object>();
//        System.out.println("First");
//        app.testQuery(q,p,"n");
//
//        System.out.println("Second");
//        q = "match (p1:Patient)<-[r:contact]->(p2:Patient) where p1.mrn=$mrn return p2";
//        p.clear();
//        p.put("mrn","b47197a2-c31b-11ec-859a-3af9d3a61d88");
//        app.testQuery(q,p,"p2");

//        System.out.println("Second");
//        app.testQuery(q,p);
//        app.clear();
//        app.testQuery(q,p);
//        app.clear();
//        app.processEventMrn(mainMRN, contactList, eventList);



//        MongoEngine mongoEngine = new MongoEngine();
//        MongoDatabase mongoDatabase = mongoEngine.client.getDatabase("CS505Doc");
//
//        HospitalMongo hospitalMongo = new HospitalMongo(mongoEngine, mongoDatabase);
//        VaccineMongo vaccineMongo = new VaccineMongo(mongoEngine, mongoDatabase);
//        FindIterable<Document> hosAllData = hospitalMongo.getAllHospitalData();
//        int count[] = {0,0,0};
//        double[] vax = {0.0, 0.0, 0.0};
//        for(Document tmpHos : hosAllData){
//            count[(Integer) tmpHos.get("patient_status")-1]++;
//            if(vaccineMongo.getVaccinationData((String) tmpHos.get("patient_mrn"))){
//                vax[(Integer) tmpHos.get("patient_status")-1]++;
//            }
//        }
//
//        for(int status=0;status<3;status++){
//            System.out.println("Status : "+status+" Count: "+count[status]+" Vax: "+((count[status]>0)?vax[status]/count[status]:0));
//        }
//
//        //hid specific hospital
//        String hid = "640143";
//        System.out.println("For hid: "+hid);
//        FindIterable<Document> hosSpecificData = hospitalMongo.getSpecificHospitalData(hid);
//        int countAll[] = {0,0,0};
//        double[] vaxAll = {0.0, 0.0, 0.0};
//        for(Document tmpHos : hosSpecificData){
//            countAll[(Integer) tmpHos.get("patient_status")-1]++;
//            if(vaccineMongo.getVaccinationData((String) tmpHos.get("patient_mrn"))){
//                vaxAll[(Integer) tmpHos.get("patient_status")-1]++;
//            }
//        }
//
//        for(int status=0;status<3;status++){
//            System.out.println("Status : "+status+" Count: "+countAll[status]+" Vax: "+((countAll[status]>0)?vaxAll[status]/countAll[status]:0));
//        }

//        hospitalMongo.getSpecificHospitalData("640143");
//        EventMongo eventMongo = new EventMongo(mongoEngine, mongoDatabase);
//
//        ContactMongo contactMongo = new ContactMongo(mongoEngine, mongoDatabase);
//
//        ArrayList<String> cList = contactMongo.getContactList("a1e38e61-bf41-11ec-8eab-b9440ee2d45c");
//        eventMongo.getEventContactList(cList);
//        ArrayList<String> cList = contactMongo.getContactList("ca1976c0-beda-11ec-8eab-b9440ee2d45c");

    }
}
