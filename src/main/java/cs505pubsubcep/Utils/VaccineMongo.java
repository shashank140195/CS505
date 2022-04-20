package cs505pubsubcep.Utils;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cs505pubsubcep.database.MongoEngine;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;

public class VaccineMongo implements DBImpl{

    public MongoEngine mongoEngine;
    public MongoDatabase mongoDatabase;
    public MongoCollection<Document> collection;
    public String COLLECTION_NAME = "vaccine";

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

    public VaccineMongo(MongoEngine mongoEngine, MongoDatabase mongoDatabase) {
        this.mongoEngine = mongoEngine;
        this.mongoDatabase = mongoDatabase;
        this.collection = this.mongoDatabase.getCollection(COLLECTION_NAME);
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

    public boolean getVaccinationData(String mrn){
        boolean isVaccinated=false;
        Document vaxData = new Document();
        vaxData.append("patient_mrn", mrn);
        FindIterable<Document> patVaxData = this.collection.find(vaxData);
        if(patVaxData!=null){
            for(Document vax : patVaxData){
//                System.out.println(vax.get("patient_mrn") + " is vaccinated. vax id: "+vax.get("vaccination_id"));
                isVaccinated=true;
            }
        }

        return isVaccinated;
    }
}
