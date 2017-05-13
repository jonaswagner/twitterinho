package ch.uzh.ase.data;

import ch.uzh.ase.Application;
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
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.*;

/**
 * Created by Silvio Fankhauser on 26.04.2017.
 */
public class DB {

    private final MongoDatabase mdb;
    private final MongoClient mongoClient;
    private final MongoCollection mc;
    private static final String ISO = "iso";
    private static final String SENTIMENT = "sentiment";
    private static final String TEXT = "text";
    private static final String AUTHOR = "author";
    private static final String DATE = "date";
    private static final String SEARCH_ID = "searchID";
    private static final String COLLECTION_NAME = "collection";
    private final String LANGUAGE_DETECTION_DURATION = "languageDetectionDuration";
    private final String SENTIMENT_DETECTION_DURATION = "sentimentDetectionDuration";
    private final String PROCESSING_TIME = "processingTime";

    public DB() {
        mongoClient = new MongoClient(new MongoClientURI(Application.getProp().getProperty("databaseconnection")));
        mdb = mongoClient.getDatabase(Application.getProp().getProperty("dbname"));
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
        DateTime startLangDetTime = tweet.getStartLangDetection();
        DateTime endLangDetTime = tweet.getEndLangDetection();
        DateTime startSentDetTime = tweet.getStartSentimentAnalysis();
        DateTime endSentDetTime = tweet.getEndSentimentAnalysis();
        DateTime newTweetTime = tweet.getFlaggedNew();
        DateTime finishedTweetTime = tweet.getFlaggedFinished();

        Interval langDetTimeInterval = new Interval(startLangDetTime, endLangDetTime);
        Interval sentDetTimeInterval = new Interval(startSentDetTime, endSentDetTime);
        Interval processingTimeInterval = new Interval(newTweetTime, finishedTweetTime);

        String langDetTime  = String.valueOf(langDetTimeInterval.toDurationMillis());
        String sentDetTime  = String.valueOf(sentDetTimeInterval.toDurationMillis());
        String processingTime = String.valueOf(processingTimeInterval.toDurationMillis());

        Document doc = new Document(ISO, iso)
                .append(SENTIMENT, sentiment)
                .append(TEXT, text)
                .append(AUTHOR, author)
                .append(DATE, date)
                .append(SEARCH_ID, searchID)
                .append(LANGUAGE_DETECTION_DURATION, langDetTime)
                .append(SENTIMENT_DETECTION_DURATION, sentDetTime)
                .append(PROCESSING_TIME, processingTime);
        mc.insertOne(doc);
    }

    public double getAverageSentiment(String searchId) {
        double sum = 0.0;
        int numberOfTweets = 0;
        BasicDBObject query = new BasicDBObject(SEARCH_ID, searchId);
        MongoCursor<Document> cursor = mc.find(query).iterator();
        try {
            while (cursor.hasNext()) {
                sum = sum + Double.parseDouble(cursor.next().getString(SENTIMENT));
                numberOfTweets++;
            }
        } finally {
            cursor.close();
        }
        return sum / (double) numberOfTweets;
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

    public Map<String, Double> getAllAverageSentiments() {
        Map<String, Double> resultMap = new HashMap<>();
        Set<String> searchIDs = getDistinctSearchIDs();
        for (String searchId : searchIDs) {
            resultMap.put(searchId, getAverageSentiment(searchId));
        }
        return resultMap;
    }

    public void deleteCollection() {
        mc.drop();
    }

    public void deleteSearchIdEntries(String searchId){
        BasicDBObject query = new BasicDBObject();
        query.append(SEARCH_ID, searchId);
        mc.deleteMany(query);
    }

    public long getEvaluationTime(String key){
        long sum = 0;
        int numberOfTweets = 0;
        MongoCursor<Document> cursor = mc.find().iterator();
        try {
            while (cursor.hasNext()) {
                sum = sum + Long.parseLong(cursor.next().getString(key));
                numberOfTweets++;
            }
        } finally {
            cursor.close();
        }
        return sum / numberOfTweets;
    }

    public Map<String, Long> getTermStatistics(){
        Map<String, Long> resultMap = new HashMap<>();

        long langDetTime = getEvaluationTime(LANGUAGE_DETECTION_DURATION);
        long sentDetTime = getEvaluationTime(SENTIMENT_DETECTION_DURATION);
        long procTime = getEvaluationTime(PROCESSING_TIME);

        resultMap.put(LANGUAGE_DETECTION_DURATION, langDetTime);
        resultMap.put(SENTIMENT_DETECTION_DURATION, sentDetTime);
        resultMap.put(PROCESSING_TIME, procTime);

        return resultMap;
    }



}
