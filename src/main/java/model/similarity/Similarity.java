package model.similarity;

import dataObjects.Document;
import javafx.collections.ObservableList;

import java.util.HashMap;

/**
 * Created by Thagus on 24/09/16.
 */
public abstract class Similarity {
    String similarityMethodName;

    public String getSimilarityMethodName() {
        return similarityMethodName;
    }

    public abstract ObservableList<Document> calculateSimilarity(HashMap<String, Integer> wordCount);
}
