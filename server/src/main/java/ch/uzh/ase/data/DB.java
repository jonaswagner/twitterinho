package ch.uzh.ase.data;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.config.Configuration;
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

    private Properties properties = Configuration.getInstance().getProp();
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
        mongoClient = new MongoClient(new MongoClientURI(properties.getProperty("databaseconnection")));
        mdb = mongoClient.getDatabase(properties.getProperty("dbname"));
        mdb.withWriteConcern(WriteConcern.JOURNALED);
        mc = mdb.getCollection(COLLECTION_NAME);
    }

    public void persist(Tweet tweet) {
        String iso = tweet.getIso().toString();
        String sentiment = "" + tweet.getSentimentScore();
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

        String langDetTime = String.valueOf(langDetTimeInterval.toDurationMillis());
        String sentDetTime = String.valueOf(sentDetTimeInterval.toDurationMillis());
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
        return Math.round((sum / (double) numberOfTweets) * 100.0) / 100.0;
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

    public void deleteSearchIdEntries(String searchId) {
        BasicDBObject query = new BasicDBObject();
        query.append(SEARCH_ID, searchId);
        mc.deleteMany(query);
    }

    public Map<String, Long> getTermStatistics()  {
        Map<String, Long> resultMap = new HashMap<>();
        long langDetTime = 0;
        long sentDetTime = 0;
        long procTime = 0;
        MongoCursor<Document> cursor = mc.find().iterator();
        List<Long> langDetTimeList = new ArrayList<>();
        List<Long> sentDetTimeList = new ArrayList<>();
        List<Long> procTimeList = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                langDetTimeList.add(Long.parseLong(cursor.next().getString(LANGUAGE_DETECTION_DURATION)));
                sentDetTimeList.add(Long.parseLong(cursor.next().getString(SENTIMENT_DETECTION_DURATION)));
                procTimeList.add(Long.parseLong(cursor.next().getString(PROCESSING_TIME)));
            }
        } finally {
            cursor.close();
        }
        for (int i = langDetTimeList.size()-10; i < langDetTimeList.size(); i++ ){
            langDetTime = langDetTime + langDetTimeList.get(i);
            sentDetTime = sentDetTime + sentDetTimeList.get(i);
            procTime = procTime + procTimeList.get(i);
        }
        resultMap.put(LANGUAGE_DETECTION_DURATION, langDetTime/10);
        resultMap.put(SENTIMENT_DETECTION_DURATION, sentDetTime/10);
        resultMap.put(PROCESSING_TIME, procTime/10);
        return resultMap;
    }

}
