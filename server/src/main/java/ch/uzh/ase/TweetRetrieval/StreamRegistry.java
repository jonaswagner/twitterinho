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



    private List<Blackboard> blackboardList =  Collections.synchronizedList(new ArrayList<>());
    private Map<String, TweetStream> streamMap = new ConcurrentHashMap<>();
    private Map<Blackboard, List<String>> blackboardMap = new ConcurrentHashMap<>();

    public void register(String streamId, TweetStream tweetStream){
        streamMap.put(streamId, tweetStream);

        blackboardMap.get(setBlackboard()).add(streamId);
    }

    public void unRegister(String streamId, TweetStream tweetStream){
        streamMap.remove(streamId);
    }

    public TweetStream locateStream(String streamId){
        return streamMap.get(streamId);
    }

    public Blackboard locateBlackboard(String streamId){
        for(Map.Entry<Blackboard, List<String>> element : blackboardMap.entrySet()) {
            if (element.getValue().contains(streamId)) {
                return element.getKey();
            }
        }
        //TODO handle return null
        return null;
    }

    private Blackboard setBlackboard(){
        if(blackboardList.isEmpty()){
            Blackboard blackboard = new Blackboard();
            WorkloadObserver workloadObserver = new WorkloadObserver();
            workloadObserver.start();
            AbstractKSMaster iks = new SentimentEnglishKS(blackboard, workloadObserver);
            iks.start();
            BlackboardControl blackboardControl = new BlackboardControl(blackboard, Arrays.asList(iks));
            blackboardControl.start();
            //TODO: add language stuff
            //TODO: start persistentControler

            blackboardList.add(blackboard);
            return blackboard;
        }
        else {
            //TODO: handle workload of blackboard
            return blackboardList.get(0);
        }
    }

}
