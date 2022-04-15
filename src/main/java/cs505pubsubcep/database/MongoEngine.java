package cs505pubsubcep.database;

public class MongoEngine {
    public MongoEngine() {


        ConnectionString connectionString = new ConnectionString("mongodb+srv://<username>:<username>@cluster0.dqmo2.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("CS505Doc");


    }

    public void connect(){

    }
}
