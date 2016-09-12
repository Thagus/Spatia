package controller;

import dataObjects.Document;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ModelDatabase;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Thagus on 03/09/16.
 */
public class ControllerImportDocument implements EventHandler<ActionEvent> {
    private final Stage window;
    private final ModelDatabase db;

    public ControllerImportDocument(Stage window) {
        this.window = window;
        this.db = ModelDatabase.instance();
    }

    /**
     * Handle the Import document option from the menu by opening a FileChooser,
     * and passing the selected file to the readFile method
     */
    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);

        if(file!=null) {
            readFile(file);
        }
    }

    /**
     * Reads the input file following the CACM collection document structure
     * and creates Document objects to store the data related to each document from teh collection
     * Finally calls the feedDatabase method with the constructed array of Documents
     *
     * @param file The file that will be read
     */
    private void readFile(File file){
        //Read file and feed database
        try (Stream<String> stream = Files.lines(file.toPath())) {
            final boolean[] currentType = new boolean[8];     //0-T, 1-B, 2-N, 3-A, 4-W, 5-K, 6-C, 7-X
            final ArrayList<Document> documents = new ArrayList<>();

            stream.forEach(line -> {
                if(line.startsWith(".I")){
                    //The format is "I. " + id
                    int id = Integer.parseInt(line.substring(3));
                    documents.add(new Document(id));
                } else if(line.startsWith(".T")){
                    setAllFalse(currentType);
                    currentType[0] = true;
                } else if(line.startsWith(".B")){
                    setAllFalse(currentType);
                    currentType[1] = true;
                } else if(line.startsWith(".N")){
                    setAllFalse(currentType);
                    currentType[2] = true;
                } else if(line.startsWith(".A")){
                    setAllFalse(currentType);
                    currentType[3] = true;
                } else if(line.startsWith(".W")){
                    setAllFalse(currentType);
                    currentType[4] = true;
                } else if(line.startsWith(".K")){
                    setAllFalse(currentType);
                    currentType[5] = true;
                } else if(line.startsWith(".C")){
                    setAllFalse(currentType);
                    currentType[6] = true;
                } else if(line.startsWith(".X")){
                    setAllFalse(currentType);
                    currentType[7] = true;
                } else {    //When the line is not a header
                    int type = getTrueValueIndex(currentType);

                    switch (type) {
                        case 0:     //Case for .T - Title
                            documents.get(documents.size()-1).appendTitle(" " + line);
                            break;
                        case 1:     //Case for .B - Journal name and edition
                            documents.get(documents.size()-1).setJournal(line);
                            break;
                        case 2:     //Case for .N - library notes
                            documents.get(documents.size()-1).setLibraryNotes(line);
                            break;
                        case 3:     //Case for .A - Authors
                            documents.get(documents.size()-1).appendAuthor(line);
                            break;
                        case 4:     //Case for .W - abstract
                            documents.get(documents.size()-1).appendAbstract(line);
                            break;
                        case 5:     //Case for .K - key words
                            documents.get(documents.size()-1).appendKeywords(line);
                            break;
                        case 6:     //Case for .C - Classification
                            documents.get(documents.size()-1).appendClassification(line);
                            break;
                        case 7:     //Case for .X - Citations
                            documents.get(documents.size()-1).appendCitations(line);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "The file doesn't follow the expected structure!");
                    }
                }
            });
            //A message to alert the user about the number of read documents
            Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + documents.size() + " documents");
            countInfo.setTitle("Successful reading!");
            countInfo.setHeaderText(null);
            countInfo.showAndWait();

            //Call to feed database with new documents
            feedDatabase(documents);
        } catch (IOException e) {
            System.out.println("Error reading file:");
            e.printStackTrace();
        }
    }

    /**
     * Counts the words on each document in order to calculate the TF of each term in the document
     * Adds the documents and its terms to the database
     * Requests the calculation of IDF
     * Requests the calculation of TFIDF
     *
     * @param documents The array of documents read
     */
    private void feedDatabase(ArrayList<Document> documents){
        HashMap<String, Integer> documentWordOccurrence = new HashMap<>();
        HashMap<String, Integer> wordCountLocal;

        int documentCount = 0;

        //Add documents and terms
        for(Document doc : documents){
            boolean insertCheck = db.opDocuments.addDocument(doc.getIdDoc(), doc.getTitle(), doc.getJournal(), doc.getLibraryNotes(), doc.getAuthors(), doc.getAbstractText(), doc.getKeywords(), doc.getClassification(), doc.getCitations());

            //The document was correctly added if there is no duplicate key
            if(insertCheck) {
                documentCount++;    //Increase successful document insert count
                wordCountLocal = doc.countWords(documentWordOccurrence);    //Request the count of words for the inserted document
                for(Map.Entry<String, Integer> termEntry : wordCountLocal.entrySet()){      //For each term obtained from the document (Key String is the term, Value Integer is the TF)
                    //Write the term, with the document id and the TF
                    db.opTerm.addTerm(doc.getIdDoc(), termEntry.getKey(), termEntry.getValue());
                }
            }
        }

        int numTotalDocs = db.opDocuments.countDocuments();

        //Calculate IDF
        for(Map.Entry<String, Integer> entry : documentWordOccurrence.entrySet()){      //For each term, request the calculation of the IDF, considering numTotalDocs
            db.opModel.calculateIDF(entry.getKey(), numTotalDocs, entry.getValue());
        }

        //Request the calculation of every term TFIDF
        db.opModel.calculateTFIDFs();

        //Message to alert the user of the total amount of successfully added documents
        Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Successfully added " + documentCount + " documents");
        countInfo.setTitle("Successful index!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();
    }

    /**
     * An utility method to set all elements of a boolean array to false
     *
     * @param array The array that will be set to false
     */
    private void setAllFalse(boolean[] array){
        for(int i=0; i<array.length; i++){
            array[i] = false;
        }
    }

    /**
     * An utility method to get the index of the first true value from a boolean array
     *
     * @param array The input array of booleans
     * @return The index of the first true boolean in the array
     */
    private int getTrueValueIndex(boolean[] array){
        for(int i=0; i<array.length; i++){
            if(array[i]){
                return i;
            }
        }
        return -1;
    }
}
