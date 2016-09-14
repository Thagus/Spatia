package utilities.SpellChecker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import utilities.TermExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Copyright (C) 2015 Wolf Garbe
// Version: 3.0
// Author: Wolf Garbe <wolf.garbe@faroo.com>
// Maintainer: Wolf Garbe <wolf.garbe@faroo.com>
// URL: http://blog.faroo.com/2012/06/07/improved-edit-distance-based-spelling-correction/
// Description: http://blog.faroo.com/2012/06/07/improved-edit-distance-based-spelling-correction/
//
// License:
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License,
// version 3.0 (LGPL-3.0) as published by the Free Software Foundation.
// http://www.opensource.org/licenses/LGPL-3.0
//
// Modified for this project by: Thagus Alcarin

public class Dictionary {
    private static int editDistanceMax = 2;
    public static int maxlength = 0;    //biggest dictionary term entry length

    //Dictionary that contains both the original words and the deletes derived from them. A term might be both word and delete from another word at the same time.
    //For space reduction a item might be either of type dictionaryItem or Int.
    //A dictionaryItem is used for word, word/delete, and delete with multiple suggestions. Int is used for deletes with a single suggestion (the majority of entries).
    public static HashMap<String, Object> dictionary = new HashMap<>(); //initialisierung
    //List of unique words. By using the suggestions (Int) as index for this list they are translated into the original String.
    public static List<String> wordlist = new ArrayList<String>();

    //create a non-unique wordlist from sample text
    //language independent (e.g. works with Chinese characters)
    private static Iterable<String> parseWords(String text) {
        // \w Alphanumeric characters (including non-latin characters, umlaut characters and digits) plus "_"
        // \d Digits
        // Provides identical results to Norvigs regex "[a-z]+" for latin characters, while additionally providing compatibility with non-latin characters
        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("[a-z]+").matcher(text.toLowerCase());
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches;
    }

    //for every word there all deletes with an edit distance of 1..editDistanceMax created and added to the dictionary
    //every delete entry has a suggestions list, which points to the original term(s) it was created from
    //The dictionary may be dynamically updated (word frequency and new words) at any time by calling createDictionaryEntry
    private static boolean createDictionaryEntry(String key) {
        boolean result = false;
        DictionaryItem value = null;
        Object valueo;
        valueo = dictionary.get(key);
        if (valueo!=null) {
            //int or dictionaryItem? delete existed before word!
            if (valueo instanceof Integer) {
                int tmp = (int)valueo;
                value = new DictionaryItem();
                value.suggestions.add(tmp);
                dictionary.put(key,value);
            }

            //already exists:
            //1. word appears several times
            //2. word1==deletes(word2)
            else {
                value = (DictionaryItem)valueo;
            }

            //prevent overflow
            if (value.count < Integer.MAX_VALUE)
                value.count++;
        }
        else if (wordlist.size() < Integer.MAX_VALUE) {
            value = new DictionaryItem();
            value.count++;
            dictionary.put(key, value);

            if (key.length() > maxlength)
                maxlength = key.length();
        }

        //edits/suggestions are created only once, no matter how often word occurs
        //edits/suggestions are created only as soon as the word occurs in the corpus,
        //even if the same term existed before in the dictionary as an edit from another word
        //a treshold might be specifid, when a term occurs so frequently in the corpus that it is considered a valid word for spelling correction
        if(value.count == 1) {
            //word2index
            wordlist.add(key);
            int keyint = (int)(wordlist.size() - 1);

            result = true;

            //create deletes
            for (String delete : edits(key, 0, new HashSet<String>())) {
                Object value2;
                value2 = dictionary.get(delete);
                if (value2!=null) {
                    //already exists:
                    //1. word1==deletes(word2)
                    //2. deletes(word1)==deletes(word2)
                    //int or dictionaryItem? single delete existed before!
                    if (value2 instanceof Integer) {
                        //transformes int to dictionaryItem
                        int tmp = (int)value2;
                        DictionaryItem di = new DictionaryItem();
                        di.suggestions.add(tmp);
                        dictionary.put(delete,di);
                        if (!di.suggestions.contains(keyint))
                            addLowestDistance(di, key, keyint, delete);
                    }
                    else if (!((DictionaryItem)value2).suggestions.contains(keyint))
                        addLowestDistance((DictionaryItem) value2, key, keyint, delete);
                }
                else {
                    dictionary.put(delete, keyint);
                }
            }
        }
        return result;
    }

    /**
     * create a frequency dictionary from a corpus
     * @param corpus The corpus text file path
     * @throws IOException Thrown when the file doesn't exist
     */
    public static void createDictionary(String corpus) throws IOException {
        LineIterator lineIterator = IOUtils.lineIterator(TermExtractor.class.getResourceAsStream(corpus), null);
        int wordCount = 0;

        try {
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                for (String word : parseWords(line)) {
                    if (createDictionaryEntry(word)) {
                        wordCount++;
                    }
                }
            }
        }
        finally {
            lineIterator.close();
        }

        System.out.println("Dictionary: " + wordCount + " words, " + dictionary.size() + " entries");
    }

    //save some time and space
    private static void addLowestDistance(DictionaryItem item, String suggestion, int suggestionint, String delete) {
        //remove all existing suggestions of higher distance
        if ((item.suggestions.size() > 0) && (wordlist.get(item.suggestions.get(0)).length()-delete.length() > suggestion.length() - delete.length()))
            item.suggestions.clear();
        if ((item.suggestions.size() == 0) || (wordlist.get(item.suggestions.get(0)).length()-delete.length() >= suggestion.length() - delete.length()))
            item.suggestions.add(suggestionint);
    }

    //inexpensive and language independent: only deletes, no transposes + replaces + inserts
    //replaces and inserts are expensive and language dependent (Chinese has 70,000 Unicode Han characters)
    private static HashSet<String> edits(String word, int editDistance, HashSet<String> deletes) {
        editDistance++;
        if (word.length() > 1) {
            for (int i = 0; i < word.length(); i++) {
                //delete ith character
                String delete =  word.substring(0,i)+word.substring(i+1);
                if (deletes.add(delete)) {
                    //recursion, if maximum edit distance not yet reached
                    if (editDistance < editDistanceMax)
                        edits(delete, editDistance, deletes);
                }
            }
        }
        return deletes;
    }

    /**
     * Create a frequency dictionary from a corpus
     * @param corpusDirectory The directory path containing the text files from corpus
     * @throws IOException Thrown when the directory doesn't exist
     */
    public static void createDictionaryCorpusDirectory(String corpusDirectory) throws IOException {
        final int[] wordCount = {0, 0};  //To count the amount of words and documents read

        Files.walk(Paths.get(corpusDirectory))
                .filter(f -> f.toString().endsWith(".txt"))
                .forEach(f -> {
                    try {
                        LineIterator lineIterator = FileUtils.lineIterator(f.toFile());

                        try {
                            while (lineIterator.hasNext()) {
                                String line = lineIterator.nextLine();
                                for (String word : parseWords(line)) {
                                    if (createDictionaryEntry(word)) {
                                        wordCount[0]++;
                                    }
                                }
                            }
                        } finally {
                            lineIterator.close();
                        }
                        wordCount[1]++;
                        if(wordCount[1]%500==0){
                            System.out.println(wordCount[1]);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        System.out.println("Dictionary: " + wordCount[0] + " words, " + wordCount[1] + " files read, " + dictionary.size() + " entries");
    }
}
