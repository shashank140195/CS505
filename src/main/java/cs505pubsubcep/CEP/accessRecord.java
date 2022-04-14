package cs505pubsubcep.CEP;

public class accessRecord {
    String zip_code;
    long timestamp;

    public accessRecord(String zip_code, long timestamp) {
        this.zip_code = zip_code;
        this.timestamp = timestamp;
    }
    public String getZipCode() {return this.zip_code;}
    public long getTs() {return this.timestamp;}

    @Override
    public String toString() {
        return "zip_code:" + zip_code + " timestamp:" + timestamp;
    }
}

/*
{
        "remote_ip":  "127.0.0.1",							 #A
        "timestamp": "1576600245000",						 #B
        }
*/