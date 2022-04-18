package cs505pubsubcep;

import com.mongodb.client.MongoDatabase;
import cs505pubsubcep.Utils.ContactMongo;
import cs505pubsubcep.Utils.EventMongo;
import cs505pubsubcep.database.MongoEngine;

import java.util.ArrayList;

public class Test {

    public static void main(String args[]){
        System.out.println("test");
        MongoEngine mongoEngine = new MongoEngine();
        MongoDatabase mongoDatabase = mongoEngine.client.getDatabase("CS505Doc");
        EventMongo eventMongo = new EventMongo(mongoEngine, mongoDatabase);

        ContactMongo contactMongo = new ContactMongo(mongoEngine, mongoDatabase);

        ArrayList<String> cList = contactMongo.getContactList("a1e38e61-bf41-11ec-8eab-b9440ee2d45c");
        eventMongo.getEventContactList(cList);
//        ArrayList<String> cList = contactMongo.getContactList("ca1976c0-beda-11ec-8eab-b9440ee2d45c");

    }
}
