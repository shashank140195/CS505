package cs505pubsubcep.Utils;

import org.bson.Document;

public interface DBImpl {
    public abstract boolean update();
    public abstract boolean delete();
    public abstract boolean insert(Document document);
}
