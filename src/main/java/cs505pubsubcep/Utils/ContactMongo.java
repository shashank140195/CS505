package cs505pubsubcep.Utils;

import com.mongodb.BasicDBList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import cs505pubsubcep.database.MongoEngine;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.persistence.internal.sessions.DirectCollectionChangeRecord;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ContactMongo implements DBImpl{
    public MongoEngine mongoEngine;
    public MongoDatabase mongoDatabase;
    public MongoCollection<Document> collection;


    public Map<String, Set<String>> contactMap;

    public Map<String, Set<String>> getContactMap() {
        return contactMap;
    }

    public void setContactMap(Map<String, Set<String>> contactMap) {
        this.contactMap = contactMap;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void setCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public ContactMongo(MongoEngine mongoEngine, MongoDatabase mongoDatabase) {
        this.mongoEngine = mongoEngine;
        this.mongoDatabase = mongoDatabase;
        this.collection = this.mongoDatabase.getCollection("contact");
        System.out.println("Inilialized mongo - contact");
    }


    public MongoEngine getMongoEngine() {
        return this.mongoEngine;
    }

    public void setMongoEngine(MongoEngine mongoEngine) {
        this.mongoEngine = mongoEngine;
    }

    public MongoDatabase getMongoDatabase() {
        return this.mongoDatabase;
    }

    public void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public boolean update() {
        for(String key : this.contactMap.keySet()) {
            Bson filter = Filters.eq("type","contact");
            Bson update = Updates.push(key, this.contactMap.get(key));

            UpdateOptions options = new UpdateOptions().upsert(true);
            System.out.println(collection.updateOne(filter, update, options));

//            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
//                    .returnDocument(ReturnDocument.AFTER);
//            Document result = collection.findOneAndUpdate(filter, update, options);
//            System.out.println(result.toJson());
        }
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    public ArrayList<String> getContactList(String mrn){
        FindIterable<Document> mongoIter = this.collection.find();
        try {
            ArrayList<ArrayList<String>> mrnList = (ArrayList<ArrayList<String>>) mongoIter.first().get(mrn);
            System.out.println(mrnList);
            if(mrnList!=null){
                System.out.println(mrnList.get(0));
                System.out.println((mrnList!=null)?mrnList.get(0).size():null);
                return mrnList.get(0);
            }
            return new ArrayList<String>();
        }catch (NullPointerException nex){
            nex.printStackTrace();
            return new ArrayList<String>();
        }
    }
}
