package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Blackboard.*;
import ch.uzh.ase.Monitoring.WorkloadObserver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Silvio Fankhauser on 29.04.2017.
 */
public class StreamRegistry {

    private static final StreamRegistry streamRegistry = new StreamRegistry();

    public static StreamRegistry getInstance(){
        return streamRegistry;
    }

    private StreamRegistry(){
    }

    Blackboard blackboard = null;
    private Map<String, TweetStream> streamMap = new ConcurrentHashMap<>();

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
