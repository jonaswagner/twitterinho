package ch.uzh.ase.data;

import ch.uzh.ase.TestDriver;
import ch.uzh.ase.Util.Tweet;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Silvio Fankhauser on 26.04.2017.
 */
public class DB {

    private MongoDatabase mdb;
    private MongoClient mongoClient;
    private MongoCollection mc;
    private final String ISO = "iso";
    private final String SENTIMENT = "sentiment";
    private final String TEXT = "text";
    private final String AUTHOR = "author";
    private final String DATE = "date";
    private final String SEARCH_ID = "searchID";
    private final String COLLECTION_NAME = "collection";

    public DB(){
        mongoClient = new MongoClient(new MongoClientURI(TestDriver.getProp().getProperty("databaseconnection")));
        mdb= mongoClient.getDatabase(TestDriver.getProp().getProperty("dbname"));
        mdb.withWriteConcern(WriteConcern.JOURNALED);
        mc = mdb.getCollection(COLLECTION_NAME);
    }

    public void persist(Tweet tweet) {
        String iso = tweet.getIso().toString();
        String sentiment = new String (""+tweet.getSentimentScore());
        String text = tweet.getText();
        String author = tweet.getAuthor();
        String date = tweet.getDate().toString();
        String searchID = tweet.getSearchID();

        Document doc = new Document(ISO, iso)
                .append(SENTIMENT, sentiment)
                .append(TEXT, text)
                .append(AUTHOR, author)
                .append(DATE, date)
                .append(SEARCH_ID, searchID);
            mc.insertOne(doc);
    }

    public double getAverageSentiment(String searchId){
        double sum = 0.0;
        int numberOfTweets = 0;
        BasicDBObject query = new BasicDBObject(SEARCH_ID, searchId);
        MongoCursor<Document>  cursor = mc.find(query).iterator();
        try {
            while (cursor.hasNext()) {
                sum = sum + Double.parseDouble(cursor.next().getString(SENTIMENT));
                numberOfTweets++;
            }
        } finally {
            cursor.close();
        }
        double average = sum/(double)numberOfTweets;
        return average;
    }


    public Set<String> getDistinctSearchIDs() {
        Set<String> searchIDs = new HashSet<>();
        MongoCursor<Document> cursor = mc.find().iterator();
        try {
            while (cursor.hasNext()) {
                searchIDs.add(cursor.next().getString(SEARCH_ID));
            }
        } finally {
            cursor.close();
        }
        return searchIDs;
    }


    public Map<String, Double> getAllAverageSentiments(){
        Map<String, Double> resultMap = new HashMap();
        Set<String> searchIDs = getDistinctSearchIDs();
        for (String searchId : searchIDs){
            resultMap.put(searchId, getAverageSentiment(searchId));
        }
        return resultMap;
    }


}
