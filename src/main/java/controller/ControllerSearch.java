package controller;

import dataObjects.Document;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.ModelDatabase;
import view.DocumentView;

/**
 * Created by Thagus on 10/09/16.
 */
public class ControllerSearch {
    /**
     * Handle the search from View
     * @param tableDocuments the table to update with the search results
     * @param searchBox The textField containing the search text
     */
    public void handleSearch(TableView<Document> tableDocuments, TextField searchBox){
        tableDocuments.setItems(ModelDatabase.instance().opModel.evaluateQuery(searchBox.getText(), "Complete linkage"));
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

                //Extract Document from table
                Document document = (Document) row.getItem();
                //Create document view
                new DocumentView(document);
            } else if (node.getParent() instanceof TableRow){
                //clicking on text part
                row = (TableRow) node.getParent();

                //Extract Document from table
                Document document = (Document) row.getItem();
                //Create document view
                new DocumentView(document);
            }
        }
    }
}
