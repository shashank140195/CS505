package cs505pubsubcep.database;
import com.mongodb.*;
 import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
 import com.mongodb.client.MongoCollection;
 import com.mongodb.client.MongoDatabase;
 import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoEngine {
    public static MongoClient client;
    public MongoEngine() {



        client = MongoClients.create("<connection string>");


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
