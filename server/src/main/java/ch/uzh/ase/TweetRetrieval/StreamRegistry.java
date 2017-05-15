package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Blackboard.*;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;

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
    private final WorkloadObserver workloadObserver = new WorkloadObserver();

    public void register(String streamId){
        TweetStream tweetStream = new TweetStream();
        tweetStream.startStream(streamId);
        streamMap.put(streamId, tweetStream);

        if (blackboardMap.isEmpty()){
            setBlackboard();
            blackboardMap.put(blackboardList.get(0), Arrays.asList(streamId)); //FIXME pls consider concurrency
        } else {
            List<String> streams = blackboardMap.get(locateBlackboard(streamId));
            if (!streams.contains(streamId)) {
                streams.add(streamId);
            }
        }

        //TODO jwa add Stream to WorkloadObserver
    }

    public void unRegister(String streamId){
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
            workloadObserver.start();
            AbstractKSMaster iks1 = new SentimentEnglishKS(blackboard, workloadObserver);
            iks1.start();
            AbstractKSMaster iks2 = new LanguageKS(blackboard, workloadObserver);
            iks2.start();
            AbstractKSMaster iks3 = new SentimentGermanKS(blackboard, workloadObserver);
            iks3.start();
            BlackboardControl blackboardControl = new BlackboardControl(blackboard, Arrays.asList(iks1, iks2, iks3));
            blackboardControl.start();
            BlackboardPersist blackboardPersist = new BlackboardPersist(blackboard);
            blackboardPersist.start();

            blackboardList.add(blackboard);
            return blackboard;
        }
        else {
            //TODO: handle workload of blackboard
            return blackboardList.get(0);
        }
    }

    public WorkloadObserver getWorkloadObserver(){
        return workloadObserver;
    }

}
