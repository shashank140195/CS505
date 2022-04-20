package cs505pubsubcep;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import cs505pubsubcep.Utils.ContactMongo;
import cs505pubsubcep.Utils.EventMongo;
import cs505pubsubcep.Utils.HospitalMongo;
import cs505pubsubcep.Utils.VaccineMongo;
import cs505pubsubcep.database.MongoEngine;
import org.bson.Document;

import java.util.ArrayList;

public class Test {

    public static void main(String args[]){
        System.out.println("test");
        MongoEngine mongoEngine = new MongoEngine();
        MongoDatabase mongoDatabase = mongoEngine.client.getDatabase("CS505Doc");

        HospitalMongo hospitalMongo = new HospitalMongo(mongoEngine, mongoDatabase);
        VaccineMongo vaccineMongo = new VaccineMongo(mongoEngine, mongoDatabase);
        FindIterable<Document> hosAllData = hospitalMongo.getAllHospitalData();
        int count[] = {0,0,0};
        double[] vax = {0.0, 0.0, 0.0};
        for(Document tmpHos : hosAllData){
            count[(Integer) tmpHos.get("patient_status")-1]++;
            if(vaccineMongo.getVaccinationData((String) tmpHos.get("patient_mrn"))){
                vax[(Integer) tmpHos.get("patient_status")-1]++;
            }
        }

        for(int status=0;status<3;status++){
            System.out.println("Status : "+status+" Count: "+count[status]+" Vax: "+((count[status]>0)?vax[status]/count[status]:0));
        }

        //hid specific hospital
        String hid = "640143";
        System.out.println("For hid: "+hid);
        FindIterable<Document> hosSpecificData = hospitalMongo.getSpecificHospitalData(hid);
        int countAll[] = {0,0,0};
        double[] vaxAll = {0.0, 0.0, 0.0};
        for(Document tmpHos : hosSpecificData){
            countAll[(Integer) tmpHos.get("patient_status")-1]++;
            if(vaccineMongo.getVaccinationData((String) tmpHos.get("patient_mrn"))){
                vaxAll[(Integer) tmpHos.get("patient_status")-1]++;
            }
        }

        for(int status=0;status<3;status++){
            System.out.println("Status : "+status+" Count: "+countAll[status]+" Vax: "+((countAll[status]>0)?vaxAll[status]/countAll[status]:0));
        }

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
