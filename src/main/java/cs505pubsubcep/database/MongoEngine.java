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

 import static com.mongodb.client.model.Filters.*;
 import static com.mongodb.client.model.Updates.*;

public class MongoEngine {
    public MongoEngine() {



        MongoClient client = MongoClients.create("mongodb+srv://diginova:diginova@cluster0.dqmo2.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");

        MongoDatabase database = client.getDatabase("CS505Doc");
        MongoCollection<Document> teamdb = database.getCollection("teamdb");
        FindIterable<Document> team = teamdb.find();
        System.out.println("teamdb: "+team.first());


    }

    public void connect(){

    }
}
