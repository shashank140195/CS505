package cs505pubsubcep.Utils;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cs505pubsubcep.database.MongoEngine;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class HospitalMongo implements DBImpl{

    public MongoEngine mongoEngine;
    public MongoDatabase mongoDatabase;
    public MongoCollection<Document> collection;
    public String COLLECTION_NAME = "hospital";

    public HospitalMongo(MongoEngine mongoEngine, MongoDatabase mongoDatabase) {
        this.mongoEngine = mongoEngine;
        this.mongoDatabase = mongoDatabase;
        this.collection = this.mongoDatabase.getCollection(COLLECTION_NAME);
    }

    public MongoEngine getMongoEngine() {
        return mongoEngine;
    }

    public void setMongoEngine(MongoEngine mongoEngine) {
        this.mongoEngine = mongoEngine;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void setCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public boolean update() {
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

    public FindIterable<Document> getAllHospitalData(){
        FindIterable<Document> hosFilteredAllData = this.collection.find();
//        for(Document myDoc : hosFilteredAllData){
//            System.out.println("Patient status: "+myDoc.get("patient_status"));
//        }
        return hosFilteredAllData;
    }

    public FindIterable<Document>  getSpecificHospitalData(String hid){
        //in-paitent = 1, icu = 2, vent =3
        Document hospitalDocument = new Document();
        hospitalDocument.append("hospital_id", Integer.parseInt(hid));
        FindIterable<Document> hosFilteredData = this.collection.find(hospitalDocument);
//        for(Document myDoc : hosFilteredData){
//            System.out.println("Patient status: "+myDoc.get("patient_status"));
//        }

        return hosFilteredData;

    }
}
