package cs505pubsubcep.database;
import com.mongodb.*;
 import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
 import com.mongodb.client.MongoCollection;
 import com.mongodb.client.MongoDatabase;
 import com.mongodb.client.model.Filters;

 import com.mongodb.client.model.UpdateOptions;
 import com.mongodb.client.result.*;
 import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

 import java.util.List;
 import java.util.Arrays;
 import java.util.ArrayList;

import org.bson.Document;

 import static com.mongodb.client.model.Filters.*;
 import static com.mongodb.client.model.Updates.*;

public class MongoEngine {
    public static MongoClient client;
    public MongoEngine() {



        client = MongoClients.create("mongodb+srv://diginova:diginova@cluster0.dqmo2.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");


    }

    public boolean delete(MongoDatabase mongoDatabase, String collectionName){
        boolean success= true;

        BasicDBObject document = new BasicDBObject();

        try {

            MongoCollection collection = mongoDatabase.getCollection(collectionName);
            // Delete All documents from collection Using blank BasicDBObject
            collection.deleteMany(document);
            System.out.println("Successfully deleted data documents in "+collectionName);
        }catch (Exception e){
            e.printStackTrace();
            success=false;
        }

        return success;
    }

    public boolean insert(Document document , MongoDatabase mongoDatabase, String collectionName){
        boolean succcess = true;
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            ObjectId objectId = new ObjectId();
            collection.insertOne(document.append("_id", objectId));
            System.out.println("Successfully inserted _id: "+ document.get("_id"));
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
            succcess=false;
        }
        return succcess;

    }
}
