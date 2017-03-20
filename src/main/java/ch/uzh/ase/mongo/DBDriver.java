package ch.uzh.ase.mongo;


        import com.mongodb.*;
        import com.mongodb.client.FindIterable;
        import com.mongodb.client.MongoCollection;
        import com.mongodb.client.MongoCursor;
        import com.mongodb.client.MongoDatabase;
        import org.bson.Document;

        import java.net.URI;
        import java.util.List;
        import java.util.Random;
        import java.util.Set;

/**
 * Created by jonas on 15.03.2017.
 */
public class DBDriver {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        // DB db = mongoClient.getDB("testtweets");
        MongoDatabase mdb = mongoClient.getDatabase("testtweets");

        //DBCollection col = db.getCollection("tweets");
        //MongoCollection mc = mdb.getCollection("tweets");

        //mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        //mdb.createCollection("test1");
        mdb.withWriteConcern(WriteConcern.JOURNALED);
        MongoCollection mc = mdb.getCollection("test1");
        //BasicDBObject doc1 = new BasicDBObject("tweet", "MongoDB").append("Tweet_ID", "777");

        //mc.insertOne(doc1);
        //Document doc = new Document("name", "MongoDB")
        //        .append("type", "database")
        //        .append("count", 1)
        //        .append("info", new Document("x", 203).append("y", 102));
        //mc.insertOne(doc);

        Document doc2 = new Document("tweetID", "777")
                .append("name", "dini mueter");
        mc.insertOne(doc2);



        //BasicDBObject query = new BasicDBObject("Tweet_ID", 777);
        MongoCursor<Document> cursor = mc.find().iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

    }


}
