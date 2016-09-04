package controller;

import dataObjects.Document;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Thagus on 03/09/16.
 */
public class ControllerImportDocument implements EventHandler<ActionEvent> {
    private final Stage window;

    public ControllerImportDocument(Stage window) {
        this.window = window;
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
                        case 2:     //Case for .N -

                            break;
                        case 3:     //Case for .A - Authors
                            documents.get(documents.size()-1).appendAuthor(line);
                            break;
                        case 4:     //Case for .W -
                            documents.get(documents.size()-1).appendText(line);
                            break;
                        case 5:     //Case for .K -

                            break;
                        case 6:     //Case for .C -

                            //Random? decimal numbers
                            break;
                        case 7:     //Case for .X - Citations

                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "The file doesn't follow the expected structure!");
                    }
                }
            });
            feedDatabase(documents);
        } catch (IOException e) {
            System.out.println("Error reading file:");
            e.printStackTrace();
        }
    }

    private void feedDatabase(ArrayList<Document> documents){
        HashMap<String, Integer> wordOccurrence = new HashMap<>();

        for(Document doc : documents){
            doc.countWords(wordOccurrence);
        }

        for(Map.Entry<String, Integer> entry : wordOccurrence.entrySet()){
            //if(entry.getKey().contains("5"))
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
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
