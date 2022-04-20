package cs505pubsubcep.httpcontrollers;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import cs505pubsubcep.CEP.OutputSubscriber;
import cs505pubsubcep.CEP.accessRecord;
import cs505pubsubcep.Launcher;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.*;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import cs505pubsubcep.Models.Team;
import cs505pubsubcep.Models.Vaccination;
import cs505pubsubcep.Utils.ContactMongo;
import cs505pubsubcep.Utils.EventMongo;
import cs505pubsubcep.database.MongoEngine;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@Path("/api")
public class API {

    @Inject
    private javax.inject.Provider<org.glassfish.grizzly.http.server.Request> request;

    private Gson gson;

    public API() {
        gson = new Gson();
    }

    //check local
    //curl --header "X-Auth-API-key:1234" "http://localhost:8082/api/checkmycep"

    //check remote
    //curl --header "X-Auth-API-key:1234" "http://[linkblueid].cs.uky.edu:8082/api/checkmycep"
    //curl --header "X-Auth-API-key:1234" "http://localhost:8081/api/checkmycep"

    //check remote
    //curl --header "X-Auth-API-key:1234" "http://[linkblueid].cs.uky.edu:8081/api/checkmycep"

    @GET
    @Path("/checkmycep")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkMyEndpoint(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //get remote ip address from request
            String remoteIP = request.get().getRemoteAddr();
            //get the timestamp of the request
            long access_ts = System.currentTimeMillis();
            System.out.println("IP: " + remoteIP + " Timestamp: " + access_ts);

            Map<String,String> responseMap = new HashMap<>();
            if(Launcher.cepEngine != null) {

                    responseMap.put("success", Boolean.TRUE.toString());
                    responseMap.put("status_desc","CEP Engine exists");

            } else {
                responseMap.put("success", Boolean.FALSE.toString());
                responseMap.put("status_desc","CEP Engine is null!");
            }

            responseString = gson.toJson(responseMap);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }


