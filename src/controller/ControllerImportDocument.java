package controller;

import dataObjects.Document;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ModelDatabase;

import javax.swing.*;
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
    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);

        if(file!=null) {
            readFile(file);
        }
    }

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
                        case 2:     //Case for .N - library info?

                            break;
                        case 3:     //Case for .A - Authors
                            documents.get(documents.size()-1).appendAuthor(line);
                            break;
                        case 4:     //Case for .W - abstract
                            documents.get(documents.size()-1).appendAbstract(line);
                            break;
                        case 5:     //Case for .K - key words

                            break;
                        case 6:     //Case for .C - Classification
                            //decimal numbers
                            break;
                        case 7:     //Case for .X - Citations

                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "The file doesn't follow the expected structure!");
                    }
                }
            });
            Alert countInfo = new Alert(Alert.AlertType.INFORMATION, "Read " + documents.size() + " documents");
            countInfo.setTitle("Successful reading!");
            countInfo.setHeaderText(null);
            countInfo.showAndWait();

            feedDatabase(documents);
        } catch (IOException e) {
            System.out.println("Error reading file:");
            e.printStackTrace();
        }
    }

    private void feedDatabase(ArrayList<Document> documents){
        HashMap<String, Integer> documentWordOccurrence = new HashMap<>();
        HashMap<String, Integer> wordCountLocal;

        for(Document doc : documents){
            boolean insertCheck = db.opDocuments.addDocument(doc.getIdDoc(), doc.getTitle(), doc.getJournal(), doc.getLibraryInfo(), doc.getAuthors(), doc.getAbstractText(), doc.getKeywords(), doc.getClassification(), doc.getCitations());

            if(insertCheck) {
                wordCountLocal = doc.countWords(documentWordOccurrence);
                for(Map.Entry<String, Integer> termEntry : wordCountLocal.entrySet()){
                    db.opTerm.addTerm(doc.getIdDoc(), termEntry.getKey(), termEntry.getValue());
                }
            }

        }


        /*for(Map.Entry<String, Integer> entry : documentWordOccurrence.entrySet()){
            //if(entry.getKey().length()<=3)
                System.out.println(entry.getKey() + " - " + entry.getValue());
        }*/
    }


    private void setAllFalse(boolean[] array){
        for(int i=0; i<array.length; i++){
            array[i] = false;
        }
    }

    private int getTrueValueIndex(boolean[] array){
        for(int i=0; i<array.length; i++){
            if(array[i]){
                return i;
            }
        }
        return -1;
    }
}
