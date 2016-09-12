package test;

import dataObjects.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Thagus on 11/09/16.
 */
public class ControllerTests implements EventHandler<ActionEvent>{
    private final Stage window;
    private final TestsDatabase db;
    private ArrayList<QueryObject> queries;

    public ControllerTests(Stage window){
        db = TestsDatabase.instance();
        this.window = window;
    }

    @Override
    public void handle(ActionEvent event) {
        MenuItem node = (MenuItem) event.getSource();
        String userData = (String) node.getUserData();

        if(userData.equals("import")) {

            DirectoryChooser fileChooser = new DirectoryChooser();
            File file = fileChooser.showDialog(window);

            if (file != null) {
                importTests(file);
            }
        }
        else if(userData.equals("begin")){
            beginTests();
        }
    }

    public void importTests(File folder){
        String folderPath = folder.getAbsolutePath();
        //Query documents paths
        String queryTextPath = folderPath + "\\query.text";
        String qrelsTextPath = folderPath + "\\qrels.text";

        File queryTextFile = new File(queryTextPath);
        File qrelsTextFile = new File(qrelsTextPath);

        final ArrayList<QueryObject> queriesRead = new ArrayList<>();

        //Read query.text file
        try (Stream<String> stream = Files.lines(queryTextFile.toPath())) {
            final int[] currentType = new int[1];

            stream.forEach(line -> {
                if(line.startsWith(".I")){
                    //The format is "I. " + id
                    int id = Integer.parseInt(line.substring(3));
                    queriesRead.add(new QueryObject(id));
                } else if(line.startsWith(".W")){
                    currentType[0] = 0;
                } else if(line.startsWith(".")) {   //Ignore all other tags
                    currentType[0] = 1;
                } else {    //When the line is not a header
                    switch (currentType[0]) {
                        case 0:     //Case for .W - query
                            queriesRead.get(queriesRead.size()-1).appendQuery(line.trim());
                            currentType[0] = -1;
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
            db.addQuery(queryObject.getQid(), queryObject.getQuery());
        }

        Pattern numberPattern = Pattern.compile("\\s+");
        //Read qrels.text file and write to database its contents
        try (Stream<String> stream = Files.lines(qrelsTextFile.toPath())) {
            stream.forEach(line -> {
                String[] numbers = numberPattern.split(line);
                db.addRelevant(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beginTests(){
        //Load queries

        //Execute them

        //Evaluate

        //Create charts
    }

}
