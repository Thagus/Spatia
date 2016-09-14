package utilities.SpellChecker;

import java.util.*;

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


public class SpellChecker {
    private static int editDistanceMax = 2;
    private static boolean singleSuggestion = true;

    public static String correct(String input){
        List<SuggestItem> suggestions;

        //Check in dictionary for existence
        suggestions = lookup(input, editDistanceMax);

        //Display term and frequency
        /*for(SuggestItem suggestion : suggestions){
            System.out.println( suggestion.term + " " + suggestion.distance + " " + suggestion.count);
        }*/

        if(suggestions.size()>0){
            return suggestions.get(0).term;
        }
        else{
            return null;
        }
    }

    private static List<SuggestItem> lookup(String input, int editDistanceMax){
        //if the input length minus the editDistance is bigger than the biggest word, save some time
        if (input.length() - editDistanceMax > Dictionary.maxlength)
            return new ArrayList<>();

        List<String> candidates = new ArrayList<>();
        HashSet<String> hashset1 = new HashSet<>();

        List<SuggestItem> suggestions = new ArrayList<>();
        HashSet<String> hashset2 = new HashSet<>();

        Object valueo;

        //Consider original input as candidate
        candidates.add(input);

        while (candidates.size()>0){
            String candidate = candidates.get(0);
            candidates.remove(0);

            //save some time
            //early termination
            //suggestion distance=candidate.distance... candidate.distance+editDistanceMax
            //if canddate distance is already higher than suggestion distance, than there are no better suggestions to be expected

            //label for c# goto replacement
            nosort:{
                if ((suggestions.size() > 0) && (input.length()-candidate.length() > suggestions.get(0).distance)) {
                    break nosort;
                }

                //read candidate entry from dictionary
                valueo = Dictionary.dictionary.get(candidate);
                if (valueo != null) {
                    DictionaryItem value= new DictionaryItem();
                    if (valueo instanceof Integer)
                        value.suggestions.add((int)valueo);
                    else
                        value = (DictionaryItem)valueo;

                    //if count>0 then candidate entry is correct dictionary term, not only delete item
                    if ((value.count > 0) && hashset2.add(candidate)) {
                        //add correct dictionary term term to suggestion list
                        SuggestItem si = new SuggestItem();
                        si.term = candidate;
                        si.count = value.count;
                        si.distance = input.length() - candidate.length();
                        suggestions.add(si);
                        //early termination
                        if ((input.length() - candidate.length() == 0))
                            break nosort;
                    }

                    //iterate through suggestions (to other correct dictionary items) of delete item and add them to suggestion list
                    Object value2;
                    for (int suggestionint : value.suggestions) {
                        //save some time
                        //skipping double items early: different deletes of the input term can lead to the same suggestion
                        //index2word
                        String suggestion = Dictionary.wordlist.get(suggestionint);
                        if (hashset2.add(suggestion)) {
                            //True Damerau-Levenshtein Edit Distance: adjust distance, if both distances>0
                            //We allow simultaneous edits (deletes) of editDistanceMax on on both the dictionary and the input term.
                            //For replaces and adjacent transposes the resulting edit distance stays <= editDistanceMax.
                            //For inserts and deletes the resulting edit distance might exceed editDistanceMax.
                            //To prevent suggestions of a higher edit distance, we need to calculate the resulting edit distance, if there are simultaneous edits on both sides.
                            //Example: (bank==bnak and bank==bink, but bank!=kanb and bank!=xban and bank!=baxn for editDistanceMaxe=1)
                            //Two deletes on each side of a pair makes them all equal, but the first two pairs have edit distance=1, the others edit distance=2.
                            int distance = 0;
                            if (suggestion != input) {
                                if (suggestion.length() == candidate.length()) distance = input.length() - candidate.length();
                                else if (input.length() == candidate.length()) distance = suggestion.length() - candidate.length();
                                else {
                                    //common prefixes and suffixes are ignored, because this speeds up the Damerau-levenshtein-Distance calculation without changing it.
                                    int ii = 0;
                                    int jj = 0;
                                    while ((ii < suggestion.length()) && (ii < input.length()) && (suggestion.charAt(ii) == input.charAt(ii)))
                                        ii++;
                                    while ((jj < suggestion.length() - ii) && (jj < input.length() - ii) && (suggestion.charAt(suggestion.length() - jj - 1) == input.charAt(input.length() - jj - 1)))
                                        jj++;
                                    if ((ii > 0) || (jj > 0)) {
                                        distance = damerauLevenshteinDistance(suggestion.substring(ii, suggestion.length() - jj), input.substring(ii, input.length() - jj));
                                    }
                                    else distance = damerauLevenshteinDistance(suggestion, input);
                                }
                            }

                            //save some time.
                            //remove all existing suggestions of higher distance
                            if ((suggestions.size() > 0) && (suggestions.get(0).distance > distance))
                                suggestions.clear();
                            //do not process higher distances than those already found
                            if ((suggestions.size() > 0) && (distance > suggestions.get(0).distance))
                                continue;

                            if (distance <= editDistanceMax) {
                                value2 = Dictionary.dictionary.get(suggestion);
                                if (value2!=null) {
                                    SuggestItem si = new SuggestItem();
                                    si.term = suggestion;
                                    si.count = ((DictionaryItem)value2).count;
                                    si.distance = distance;
                                    suggestions.add(si);
                                }
                            }
                        }
                    }//end foreach
                }//end if

                //add edits
                //derive edits (deletes) from candidate (input) and add them to candidates list
                //this is a recursive process until the maximum edit distance has been reached
                if (input.length() - candidate.length() < editDistanceMax) {
                    //save some time
                    //do not create edits with edit distance smaller than suggestions already found
                    if ((suggestions.size() > 0) && (input.length() - candidate.length() >= suggestions.get(0).distance))
                        continue;

                    for (int i = 0; i < candidate.length(); i++) {
                        String delete = candidate.substring(0, i)+candidate.substring(i+1);
                        if (hashset1.add(delete))
                            candidates.add(delete);
                    }
                }
            } //end lable nosort
        } //end while

        //sort by descending word frequency, as all items have the same edit distance
        Collections.sort(suggestions, new Comparator<SuggestItem>(){
            public int compare(SuggestItem f1, SuggestItem f2){
                return -(f1.count-f2.count);
            }
        });

        if ((singleSuggestion) && (suggestions.size()>1))   //If we must return only one suggestion, return a sublist containing just one item
            return suggestions.subList(0, 1);
        else    //Return all suggestions
            return suggestions;

    }

