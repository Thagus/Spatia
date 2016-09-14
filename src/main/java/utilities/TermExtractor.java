package utilities;

import utilities.Stemmer.PorterStemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thagus on 05/09/16.
 */
public class TermExtractor {
    private static Pattern reg = Pattern.compile("[a-z]+");

    public static HashMap<String, Integer> extractTerms(String text){
        HashMap<String, Integer> wordResult = new HashMap<>();  //Will contain as key every term in the text, and value the frequency of the term

        //Get only character strings from the text in lowercase
        Matcher matcher = reg.matcher(text.toLowerCase());

        while (matcher.find()){
            String found = matcher.group();

            Integer count = wordResult.get(found);

            if(count==null){
                count = 0;
            }
            wordResult.put(found, count+1);
        }

        return wordResult;
    }
}
