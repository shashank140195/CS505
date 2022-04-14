package cs505pubsubcep.database;

import com.google.gson.reflect.TypeToken;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
// import com.orientechnologies.orient.core.db.ODatabasePool;
// import com.orientechnologies.orient.core.db.ODatabaseSession;
// import com.orientechnologies.orient.core.db.ODatabaseType;
// import com.orientechnologies.orient.core.db.OrientDB;
// import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
// import com.orientechnologies.orient.core.util.OURLConnection;
// import com.orientechnologies.orient.core.util.OURLHelper;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DerbyDBEngine {


    public DerbyDBEngine(){
        try {

            

            // OPEN THE DATABASE
            // OrientDB orientDB = new OrientDB("remote:localhost", null);
            // ODatabaseDocument db = orientDB.open("teamdb", "root", "rootpwd");
            

            // System.out.print("Connected!");

            // // CREATE A NEW DOCUMENT AND FILL IT
            // ODocument doc = new ODocument("Person");
            // doc.field( "name", "Luke" );
            // doc.field( "surname", "Skywalker" );
            // doc.field( "city", new ODocument("City")
            // .field("name","Rome")
            // .field("country", "Italy") );

            // // SAVE THE DOCUMENT
            // db.save(doc);

            // db.close();
            // orientDB.close();

            // System.out.println("Written random data!");

            // OPEN THE DATABASE
ODatabaseDocumentTx db = new ODatabaseDocumentTx(
    "localhost:2424/teamdb").open("root", "rootpwd");
 
 // CREATE A NEW DOCUMENT AND FILL IT
 ODocument doc = new ODocument("Person");
 doc.field( "name", "Luke" );
 doc.field( "surname", "Skywalker" );
 doc.field( "city", new ODocument("City")
    .field("name","Rome")
    .field("country", "Italy") );
 
 // SAVE THE DOCUMENT
 doc.save();
 
 db.close();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }



        
    
}
