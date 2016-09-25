package utilities;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thagus on 05/09/16.
 */
public class TermExtractor {
    private static Pattern reg = Pattern.compile("[a-z]+");

    private static HashSet<String> stopWords;

    /**
     * Coordinates the initialization of the stopwords set and the dictionary for the spell checker
     */
    public static void initialize(){
        //Load stop words
        try {
            stopWords = new HashSet<>();
            readStopWords();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error loading stopwords.txt", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Read the stopwords file in order to feed the HashSet
     * @throws IOException
     */
    private static void readStopWords() throws IOException {
        LineIterator lineIterator = IOUtils.lineIterator(TermExtractor.class.getResourceAsStream("/stopwords.txt"), null);

        try {
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                stopWords.add(line.trim());
            }
            System.out.println(stopWords.size() + " stop words read");
        }
        finally {
            lineIterator.close();
        }
    }

    /**
     * Extracts the stemmed and corrected terms from a String
     * @param text The string from where obtain the terms
     * @return A HashMap containing as key the term and value its occurrence
     */
    public static HashMap<String, Integer> extractTerms(String text){
        HashMap<String, Integer> termResults = new HashMap<>();  //Will contain as key every term in the text, and value the frequency of the term
        ArrayList<String> words = new ArrayList<>();

        //Get only character strings from the text in lowercase
        Matcher matcher = reg.matcher(text.toLowerCase());

        while (matcher.find()){
            words.add(matcher.group()); //Add the matched strings to the words array
        }

        //Correct spelling of the words
        //spellCheck(words);
        //Stem and remove stop words from the array
        words = stemmingAndStopWordsRemoval(words);

        //Count words and add them to a HashMap
        for(String word : words){
            Integer count = termResults.get(word);  //Check if the term is already on the HashMap and obtain its value
            //If it's not on the HashSet, initialize the count
            if(count==null){
                count = 0;
            }
            termResults.put(word, count+1);     //Add +1 for each term occurrence
        }

        //Return the termResults HashMap
        return termResults;
    }

    /**
     * A method to obtain stemmed words without stop words
     * @param words The original array containing words to stem
     * @return The new array containing stemmed words
     */
    private static ArrayList<String> stemmingAndStopWordsRemoval(ArrayList<String> words){
        ArrayList<String> stemmedWords = new ArrayList<>();
        for(String word : words){
            //Skip stop words
            if(!stopWords.contains(word)) {
                //Stem word
                String stemmed = PorterStemmer.stem(word);
                //Add the stemmed word to teh array
                stemmedWords.add(stemmed);
            }
        }
        return stemmedWords;
    }
}
