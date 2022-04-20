package cs505pubsubcep.Utils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import cs505pubsubcep.database.MongoEngine;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
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
    public String COLLECTION_NAME = "contact";


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
        this.collection = this.mongoDatabase.getCollection(COLLECTION_NAME);
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
            System.out.println("this.contactMap: "+this.contactMap);
            Set<String> set1 = this.contactMap.get(key);

            for(String str : set1) {

                Bson update = Updates.push(key, str);


                UpdateOptions options = new UpdateOptions().upsert(true);
                System.out.println(collection.updateOne(filter, update, options));
            }

//            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
//                    .returnDocument(ReturnDocument.AFTER);
//            Document result = collection.findOneAndUpdate(filter, update, options);
//            System.out.println(result.toJson());
        }
        return false;
    }

    @Override
    public boolean delete() {

        boolean success= true;

        BasicDBObject document = new BasicDBObject();

        try {

            MongoCollection collection = mongoDatabase.getCollection(COLLECTION_NAME);
            // Delete All documents from collection Using blank BasicDBObject
            collection.deleteMany(document);
            System.out.println("Successfully deleted data documents in "+COLLECTION_NAME);
        }catch (Exception e){
            e.printStackTrace();
            success=false;
        }

        return success;

    }

    @Override
    public boolean insert(Document document) {
        boolean succcess = true;
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
            ObjectId objectId = new ObjectId();
            collection.insertOne(document.append("_id", objectId));
            System.out.println("Successfully inserted _id: "+ document.get("_id")+" to "+COLLECTION_NAME);
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
            succcess=false;
        }
        return succcess;
    }

    public ArrayList<String> getContactList(String mrn){
        FindIterable<Document> mongoIter = this.collection.find();
        try {
            ArrayList<String> mrnList = (ArrayList<String>) mongoIter.first().get(mrn);
            System.out.println(mrnList);
            if(mrnList!=null){
                System.out.println(mrnList);
                System.out.println((mrnList!=null)?mrnList.size():null);
                return mrnList;
            }
            return new ArrayList<String>();
        }catch (NullPointerException nex){
            nex.printStackTrace();
            return new ArrayList<String>();
        }
    }
}
