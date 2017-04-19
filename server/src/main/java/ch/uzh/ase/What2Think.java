//package ch.uzh.ase;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.util.ArrayList;
//
//public class What2Think {
//
//    public static void main(String[] args) {
//
//        String topic = "zurich";
//        ArrayList<String> tweets = null;
//
//        File file = new File("tweets.txt");
//        final FileWriter fw;
//        final BufferedWriter bw;
//
//        try {
//
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            fw = new FileWriter(file.getAbsoluteFile());
//            bw = new BufferedWriter(fw);
//            tweets = TweetManager.getTweets(topic);
//
//            NLP.init();
//            for (String tweet : tweets) {
//                bw.write(NLP.findSentiment(tweet) + ": " + tweet);
//                bw.newLine();
//            }
//
//            bw.flush();
//            bw.close();
//            fw.close();
//
//
//            /*for (String tweet : tweets) {
//                System.out.println(tweet + " : " + NLP.findSentiment(tweet));
//            }*/
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//}