    // Damerau–Levenshtein distance algorithm and code
    // from http://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance (as retrieved in June 2012)
    public static int damerauLevenshteinDistance(String a, String b) {
        final int inf = a.length() + b.length() + 1;
        int[][] H = new int[a.length() + 2][b.length() + 2];
        for (int i = 0; i <= a.length(); i++) {
            H[i + 1][1] = i;
            H[i + 1][0] = inf;
        }
        for (int j = 0; j <= b.length(); j++) {
            H[1][j + 1] = j;
            H[0][j + 1] = inf;
        }
        HashMap<Character, Integer> DA = new HashMap<>();
        for (int d = 0; d < a.length(); d++)
            if (!DA.containsKey(a.charAt(d)))
                DA.put(a.charAt(d), 0);


        for (int d = 0; d < b.length(); d++)
            if (!DA.containsKey(b.charAt(d)))
                DA.put(b.charAt(d), 0);

        for (int i = 1; i <= a.length(); i++) {
            int DB = 0;
            for (int j = 1; j <= b.length(); j++) {
                final int i1 = DA.get(b.charAt(j - 1));
                final int j1 = DB;
                int d = 1;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    d = 0;
                    DB = j;
                }
                H[i + 1][j + 1] = min(
                        H[i][j] + d,
                        H[i + 1][j] + 1,
                        H[i][j + 1] + 1,
                        H[i1][j1] + ((i - i1 - 1))
                                + 1 + ((j - j1 - 1)));
            }
            DA.put(a.charAt(i - 1), i);
        }
        return H[a.length() + 1][b.length() + 1];
    }

    public static int min(int a, int b, int c, int d) {
        return Math.min(a, Math.min(b, Math.min(c, d)));
    }
}
