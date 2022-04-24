package cs505pubsubcep.httpcontrollers;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import cs505pubsubcep.Launcher;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.*;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import cs505pubsubcep.Models.Team;
import cs505pubsubcep.Utils.Constants;
import org.bson.Document;

@Path("/api")
public class API {

    @Inject
    private javax.inject.Provider<org.glassfish.grizzly.http.server.Request> request;

    private Gson gson;

    public API() {
        gson = new Gson();
    }

    //check local
    //curl --header "X-Auth-API-key:1234" "http://localhost:8082/api/getteam"

    //check remote
    //curl --header "X-Auth-API-key:1234" "http://[linkblueid].cs.uky.edu:8082/api/getteam"
    //curl --header "X-Auth-API-key:1234" "http://localhost:8081/api/getteam"

    //check remote
    //curl --header "X-Auth-API-key:1234" "http://[linkblueid].cs.uky.edu:8081/api/getteam"

    public boolean reset(){
        try {

            System.out.println("Clearing neo4j!");
            Launcher.neoApp.clear();
            Launcher.eventMongo.delete();
            Document doc = new Document();
            doc.append("type", "event");
            Launcher.eventMongo.insert(doc);
            Launcher.contactMongo.delete();
            doc = new Document();
            doc.append("type", "contact");
            Launcher.contactMongo.insert(doc);

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

//            ArrayList<String> cList = Launcher.contactMongo.getContactList(mrn);
            Map<String,Object> res = new HashMap<String, Object>();
            res.put("contactlist", Launcher.neoApp.getContactMrn(mrn));
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
            int status = (Launcher.common.size()>=Constants.ZIP_ALERT_LENGTH)?1:0;
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

//            ArrayList<String> cList = Launcher.contactMongo.getContactList(mrn);
            Map<String, Object> res = new HashMap<String, Object>();
//            if(cList.size()>0 && cList!=null) {
////                res.put("contactlist", cList.get(0));
//                Map<String, ArrayList<String>> res1 = Launcher.eventMongo.getEventContactList(cList);
//                res.put("contactlist", res1);
//            }else{
//                res.put("contactlist", cList);
//            }

            res.put("contactlist", (Map<String, ArrayList<String>>)Launcher.neoApp.getContactEvents(mrn));
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
}
