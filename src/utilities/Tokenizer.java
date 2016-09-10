package utilities;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thagus on 05/09/16.
 */
public class Tokenizer {
    //private static Pattern reg = Pattern.compile("(\\s+)|\\p{Punct}");
    private static Pattern reg = Pattern.compile("[a-z]{3,}");

    /*public static String[] tokenizeString(String text){
        String lowercase = text.toLowerCase();

        //Remove numbers
        lowercase = lowercase.replaceAll("\\d","");
        //Split at spaces or punctuation
        String[] splited = reg.split(lowercase);

        return splited;
    }*/

    public static ArrayList<String> tokenizeString(String text){
        String lowercase = text.toLowerCase();

        //Remove numbers
        lowercase = lowercase.replaceAll("\\d","");
        //Get only strings with 3 or more characters
        Matcher matcher = reg.matcher(lowercase);
        ArrayList<String> result = new ArrayList<>();

        while (matcher.find()){
            result.add(matcher.group());
        }


        return result;
    }
}
