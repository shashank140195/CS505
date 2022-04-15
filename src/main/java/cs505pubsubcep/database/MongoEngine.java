package cs505pubsubcep.database;

public class MongoEngine {

    public void connect(){
        ConnectionString connectionString = new ConnectionString("mongodb+srv://cs505:<password>@cluster0.h4pa4.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("test");
    }
}
