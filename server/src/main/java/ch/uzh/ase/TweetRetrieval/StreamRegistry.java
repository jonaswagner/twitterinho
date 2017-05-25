package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Blackboard.Blackboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a Singleton. It is responsible for all running {@link ch.uzh.ase.Util.Term}s and also holds a {@link Blackboard} instance.
 */
public class StreamRegistry {

    private static final StreamRegistry streamRegistry = new StreamRegistry();
    private Blackboard blackboard = null;
    private final Map<String, TweetStream> streamMap = new ConcurrentHashMap<>();

    public static StreamRegistry getInstance(){
        return streamRegistry;
    }

    private StreamRegistry(){
    }

    /**
     * This method registers a {@link ch.uzh.ase.Util.Term} and starts a new {@link TweetStream} to retrieve {@link ch.uzh.ase.Util.Tweet}s
     * @param streamId
     */
    public void register(String streamId){
        TweetStream tweetStream = new TweetStream();
        tweetStream.startStream(streamId);
        streamMap.put(streamId, tweetStream);
        }

    public void unRegister(String streamId){
        streamMap.remove(streamId);
    }

    public TweetStream locateStream(String streamId){
        return streamMap.get(streamId);
    }

    public void setBlackBoard(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    public Blackboard getBlackBoard() {
        return this.blackboard;
    }
}
