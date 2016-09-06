package controller;

import dataObjects.Document;
import dataObjects.DocumentTerm;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.ModelDatabase;
import utilities.Tokenizer;
import view.DocumentView;

import javax.swing.JOptionPane;

/**
 * Created by Thagus on 05/09/16.
 */
public class ControllerTermSearch{
    /**
     * Method to handle the search request from the View
     *
     * @param tableTerms The TableView that contains the search result
     * @param tfidfLabel The label that displays the TFIDF value of the term
     * @param tfCol The TF column, that we will sort upon
     * @param searchBox The search box where the term to be searched is written
     */
    public void handleSearch(TableView<DocumentTerm> tableTerms, Label tfidfLabel, TableColumn<DocumentTerm, Integer> tfCol, TextField searchBox){
        //Tokenize string, and get first word
        String[] terms = Tokenizer.tokenizeString(searchBox.getText());
        String term = "";

        for(String t : terms){
            if(t.length()>=3){  //Filter terms that have less than 2 characters
                term = t;
                break;
            }
        }

        if(term.length()>2){
            tableTerms.setItems( ModelDatabase.instance().opModel.termSearch(term));
            tableTerms.getSortOrder().setAll(tfCol);
            tableTerms.sort();
            tfidfLabel.setText("IDF: " + ModelDatabase.instance().opIDF.getTermIDF(term));
        }
        else {
            JOptionPane.showMessageDialog(null, "The given term is too short!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method to handle clicks over the TableView of the searchResult
     * @param event The MouseEvent fired when the row was clicked
     */
    public void handleTableClick(MouseEvent event){
        //Single click on row with primary button
        if(event.getClickCount() == 1 && event.getButton()== MouseButton.PRIMARY){
            Node node = ((Node) event.getTarget()).getParent();
            TableRow row;
            if (node instanceof TableRow) {
                row = (TableRow) node;

                //Extract DocumentTerm
                DocumentTerm documentTerm = (DocumentTerm) row.getItem();
                //Get Document
                Document document = ModelDatabase.instance().opDocuments.getDocument(documentTerm.getIdDoc());
                //Create document view
                new DocumentView(document);
            } else if (node.getParent() instanceof TableRow){
                //clicking on text part
                row = (TableRow) node.getParent();

                //Extract DocumentTerm
                DocumentTerm documentTerm = (DocumentTerm) row.getItem();
                //Get Document
                Document document = ModelDatabase.instance().opDocuments.getDocument(documentTerm.getIdDoc());
                //Create document view
                new DocumentView(document);
            }
        }
    }
}
