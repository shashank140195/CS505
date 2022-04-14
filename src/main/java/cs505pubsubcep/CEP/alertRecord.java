package cs505pubsubcep.CEP;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class event{
    private String zip_code;
    private int count;

    public String getZip_code(){
        return this.zip_code;
    }

    public int getCount(){
        return this.count;
    }

    @Override
    public String toString(){
        return "zip_code: " + getZip_code() + ", count: " + getCount();
    }
}

public class alertRecord {
    private ArrayList<Map<String, event>> alert;



    public ArrayList<Map<String, event>> getAlert(){
        return this.alert;
    }
}
