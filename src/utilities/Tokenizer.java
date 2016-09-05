package utilities;

/**
 * Created by Thagus on 05/09/16.
 */
public class Tokenizer {

    public static String[] tokenizeString(String text){
        String lowercase = text.toLowerCase();

        //Remove numbers
        lowercase = lowercase.replaceAll("\\d","");
        //Split at spaces or punctuation
        return lowercase.split("(\\s+)|\\p{Punct}");
    }
}
