package utilities;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import utilities.stemmer.EnglishPorterStemmer;
import utilities.stemmer.SpanishPorterStemmer;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thagus on 05/09/16.
 */
public class TermExtractor {
    private static Pattern reg = Pattern.compile("[a-z]+");

    private static boolean stopWordRemoval=true, useStemming=true;

    private static HashMap<String, HashSet<String>> stopWords;

    /**
     * Coordinates the initialization of the stopwords set and the dictionary for the spell checker
     */
    public static void initialize(){
        //Load stop words
        try {
            stopWords = new HashMap<>();

            readStopWords("en");
            readStopWords("es");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error loading stopwords.txt", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Read the stopwords file in order to feed the HashSet
     * @throws IOException in case that the file cant be opened or found
     */
    private static void readStopWords(String language) throws IOException {
        LineIterator lineIterator = IOUtils.lineIterator(TermExtractor.class.getResourceAsStream("/stopwords/" + language + ".txt"), null);

        HashSet<String> languageWords = new HashSet<>();
        stopWords.put(language, languageWords);

        try {
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                languageWords.add(line.trim());
            }
            System.out.println(languageWords.size() + " stop words read");
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
    public static HashMap<String, Integer> extractTerms(String text, String language){
        HashMap<String, Integer> termResults = new HashMap<>();  //Will contain as key every term in the text, and value the frequency of the term
        ArrayList<String> words = new ArrayList<>();

        //Get only character strings from the text in lowercase
        Matcher matcher = reg.matcher(text.toLowerCase());

        while (matcher.find()){
            words.add(matcher.group()); //Add the matched strings to the words array
        }

        if(stopWordRemoval){    //If we have to remove stop words, remove them
            removeStopWords(words, language);
        }
        if(useStemming){    //If we should stem the word, stem them
            words = stemming(words, language);
        }

        //Count words and add them to a HashMap
        for(String word : words){
            if(word.length()>0) {   //If the word exists, proceed
                Integer count = termResults.get(word);  //Check if the term is already on the HashMap and obtain its value
                //If it's not on the HashSet, initialize the count
                if (count == null) {
                    count = 0;
                }
                termResults.put(word, count + 1);     //Add +1 for each term occurrence
            }
        }

        //Return the termResults HashMap
        return termResults;
    }

    /**
     * A method to obtain stemmed words
     * @param words The original array containing words to stem
     * @return The new array containing stemmed words
     */
    private static ArrayList<String> stemming(ArrayList<String> words, String language){
        ArrayList<String> stemmedWords = new ArrayList<>();
        for(String word : words){
            String stemmed;
            //Stem word depending on language
            if(language.equals("en")) {
                stemmed = EnglishPorterStemmer.stem(word);
                //Add the stemmed word to the array
                stemmedWords.add(stemmed);
            }
            else if(language.equals("es")){
                stemmed = SpanishPorterStemmer.stem(word);
                //Add the stemmed word to the array
                stemmedWords.add(stemmed);
            }
        }
        return stemmedWords;
    }

    /**
     * A method to remove stop words from an ArrayList
     * @param words the ArrayList that contains the words that will be checked
     */
    private static void removeStopWords(ArrayList<String> words, String language){
        ListIterator<String> iterator = words.listIterator();
        while (iterator.hasNext()){
            //Remove word if contained in the stopwords set
            if(stopWords.get(language).contains(iterator.next())){
                iterator.remove();
            }
        }
    }

    public static void setStopWordRemoval(boolean stopWordRemoval) {
        TermExtractor.stopWordRemoval = stopWordRemoval;
    }
    public static void setUseStemming(boolean useStemming) {
        TermExtractor.useStemming = useStemming;
    }
}
