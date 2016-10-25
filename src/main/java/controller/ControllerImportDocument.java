package controller;

import dataObjects.Document;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.ModelDatabase;
import test.QueryObject;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Thagus on 03/09/16.
 */
public class ControllerImportDocument implements EventHandler<ActionEvent> {
    private final ModelDatabase db;
    private boolean messagesEnabled;

    public ControllerImportDocument(boolean messagesEnabled){
        this.db = ModelDatabase.instance();
        this.messagesEnabled = messagesEnabled;
    }

    /**
     * Handle the Import document option from the menu by opening a FileChooser,
     * and passing the selected file to the readFile method
     */
    @Override
    public void handle(ActionEvent event) {
        //Send starting index, for multiple collections
        int startingIndex;
        int queryStartIndex;

        startingIndex = 0;
        readFile(new File(getClass().getClassLoader().getResource("cacm.all").getFile()), startingIndex);
        queryStartIndex = 0;
        readTestsFiles("cacm", queryStartIndex, startingIndex);

        startingIndex = db.opDocuments.countDocuments();
        readFile(new File(getClass().getClassLoader().getResource("med.all").getFile()), startingIndex);
        queryStartIndex = db.opTests.countQueries();
        readTestsFiles("med", queryStartIndex, startingIndex);

        //Delete queries with no relevant documents
        db.opTests.deleteEmptyQueries();

        //A message to alert the user about the number of read documents
        Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Cleared empty queries!, remained " + db.opTests.countQueries() + " queries");
        countInfo.setTitle("Successful cleaning!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();
    }

    /**
     * Reads the tests files (query and query relations) from resources for the specific collection
     * @param collectionName The name of the collection we want the queries from
     */
    public void readTestsFiles(String collectionName, int queryStartIndex, int documentStartIndex){
        //Query documents files from resources
        File queryTextFile = new File(getClass().getClassLoader().getResource(collectionName + ".qry").getFile());
        File qrelsTextFile = new File(getClass().getClassLoader().getResource(collectionName + ".rels").getFile());

        final ArrayList<QueryObject> queriesRead = new ArrayList<>();

        //Read query.text file
        try (Stream<String> stream = Files.lines(queryTextFile.toPath())) {
            final int[] currentType = new int[1];

            stream.forEach(line -> {
                if(line.startsWith(".I")){
                    //The format is "I. " + id
                    int id = Integer.parseInt(line.substring(3)) + queryStartIndex;
                    queriesRead.add(new QueryObject(id));
                } else if(line.startsWith(".W")){
                    currentType[0] = 0;
                } else if(line.startsWith(".")) {   //Ignore all other tags
                    currentType[0] = 1;
                } else {    //When the line is not a header
                    switch (currentType[0]) {
                        case 0:     //Case for .W - query
                            queriesRead.get(queriesRead.size()-1).appendQuery(line.trim());
                            break;
                        case 1:     //Case for ignored tags
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "The file doesn't follow the expected structure!");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write to database the queries
        for(QueryObject queryObject : queriesRead){
            db.opTests.addQuery(queryObject.getQid(), queryObject.getQuery());
        }

        Pattern numberPattern = Pattern.compile("\\s+");
        //Read qrels.text file and write to database its contents
        try (Stream<String> stream = Files.lines(qrelsTextFile.toPath())) {
            stream.forEach(line -> {
                String[] numbers = numberPattern.split(line);
                db.opTests.addRelevant(Integer.parseInt(numbers[0]) + queryStartIndex, Integer.parseInt(numbers[1]) + documentStartIndex);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //A message to alert the user about the number of read documents
        Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + queriesRead.size() + " queries");
        countInfo.setTitle("Successful reading!");
        countInfo.setHeaderText(null);
        countInfo.showAndWait();
    }

    /**
     * Reads the input file following the general collections structure
     * and creates Document objects to store the data related to each document from teh collection
     * Finally calls the feedDatabase method with the constructed array of Documents
     *
     * @param file The file that will be read
     */
    public void readFile(File file, int startingIndex){
        //Read file and feed database
        try (Stream<String> stream = Files.lines(file.toPath())) {
            final boolean[] currentType = new boolean[8];     //0-T, 1-B, 2-N, 3-A, 4-W, 5-K, 6-C, 7-X
            final ArrayList<Document> documents = new ArrayList<>();

            stream.forEach(line -> {
                if(line.startsWith(".I")){
                    //The format is "I. " + id
                    int id = Integer.parseInt(line.substring(3)) + startingIndex;
                    documents.add(new Document(id));
                } else if(line.startsWith(".T")){
                    setAllFalse(currentType);
                    currentType[1] = true;
                } else if(line.startsWith(".B")){
                    documents.get(documents.size()-1).appendText("\n");
                    setAllFalse(currentType);
                    currentType[2] = true;
                } else if(line.startsWith(".A")){
                    documents.get(documents.size()-1).appendText("\n");
                    setAllFalse(currentType);
                    currentType[3] = true;
                } else if(line.startsWith(".W")){
                    documents.get(documents.size()-1).appendText("\n");
                    setAllFalse(currentType);
                    currentType[4] = true;
                } else if(line.startsWith(".K")){
                    documents.get(documents.size()-1).appendText("\n");
                    setAllFalse(currentType);
                    currentType[5] = true;
                } else if(line.startsWith(".")){
                    setAllFalse(currentType);
                    currentType[0] = true;
                } else {    //When the line is not a header
                    int type = getTrueValueIndex(currentType);

                    switch (type) {
                        case 0: //Ignore the rest
                            break;
                        case 1:     //Case for .T - Title
                        case 2:     //Case for .B - Journal name and edition
                        case 3:     //Case for .A - Authors
                        case 4:     //Case for .W - abstract
                        case 5:     //Case for .K - key words
                            documents.get(documents.size()-1).appendText(line);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "The file doesn't follow the expected structure!");
                    }
                }
            });

            if(messagesEnabled) {
                //A message to alert the user about the number of read documents
                Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + documents.size() + " documents");
                countInfo.setTitle("Successful reading!");
                countInfo.setHeaderText(null);
                countInfo.showAndWait();
            }

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
     * Requests the calculation of weights
     *
     * @param documents The array of documents read
     */
    private void feedDatabase(ArrayList<Document> documents){
        HashMap<String, Integer> wordCountLocal;

        int documentCount = 0;

        //Add documents and terms
        for(Document doc : documents){
            boolean insertCheck = db.opDocuments.addDocument(doc.getIdDoc(), doc.getText());

            //The document was correctly added if there is no duplicate key
            if(insertCheck) {
                documentCount++;    //Increase successful document insert count
                wordCountLocal = doc.countWords();    //Request the count of words for the inserted document
                for(Map.Entry<String, Integer> termEntry : wordCountLocal.entrySet()){      //For each term obtained from the document (Key String is the term, Value Integer is the TF)
                    //Write the term, with the document id and the TF
                    db.opInvertedIndex.addTerm(doc.getIdDoc(), termEntry.getKey(), termEntry.getValue());
                }
            }
        }

        //Requests the calculation of IDFs and weights
        db.opModel.recalculateWeights();

        if(messagesEnabled) {
            //Message to alert the user of the total amount of successfully added documents
            Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Successfully added " + documentCount + " documents");
            countInfo.setTitle("Successful index!");
            countInfo.setHeaderText(null);
            countInfo.showAndWait();
        }
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
