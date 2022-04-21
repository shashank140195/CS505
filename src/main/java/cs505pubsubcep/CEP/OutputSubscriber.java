package cs505pubsubcep.CEP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import cs505pubsubcep.Launcher;
import io.siddhi.core.util.transport.InMemoryBroker;
import io.siddhi.query.api.expression.condition.In;

// import javax.xml.bind.JAXBContext;
// import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.sql.Struct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Alert {
    private String zip;
    private int count;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Alert(String zip, int count) {
        this.zip = zip;
        this.count = count;
    }
}

class RabbitEvent{

    private Map<String, Alert> rabbit;

    public RabbitEvent(Map<String, Alert> rabbit) {
        this.rabbit = rabbit;
    }

    public Map<String, Alert> getRabbit() {
        return rabbit;
    }

    public void setRabbit(Map<String, Alert> rabbit) {
        this.rabbit = rabbit;
    }
}

class TmpClass{
    private Map<String, List<RabbitEvent>> TmpMap;

    public Map<String, List<RabbitEvent>> getTmpMap() {
        return TmpMap;
    }

    public void setTmpMap(Map<String, List<RabbitEvent>> tmpMap) {
        TmpMap = tmpMap;
    }

    public TmpClass(Map<String, List<RabbitEvent>> tmpMap) {
        TmpMap = tmpMap;
    }
}


public class OutputSubscriber implements InMemoryBroker.Subscriber {

    private String topic;


    public OutputSubscriber(String topic, String streamName) {
        this.topic = topic;
    }

    @Override
    public void onMessage(Object msg) {

        try {
            String m = String.valueOf(msg);
            System.out.println("OUTPUT CEP EVENT: " + m);

//            Pattern pattern = Pattern.compile("\\{\"event\":\\{\"zip_code\":(.*?),\"count\":(.*?)\\}\\}");

            String first = null;
            String second = null;

            if(m.contains("[")) {
                System.out.println("String contains [");
                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                Matcher matcher = pattern.matcher(m);


                if (matcher.find()) {
                    first = matcher.group(1);
//                second = matcher.group(2);
                }
            }else{
                System.out.println("String donot contains [");
                first = m;
            }
//            System.out.printf("First: %s\nSecond: %s\n", first, second);

            first = first.replaceAll("\\{\"event\":","").replaceAll("\\}\\}","\\}").
                    replaceAll("\\},\\{","\\};\\{");

//            System.out.printf("First: %s\nSecond: %s\n", first, second);

            String[] alerts = first.split(";");
            Map<Integer,Integer> zipMap = new HashMap<Integer,Integer>();
            for(String p:alerts){
//                System.out.println("P: "+p);
                Pattern pat = Pattern.compile("\\{\"zip_code\":\"(.*?)\",\"count\":(.*?)\\}");
                Matcher mat = pat.matcher(p);
                String zip1=null;
                String count1=null;
                if (mat.find()) {
                    zip1 = mat.group(1);
                    count1 = mat.group(2);
                }
//                System.out.println("Zip: "+zip1+", Count: "+count1);
                if(zip1!=null && count1!=null) {
                    zipMap.put(Integer.parseInt(zip1), Integer.parseInt(count1));
                }
            }
            System.out.println(zipMap.toString());
            Launcher.common.clear();
            if(Launcher.zipAlertCount!=null){
                System.out.println("Zip alert list is not null");
                for(Integer key : zipMap.keySet()){
                    if(Launcher.zipAlertCount.containsKey(key)){
                        if(zipMap.get(key)>=2*Launcher.zipAlertCount.get(key)) {
                            Launcher.common.add(key);
                        }
                    }
                }
//                Set<Integer> result = zipMap.keySet().stream()
//                        .filter(keyB -> Launcher.zipAlertCount.keySet().stream()
//                                .filter(keyA -> Launcher.zipAlertCount.get(keyA).equals(zipMap.get(keyB)))
//                                .count() > 0).collect(Collectors.toSet());
                System.out.println("ZipAlertPrev: "+Launcher.zipAlertCount);
                System.out.println("Present: "+zipMap);
                System.out.println("Alert: "+Launcher.common);
                if(Launcher.common.size()>0){
                    System.out.println("<<<<<<<<<<=============== ALERT ===============>>>>>>>>>>>>>>");
                }
            }else{
                System.out.println("Zip alert list is NULL");
            }
            Launcher.zipAlertCount = zipMap;


            //Launcher.accessCount = Long.parseLong(outval[0]);

        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public String getTopic() {
        return topic;
    }

}
