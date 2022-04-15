package cs505pubsubcep.CEP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.siddhi.core.util.transport.InMemoryBroker;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
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

            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(m);

            String first = null;
            String second = null;

            if (matcher.find()) {
                first = matcher.group(1);
//                second = matcher.group(2);
            }
            System.out.printf("First: %s\nSecond: %s\n", first, second);

            first = first.replaceAll("\\{\"event\":","").replaceAll("\\}\\}","\\}").
                    replaceAll("\\},\\{","\\};\\{");

            System.out.printf("First: %s\nSecond: %s\n", first, second);

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
