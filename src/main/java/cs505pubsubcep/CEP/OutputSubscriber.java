package cs505pubsubcep.CEP;


import cs505pubsubcep.Launcher;
import cs505pubsubcep.Utils.Constants;
import io.siddhi.core.util.transport.InMemoryBroker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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


            String first = null;
            String second = null;

            if(m.contains("[")) {
                System.out.println("String contains [");
                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                Matcher matcher = pattern.matcher(m);


                if (matcher.find()) {
                    first = matcher.group(1);
                }
            }else{
                System.out.println("String donot contains [");
                first = m;
            }

            first = first.replaceAll("\\{\"event\":","").replaceAll("\\}\\}","\\}").
                    replaceAll("\\},\\{","\\};\\{");

            String[] alerts = first.split(";");
            Map<Integer,Integer> zipMap = new HashMap<Integer,Integer>();
            for(String p:alerts){
                Pattern pat = Pattern.compile("\\{\"zip_code\":\"(.*?)\",\"count\":(.*?)\\}");
                Matcher mat = pat.matcher(p);
                String zip1=null;
                String count1=null;
                if (mat.find()) {
                    zip1 = mat.group(1);
                    count1 = mat.group(2);
                }
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
                        if(zipMap.get(key)>=(Constants.ZIP_MULTIPLIER * Launcher.zipAlertCount.get(key))) {
                            Launcher.common.add(key);
                        }
                    }
                }
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

        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public String getTopic() {
        return topic;
    }
}
