package ch.uzh.ase.data;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.config.Configuration;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Silvio Fankhauser on 26.04.2017.
 *
 * This class holds the database connection and queries.
 */
public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(DB.class);
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
    private final int QUEUE_SIZE = 5;

    private Map<String, Queue<Double>> lastTweetSentMap = new HashMap<>();
    private Queue<Document> lastPersistedTweets = new ArrayBlockingQueue<>(QUEUE_SIZE);




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

        long langDetTimeInterval = getTimeInterval(startLangDetTime, endLangDetTime);
        long sentDetTimeInterval = getTimeInterval(startSentDetTime, endSentDetTime);
        long processingTimeInterval = getTimeInterval(newTweetTime, finishedTweetTime);

        Document doc = new Document(ISO, iso)
                .append(SENTIMENT, sentiment)
                .append(TEXT, text)
                .append(AUTHOR, author)
                .append(DATE, date)
                .append(SEARCH_ID, searchID)
                .append(LANGUAGE_DETECTION_DURATION, String.valueOf(langDetTimeInterval))
                .append(SENTIMENT_DETECTION_DURATION, String.valueOf(sentDetTimeInterval))
                .append(PROCESSING_TIME, String.valueOf(processingTimeInterval));
        mc.insertOne(doc);
        insertInTweetQueue(doc);

    }

    private long getTimeInterval(DateTime start,DateTime end){
        long interval = 0;
        Interval timeInterval = new Interval(start, end);
        interval = timeInterval.toDurationMillis();
        return interval;
    }

    /**
     * If lastPersistedTweets is full, then discard the oldest {@link Tweet} and insert the new one.
     * @param doc
     */
    private void insertInTweetQueue(Document doc) {
        if (lastPersistedTweets.size() >= QUEUE_SIZE){
            lastPersistedTweets.poll();
        }
        lastPersistedTweets.add(doc);
    }

    /**
     * This method retrieves all {@link Tweet}s for the corresponding {@link ch.uzh.ase.domain.Term} and calculates the average Sentiment.
     *
     * @param searchId
     * @return
     */
    public double getAverageSentiment(String searchId) {
        double sum = 0.0;
        int numberOfTweets = 0;
        BasicDBObject query = new BasicDBObject(SEARCH_ID, searchId);
        MongoCursor<Document> cursor = mc.find(query).iterator();
        if (!lastTweetSentMap.containsKey(searchId)){
           Queue<Double> lastTweets = new ArrayBlockingQueue<>(QUEUE_SIZE);
           lastTweetSentMap.put(searchId, lastTweets) ;
        }
        try {
            while (cursor.hasNext()) {
                Document next = cursor.next();
                double sent = Double.parseDouble(next.getString(SENTIMENT));
                sum = sum + sent;
                numberOfTweets++;
                insertInQueue(searchId, sent);
            }
        }  catch(MongoCursorNotFoundException mcnf){
        } finally {
            cursor.close();
        }
        return Math.round((sum / (double) numberOfTweets) * 100.0) / 100.0;
    }

    private void insertInQueue(String searchId, Double sent) {
        if (lastTweetSentMap.get(searchId).size() >= QUEUE_SIZE){
            lastTweetSentMap.get(searchId).poll();
        }
        lastTweetSentMap.get(searchId).add(sent);
    }

    /**
     * This method calculates the average sentiment of the retrieved {@link Tweet}s given the {@link ch.uzh.ase.domain.Term}.
     * @param searchId
     * @return
     */
    public double getCurrentSentiment(String searchId) {
        Queue<Double> queue = lastTweetSentMap.get(searchId);
        Double average = 0.0;
        for(Double sent : queue){
            average = average + sent;
        }
        return average/QUEUE_SIZE;
    }

    /**
     * This method retrieves the entire {@link MongoCollection}, because Microsoft Azure does not support the distinct search operation
     * (see also https://stackoverflow.com/questions/30089803/how-could-i-go-about-getting-distinct-field-counts-in-azure-search).
     * @return {@link Set<String>} searchIds
     */
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

    /**
     * This method calculates monitoring values needed by the client.
     * @return {@link Map<String, Long> termStatistics}
     */
    public Map<String, Long> getTermStatistics()  {
        Map<String, Long> resultMap = new HashMap<>();
        long langDetTime = 0;
        long sentDetTime = 0;
        long procTime = 0;

        List<Long> langDetTimeList = new ArrayList<>();
        List<Long> sentDetTimeList = new ArrayList<>();
        List<Long> procTimeList = new ArrayList<>();

        for (Document next : lastPersistedTweets){
                langDetTimeList.add(Long.parseLong(next.getString(LANGUAGE_DETECTION_DURATION)));
                sentDetTimeList.add(Long.parseLong(next.getString(SENTIMENT_DETECTION_DURATION)));
                procTimeList.add(Long.parseLong(next.getString(PROCESSING_TIME)));
        }

        for (int i = 0; i < langDetTimeList.size(); i++ ){
            langDetTime = langDetTime + langDetTimeList.get(i);
            sentDetTime = sentDetTime + sentDetTimeList.get(i);
            procTime = procTime + procTimeList.get(i);
        }
        if (!lastPersistedTweets.isEmpty()) {
            resultMap.put(LANGUAGE_DETECTION_DURATION, langDetTime / lastPersistedTweets.size());
            resultMap.put(SENTIMENT_DETECTION_DURATION, sentDetTime / lastPersistedTweets.size());
            resultMap.put(PROCESSING_TIME, procTime / lastPersistedTweets.size());
        }
        return resultMap;
    }


}
