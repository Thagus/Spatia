import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thagus on 03/09/16.
 */
public class Test {
    public static void main(String[] args){
        String text = "The (2s+1)-point second-degree 1+log2 goal of this program is to make a step toward te design of an automated\n" +
                "mathematical assistant. Some requirements for such a program are: it must be\n" +
                "easy to access, and that the result must be obtained in a reasonably short\n" +
                "time. Accordingly the program is written for a time-shared computer. The Q-32\n" +
                "computer as System Development Corporation, Santa Monica, California, was \n" +
                "chosen because, it also had a LISP 1.5 compiler. Programming and debugging was\n" +
                "done from a remote teletype console at Stanford University.";

        String lowercase = text.toLowerCase();
        lowercase = lowercase.replaceAll("(\\d)","");
        String[] spplited = lowercase.split("(\\s+)|\\p{Punct}");

        HashMap<String, Integer> occurrenceMap = new HashMap<>();

        for(String word : spplited){
            System.out.println(word);
            Integer count = occurrenceMap.get(word);
            if(count==null){
                count = 0;
            }
            occurrenceMap.put(word, count+1);
        }

        for(Map.Entry<String, Integer> entry : occurrenceMap.entrySet()){
            if(entry.getKey().length()==1)
                System.out.println(entry.getKey() + " - " + entry.getValue());
        }
        //System.out.println(occurrenceMap);
    }
}
