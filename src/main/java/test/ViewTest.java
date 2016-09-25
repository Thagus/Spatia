package test;

import controller.ControllerSceneSwitcher;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
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
    private ControllerSceneSwitcher controllerSceneSwitcher;

    private TreeView<String> treeView;
    private BorderPane rightPane;

    /**
     * Start the application
     * @param window The window where the application will run
     */
    public Scene createScene(Stage window, ControllerSceneSwitcher controllerSceneSwitcher){
        //Create the main layout
        VBox layout = new VBox();
        layout.setSpacing(5);

        //Create the controller
        this.controllerSceneSwitcher = controllerSceneSwitcher;
        controllerTests = new ControllerTests(window, this);

        createMenus(layout);
        createSplitPane(layout);
        return new Scene(layout, 1200, 720);
    }

    /**
     * Create the menus of the application
     * @param layout The main layout where to add the menus
     */
    private void createMenus(VBox layout){
        MenuBar menuBar = new MenuBar();

        //File menu
        Menu menuFile = new Menu("_Tests");

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


        //Open search menu
        Menu menuSearch = new Menu("_Search");
        MenuItem openSearch = new MenuItem("Open search");
        openSearch.setUserData("openSearch");
        openSearch.setOnAction(controllerSceneSwitcher);
        menuSearch.getItems().add(openSearch);


        menuBar.getMenus().addAll(menuFile, menuSearch);
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
     * @param lineChart A chart containing the averages recalls and precisions
     */
    public void setViewAverage(float averagePrecision, float averageRecall, LineChart<Number, Number> lineChart){
        VBox box = new VBox();
        box.setSpacing(10);

        Label mainLabel = new Label();
        mainLabel.setText("Averages");

        Label recallLabel = new Label("Average recall: " + averageRecall + "%");
        Label precisionLabel = new Label("Average precision: " + averagePrecision + "%");

        Label chartLabel = new Label("Chart: ");

        box.getChildren().addAll(mainLabel, recallLabel, precisionLabel, chartLabel, lineChart);


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
