package test;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by Thagus on 11/09/16.
 */
public class ViewTest {
    private ControllerTests controllerTests;

    private TreeView<String> treeView;
    private BorderPane rightPane;

    /**
     * Start the application
     * @param window The window where the application will run
     */
    public void start(Stage window){
        window.setTitle("Spatia tests");

        //Create the main layout
        VBox layout = new VBox();
        layout.setSpacing(5);

        //Create the controller
        controllerTests = new ControllerTests(window, this);

        //Create the scene based on the VBox layout
        Scene scene = new Scene(layout, 1200, 720);

        createMenus(layout);
        createSplitPane(layout);

        window.setScene(scene);
        window.show();
    }

    /**
     * Create the menus of the application
     * @param layout The main layout where to add the menus
     */
    private void createMenus(VBox layout){
        MenuBar menuBar = new MenuBar();

        //File menu
        Menu menuFile = new Menu("_File");

        //Import document
        MenuItem importTests = new MenuItem("Select tests folder...");
        importTests.setOnAction(controllerTests);
        importTests.setUserData("import");

        //Separator
        SeparatorMenuItem separator = new SeparatorMenuItem();

        //Begin tests
        MenuItem beginTests = new MenuItem("Begin tests");
        beginTests.setOnAction(controllerTests);
        beginTests.setUserData("begin");

        //Add menu items to the menu
        menuFile.getItems().addAll(importTests, separator, beginTests);


        menuBar.getMenus().addAll(menuFile);
        layout.getChildren().add(menuBar);
    }

    /**
     * Create a split pane and its components
     * @param layout The layout where to add the split pane
     */
    private void createSplitPane(VBox layout){
        SplitPane splitPane = new SplitPane();
        //Make the SplitPane the size of the window
        splitPane.prefWidthProperty().bind(layout.widthProperty());
        splitPane.prefHeightProperty().bind(layout.heightProperty());

        //Create TreeView list
        treeView = new TreeView<>();
        treeView.getSelectionModel().selectedItemProperty().addListener(controllerTests);

        //Create layout for charts
        rightPane = new BorderPane();

        splitPane.getItems().addAll(treeView, rightPane);
        splitPane.setDividerPositions(0.15, 1);
        layout.getChildren().add(splitPane);
    }

    /**
     * Display the query chart and data on the rightPane
     * @param query The query that will be displayed
     */
    public void setViewedChart(QueryObject query){
        VBox box = new VBox();
        box.setSpacing(10);

        Label queryLabel = new Label();
        queryLabel.setText("Query: ");

        Text queryText = new Text();
        queryText.setText(query.getQuery());
        queryText.setWrappingWidth(400);

        Label totalRelevant = new Label("Relevant documents for query: " + query.getRelevantDocuments().size());
        Label retrievedDocuments = new Label("Retrieved documents: " + query.getDocumentRetrieved().size());
        Label recallLabel = new Label("Final recall: " + query.getRecall() + "%");
        Label precisionLabel = new Label("Total precision: " + query.getPrecision() + "%");

        Label chartLabel = new Label("Chart: ");

        box.getChildren().addAll(queryLabel, queryText, totalRelevant, retrievedDocuments, recallLabel, precisionLabel, chartLabel, query.getLineChart());


        rightPane.setCenter(box);
    }

    /**
     * Display the global averages on the rightPane
     * @param averagePrecision The average precision
     * @param averageRecall The average recall
     */
    public void setViewAverage(float averagePrecision, float averageRecall){
        VBox box = new VBox();
        box.setSpacing(10);

        Label mainLabel = new Label();
        mainLabel.setText("Averages");

        Label recallLabel = new Label("Average recall: " + averageRecall + "%");
        Label precisionLabel = new Label("Average precision: " + averagePrecision + "%");

        box.getChildren().addAll(mainLabel, recallLabel, precisionLabel);


        rightPane.setCenter(box);
    }

    /**
     * Set the root item of the left side TreeView
     * @param root The root item
     */
    public void setRootTreeView(TreeItem<String> root){
        treeView.setRoot(root);
    }

}
