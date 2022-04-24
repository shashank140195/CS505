package cs505pubsubcep.database;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.types.Node;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeoEngine implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NeoEngine.class.getName());
    private final Driver driver;

    

    public NeoEngine(String uri, String user, String password, Config config) {
        // The driver is a long living object and should be opened during the start of your application
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
    }

    @Override
    public void close() throws Exception {
        // The driver object should be closed before the application ends.
        driver.close();
    }

    public void clear(){
        String query="MATCH (n) DETACH DELETE n";
        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(query, new HashMap<String, Object>());
                System.out.println(result==null);
                return null;
            });
            System.out.println(String.format("Deleted all nodes"));
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            throw ex;
        }
    }

    public Result run(String query, Map<String, Object> params){
        boolean success = true;

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(query, params);
                return null;
            });
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            return null;
        }
        return null;
    }


    public List<Record> runQuery(String query, Map<String, Object> params){
        boolean success = true;

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            List<Record> res = session.writeTransaction(tx -> {
                Result result = tx.run(query, params);
                List<Record> mylist = result.list();
                System.out.println("Session: "+session.isOpen());
                session.close();
                return mylist;
            });
            return res;
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
        }
        return null;
    }

    public Map<String, ArrayList<String>> getContactEvents(String mrn){
        /*


        match (e:Event)<-[r:attend]-(p:Patient)
        call {
            with p
            match (p)<-[r1:contact]->(p2)
            where p2.mrn='b47197a2-c31b-11ec-859a-3af9d3a61d88'
            return p as otherpat
        }
        return otherpat, e

         */
        /*
        match (e:Event)<-[r:attend]-(p:Patient) call { with p match (p)<-[r1:contact]->(p2) where p2.mrn='b47197a2-c31b-11ec-859a-3af9d3a61d88' return p as otherpat } return otherpat, e
        */

        String q = "match (e:Event)<-[r:attend]-(p:Patient) call { with p match (p)<-[r1:contact]->(p2) where p2.mrn=$mrn return p as otherpat } return otherpat, e";
        Map<String, Object> p = new HashMap<>();
        p.put("mrn", mrn);
        List<Record> masterRecord = runQuery(q, p);
//        System.out.println(masterRecord);

        Map<String, ArrayList<String>> contactEventMap = new HashMap<String, ArrayList<String>>();
        for(Record record : masterRecord){
//            System.out.println(record);
            Node patNode = record.get("otherpat").asNode();
            Node evNode = record.get("e").asNode();
            System.out.println("Event: "+evNode.labels()+", patient: "+patNode.labels());
            String eid = evNode.get("id").asString();
            if(!contactEventMap.containsKey(eid)){
                contactEventMap.put(eid,  new ArrayList<String>());
            }
            for(String label1 : patNode.labels()){
                String pmrn = patNode.get("mrn").asString();
                contactEventMap.get(eid).add(pmrn);
            }


        }
        System.out.println(contactEventMap);
        return contactEventMap;

    }

    public List<String> getContactMrn(String mrn){
        List<String> contactMrn = new ArrayList<String>();

        String q, key;
        Map<String, Object> p = new HashMap<>();

        q = "match (p1:Patient)<-[r:contact]->(p2:Patient) where p1.mrn=$mrn return p2";
        p.clear();
        p.put("mrn",mrn);

        key = "p2";

        List<Record> rec = runQuery(q, p);
        System.out.println(rec);
        for(Record record: rec){
            Node node = record.get(key).asNode();
            String neighbour = node.get("mrn").asString();
            if(!neighbour.equals(mrn)) {
                contactMrn.add(node.get("mrn").asString());
            }

        }

        return contactMrn;
    }

    public void testQuery(String q, Map<String, Object> p, String key){
        List<Record> rec = runQuery(q, p);
        System.out.println(rec);
        for(Record record: rec){
            System.out.println("Keys: "+record.keys());
            Node node = record.get(key).asNode();
            System.out.println(node.labels());

            for(String label : node.labels()){
                if(label.equalsIgnoreCase("Event")){
                    System.out.println("Event id: "+node.get("id"));
                }else if(label.equalsIgnoreCase("Patient")){
                    System.out.println("mrn: "+node.get("mrn"));
                }
            }

        }
    }


    public void processEventMrn(String mainMRN, List<String> contactList, List<String> eventList){

        for(String contact : contactList){
            Map<String, Object> contactParam = new HashMap<String, Object>();
            String query = "merge (p:Patient {mrn:$mrn})";
            contactParam.put("mrn", contact);
            Result resContact = run(query, contactParam);
        }

        Map<String, Object> tmpParam = new HashMap<String, Object>();
        String myquery = "merge (p:Patient {mrn:$mrn})";
        tmpParam.put("mrn", mainMRN);
        Result resTmpContact = run(myquery, tmpParam);

        for(String ev : eventList){
            Map<String, Object> eventParam = new HashMap<String, Object>();
            String query = "merge (p:Event {id:$evid})";
            eventParam.put("evid", ev);
            Result resContact = run(query, eventParam);
        }

        //Relation creation
        for(String contact : contactList){
            Map<String, Object> contactParam = new HashMap<String, Object>();
            String query = "match (p1:Patient), (p2:Patient) where p1.mrn=$mainMRN " +
                    "and p2.mrn=$mrn merge(p1)-[r:contact]->(p2) return type(r)";
            contactParam.put("mrn", contact);
            contactParam.put("mainMRN", mainMRN);
            Result resContact = run(query, contactParam);
        }

        for(String ev : eventList){
            Map<String, Object> Param = new HashMap<String, Object>();
            String query = "match (p:Patient), (e:Event) where p.mrn=$mainMRN " +
                    "and e.id=$evid merge(p)-[r:attend]->(e) return type(r)";
            Param.put("evid", ev);
            Param.put("mainMRN", mainMRN);
            Result resContact = run(query, Param);
        }
    }

}