    public boolean reset(){
        try {
//            Launcher.mongoEngine.delete(Launcher.mongoDatabase, "patient");
//            Launcher.mongoEngine.delete(Launcher.mongoDatabase, "event");
            Launcher.eventMongo.delete();
            Document doc = new Document();
            doc.append("type", "event");
            Launcher.eventMongo.insert(doc);
//            Launcher.mongoEngine.insert(doc, Launcher.mongoDatabase, "event");
//            Launcher.mongoEngine.delete(Launcher.mongoDatabase, "contact");
            Launcher.contactMongo.delete();
            doc = new Document();
            doc.append("type", "contact");
            Launcher.contactMongo.insert(doc);
//            Launcher.mongoEngine.insert(doc, Launcher.mongoDatabase, "contact");
//            Launcher.eventMongo = new EventMongo(Launcher.mongoEngine, Launcher.mongoDatabase);
//            Launcher.contactMongo = new ContactMongo(Launcher.mongoEngine, Launcher.mongoDatabase);

            Launcher.vaccineMongo.delete();
            Launcher.hospitalMongo.delete();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @GET
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResetStatus(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {
            Map<String,Object> res = new HashMap<String, Object>();
            if(reset()){
                res.put("reset_status_code", 1);
            }else{
                res.put("reset_status_code", 0);
            }

            responseString = gson.toJson(res);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/getconfirmedcontacts/{mrn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfirmedContacts(@HeaderParam("X-Auth-API-Key") String authKey,
                                         @PathParam("mrn") String mrn) {
        String responseString = "{}";
        try {

            ArrayList<String> cList = Launcher.contactMongo.getContactList(mrn);
            Map<String,Object> res = new HashMap<String, Object>();
            res.put("contactlist", cList);
//            if(cList.size()>0) {
//                res.put("contactlist", cList.get(0));
//            }else{
//                res.put("contactlist", cList);
//            }
            responseString = gson.toJson(res);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }


    ///api/getpatientstatus/{hospital_id}
    @GET
    @Path("/getpatientstatus/{hospital_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecificHospitalStatus(@HeaderParam("X-Auth-API-Key") String authKey,
                                        @PathParam("hospital_id") String hid) {
        String responseString = "{}";

        Map<String, Object> res = new HashMap<String, Object>();
        String[] statusNames = {"in", "icu", "vent"};
        String constKeyCount = "_patient_count";
        String constKeyVax = "_patient_vax";
        try {
            //in-paitent = 1, icu = 2, vent =3

            System.out.println("For hid: "+hid);
            FindIterable<Document> hosSpecificData = Launcher.hospitalMongo.getSpecificHospitalData(hid);
            int countAll[] = {0,0,0};
            double[] vaxAll = {0.0, 0.0, 0.0};
            for(Document tmpHos : hosSpecificData){
                countAll[(Integer) tmpHos.get("patient_status")-1]++;
                if(Launcher.vaccineMongo.getVaccinationData((String) tmpHos.get("patient_mrn"))){
                    vaxAll[(Integer) tmpHos.get("patient_status")-1]++;
                }
            }

            for(int status=0;status<3;status++){
                res.put(statusNames[status]+constKeyCount, countAll[status]);
                res.put(statusNames[status]+constKeyVax, ((countAll[status]>0)?vaxAll[status]/countAll[status]:0));
//                System.out.println("Status : "+status+" Count: "+countAll[status]+" Vax: "+((countAll[status]>0)?vaxAll[status]/countAll[status]:0));
            }
            responseString = gson.toJson(res);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }


    //alertlist
    @GET
    @Path("/alertlist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlertList(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        Map<String, Object> res = new HashMap<String, Object>();
        try {
            int status = (Launcher.common.size()>=5)?1:0;
            res.put("state_status", status);
            responseString = gson.toJson(res);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }



    @GET
    @Path("/getpatientstatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHospitalStatus(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";

        Map<String, Object> res = new HashMap<String, Object>();
        String[] statusNames = {"in", "icu", "vent"};
        String constKeyCount = "_patient_count";
        String constKeyVax = "_patient_vax";
        try {
            //in-paitent = 1, icu = 2, vent =3

            FindIterable<Document> hosSpecificData = Launcher.hospitalMongo.getAllHospitalData();
            int count[] = {0,0,0};
            double[] vax = {0.0, 0.0, 0.0};
            for(Document tmpHos : hosSpecificData){
                count[(Integer) tmpHos.get("patient_status")-1]++;
                if(Launcher.vaccineMongo.getVaccinationData((String) tmpHos.get("patient_mrn"))){
                    vax[(Integer) tmpHos.get("patient_status")-1]++;
                }
            }

            for(int status=0;status<3;status++){
                res.put(statusNames[status]+constKeyCount, count[status]);
                res.put(statusNames[status]+constKeyVax, ((count[status]>0)?vax[status]/count[status]:0));
//                System.out.println("Status : "+status+" Count: "+countAll[status]+" Vax: "+((countAll[status]>0)?vaxAll[status]/countAll[status]:0));
            }
            responseString = gson.toJson(res);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/getpossiblecontacts/{mrn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPossibleContacts(@HeaderParam("X-Auth-API-Key") String authKey,
                                         @PathParam("mrn") String mrn) {
        String responseString = "{}";
        try {

            ArrayList<String> cList = Launcher.contactMongo.getContactList(mrn);
            Map<String, Object> res = new HashMap<String, Object>();
            if(cList.size()>0 && cList!=null) {
//                res.put("contactlist", cList.get(0));
                Map<String, ArrayList<String>> res1 = Launcher.eventMongo.getEventContactList(cList);
                res.put("contactlist", res1);
            }else{
                res.put("contactlist", cList);
            }

            responseString = gson.toJson(res);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/getteam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeam(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {


            final Type teamType = new TypeToken<List<Team>>(){}.getType();

//            MongoDatabase database = Launcher.mongoEngine.client.getDatabase("CS505Doc");
            MongoCollection<Document> teamdb = Launcher.mongoDatabase.getCollection("teamdb");
            FindIterable<Document> team = teamdb.find();
            System.out.println("teamdb: "+team.first());
//            Team team1 = gson.fromJson(team.first().toString(), teamType);
            Map<String,Object> res = new HashMap<String, Object>();

            res.put("team_name", team.first().get("team_name"));
            String sids=(String) team.first().get("team_member_sids");
            ArrayList<Integer> sidAList = new ArrayList<Integer>();
            String[] sidList = sids.split(",");
            for(String str : sidList){
                sidAList.add(Integer.parseInt(str));
            }
            res.put("team_member_sids", sidAList);
            res.put("app_status_code", Integer.parseInt((String) team.first().get("app_status_code")));
            responseString = gson.toJson(res);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }


    @GET
    @Path("/zipalertlist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getZipAlert(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //generate a response
            Map<String,Set<Integer>> responseMap = new HashMap<>();
            responseMap.put("ziplist",Launcher.common);
            responseString = gson.toJson(responseMap);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }









    @GET
    @Path("/getaccesscount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccessCount(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //get remote ip address from request
            String remoteIP = request.get().getRemoteAddr();
            //get the timestamp of the request
            long access_ts = System.currentTimeMillis();
            System.out.println("IP: " + remoteIP + " Timestamp: " + access_ts);

            //generate event based on access
            String inputEvent = gson.toJson(new accessRecord(remoteIP,access_ts));
            System.out.println("inputEvent: " + inputEvent);

            //send input event to CEP
            Launcher.cepEngine.input(Launcher.inputStreamName, inputEvent);

            //generate a response
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("accesscoint",String.valueOf(Launcher.accessCount));
            responseString = gson.toJson(responseMap);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }


}
