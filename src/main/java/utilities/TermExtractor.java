package utilities;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import utilities.SpellChecker.Dictionary;
import utilities.SpellChecker.SpellChecker;
import utilities.Stemmer.PorterStemmer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thagus on 05/09/16.
 */
public class TermExtractor {
    private static Pattern reg = Pattern.compile("[a-z]+");

    private static HashSet<String> stopWords;

    public static void initialize(){
        //Obtain database instance
        //UtilitiesDatabase db = UtilitiesDatabase.instance();

        //Load stop words
        try {
            stopWords = new HashSet<>();
            readStopWords();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error loading stopwords.txt", JOptionPane.ERROR_MESSAGE);
        }

        //Initialize dictionary entries from database
        try {
            System.out.println("Creating dictionary....");
            Dictionary.createDictionary("/corpus.txt");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error loading dictionary from corpus", JOptionPane.ERROR_MESSAGE);
        }

    }

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

    public static HashMap<String, Integer> extractTerms(String text){
        HashMap<String, Integer> termResults = new HashMap<>();  //Will contain as key every term in the text, and value the frequency of the term
        ArrayList<String> words = new ArrayList<>();

        //Get only character strings from the text in lowercase
        Matcher matcher = reg.matcher(text.toLowerCase());

        while (matcher.find()){
            words.add(matcher.group());
        }

        //Correct spelling of the words
        spellCheck(words);
        //Stem and remove stop words from the array
        words = stemmingAndStopWordsRemoval(words);

        //Count words and add them to a HashMap
        for(String word : words){
            //System.out.println(word);
            Integer count = termResults.get(word);

            if(count==null){
                count = 0;
            }
            termResults.put(word, count+1);
        }

        return termResults;
    }

    private static void spellCheck(ArrayList<String> words){
        String previousString = "";
        //Check single words, if no results come for that word, check with its neighbors
        for(int i=0; i<words.size(); i++){
            String corrected = SpellChecker.correct(words.get(i));

            //The word doesn't exist, then it might be a word splitted by space, try combining with the next word
            if(corrected==null){
                corrected = SpellChecker.correct(words.get(i) + words.get(i+1));

                //We could correct the combination
                if(corrected!=null){
                    previousString = words.get(i) + words.get(i+1); //The previous word is now the combined words
                    words.set(i, corrected);   //Add it to the array
                    words.remove(i+1);                  //Remove the next entry, because we combined with it
                    //The index remains the same, as we will pass through the next word whichever it is
                }
                else {
                    //Try combining with previous word
                    corrected = SpellChecker.correct(previousString + words.get(i));

                    //If we got a positive correction from this combination, place this word and remove the old one (and update i to skip repeated value)
                    if(corrected!=null){
                        previousString = previousString + words.get(i);//The previous word is the combination of the corrected words
                        words.set(i, corrected);   //Add the corrected-combined word to the array
                        words.remove(i-1);  //Remove the string we combined with
                        i--;    //Update index to not skip the net word of the array
                    }
                    //Else, the word couldn't be corrected, leave it as it is
                }
            }
            else{   //The word was successfully corrected
                previousString = words.get(i);  //Save the current string for further use if necessary
                words.set(i, corrected);
            }
        }

        //Consider all words that combined with its following neighbor produce a valid word
        ArrayList<String> combinedCorrections = new ArrayList<>();
        for(int i=0; i<words.size()-1; i++){
            String corrected = SpellChecker.correct(words.get(i) + words.get(i+1));

            if(corrected!=null){
                combinedCorrections.add(corrected);
            }
        }

        words.addAll(combinedCorrections);
    }

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

    public static void main(String[] args){
        String text = "comp uter program running on Windows 8.1";

        initialize();

        extractTerms(text);
    }
}
