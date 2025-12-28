package com.JavaWebServer.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerGUI extends Application {

    private ServerController serverController;
    
    // UI Components
    private Button startButton;
    private Button stopButton;
    private Button restartButton;
    private TextField portField;
    private TextField webrootField;
    private Button browseButton;
    private Label statusLabel;
    private Label connectionCountLabel;
    private Label uniqueUsersLabel;
    private Label uptimeLabel;
    private TextArea consoleArea;
    private TableView<RequestLog> requestTable;
    private ObservableList<RequestLog> requestData;
    
    // Statistics
    private Label totalRequestsLabel;
    private Label requestsPerSecondLabel;

    @Override
    public void start(Stage primaryStage) {
        serverController = new ServerController(this);
        
        primaryStage.setTitle("Java HTTP Server Control Panel");
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Top: Control Panel
        VBox controlPanel = createControlPanel();
        mainLayout.setTop(controlPanel);
        
        // Center: TabPane with Request Log, Console, and Statistics
        TabPane tabPane = createTabPane();
        mainLayout.setCenter(tabPane);
        
        // Bottom: Status Bar
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);
        
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            serverController.stopServer();
            Platform.exit();
        });
        primaryStage.show();
    }

    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setStyle("-fx-background-color: #f0f0f0;");
        
        // Title
        Label titleLabel = new Label("Server Configuration");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Port Configuration
        HBox portBox = new HBox(10);
        Label portLabel = new Label("Port:");
        portLabel.setMinWidth(80);
        portField = new TextField("8080");
        portField.setPrefWidth(100);
        portBox.getChildren().addAll(portLabel, portField);
        
        // WebRoot Configuration
        HBox webrootBox = new HBox(10);
        Label webrootLabel = new Label("WebRoot:");
        webrootLabel.setMinWidth(80);
        webrootField = new TextField("WebRoot");
        webrootField.setPrefWidth(300);
        browseButton = new Button("Browse...");
        browseButton.setOnAction(e -> browseWebRoot());
        webrootBox.getChildren().addAll(webrootLabel, webrootField, browseButton);
        
        // Control Buttons
        HBox buttonBox = new HBox(10);
        startButton = new Button("Start Server");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setPrefWidth(120);
        startButton.setOnAction(e -> serverController.startServer(
            Integer.parseInt(portField.getText()), 
            webrootField.getText()
        ));
        
        stopButton = new Button("Stop Server");
        stopButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        stopButton.setPrefWidth(120);
        stopButton.setDisable(true);
        stopButton.setOnAction(e -> serverController.stopServer());
        
        restartButton = new Button("Restart Server");
        restartButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        restartButton.setPrefWidth(120);
        restartButton.setDisable(true);
        restartButton.setOnAction(e -> serverController.restartServer(
            Integer.parseInt(portField.getText()), 
            webrootField.getText()
        ));
        
        buttonBox.getChildren().addAll(startButton, stopButton, restartButton);
        
        controlPanel.getChildren().addAll(titleLabel, portBox, webrootBox, buttonBox);
        return controlPanel;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        
        // Request Log Tab
        Tab requestLogTab = new Tab("Request Log");
        requestLogTab.setClosable(false);
        requestLogTab.setContent(createRequestLogPanel());
        
        // Console Tab
        Tab consoleTab = new Tab("Console Output");
        consoleTab.setClosable(false);
        consoleTab.setContent(createConsolePanel());
        
        // Statistics Tab
        Tab statsTab = new Tab("Statistics");
        statsTab.setClosable(false);
        statsTab.setContent(createStatisticsPanel());
        
        tabPane.getTabs().addAll(requestLogTab, consoleTab, statsTab);
        return tabPane;
    }

    private VBox createRequestLogPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        
        // Table
        requestTable = new TableView<>();
        requestData = FXCollections.observableArrayList();
        requestTable.setItems(requestData);
        
        TableColumn<RequestLog, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeCol.setPrefWidth(150);
        
        TableColumn<RequestLog, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("method"));
        methodCol.setPrefWidth(80);
        
        TableColumn<RequestLog, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathCol.setPrefWidth(300);
        
        TableColumn<RequestLog, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        TableColumn<RequestLog, String> ipCol = new TableColumn<>("Client IP");
        ipCol.setCellValueFactory(new PropertyValueFactory<>("clientIp"));
        ipCol.setPrefWidth(150);
        
        requestTable.getColumns().addAll(timeCol, methodCol, pathCol, statusCol, ipCol);
        
        // Clear button
        Button clearButton = new Button("Clear Logs");
        clearButton.setOnAction(e -> requestData.clear());
        
        panel.getChildren().addAll(requestTable, clearButton);
        VBox.setVgrow(requestTable, Priority.ALWAYS);
        return panel;
    }

    private VBox createConsolePanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        
        consoleArea = new TextArea();
        consoleArea.setEditable(false);
        consoleArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        Button clearConsoleButton = new Button("Clear Console");
        clearConsoleButton.setOnAction(e -> consoleArea.clear());
        
        panel.getChildren().addAll(consoleArea, clearConsoleButton);
        VBox.setVgrow(consoleArea, Priority.ALWAYS);
        return panel;
    }

    private VBox createStatisticsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        
        Label statsTitle = new Label("Server Statistics");
        statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        totalRequestsLabel = new Label("Total Requests: 0");
        totalRequestsLabel.setStyle("-fx-font-size: 14px;");
        
        requestsPerSecondLabel = new Label("Requests/Second: 0.0");
        requestsPerSecondLabel.setStyle("-fx-font-size: 14px;");
        
        uptimeLabel = new Label("Uptime: 00:00:00");
        uptimeLabel.setStyle("-fx-font-size: 14px;");
        
        panel.getChildren().addAll(statsTitle, totalRequestsLabel, requestsPerSecondLabel, uptimeLabel);
        return panel;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
        
        statusLabel = new Label("Status: STOPPED");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        connectionCountLabel = new Label("Connections: 0");
        connectionCountLabel.setStyle("-fx-text-fill: white;");

        uniqueUsersLabel = new Label("Unique Users: 0");
        uniqueUsersLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(statusLabel, connectionCountLabel, uniqueUsersLabel, spacer);
        return statusBar;
    }

    private void browseWebRoot() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select WebRoot Directory");
        File selectedDirectory = directoryChooser.showDialog(webrootField.getScene().getWindow());
        if (selectedDirectory != null) {
            webrootField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    // Public methods for ServerController to update UI
    public void updateStatus(String status, boolean isRunning) {
        Platform.runLater(() -> {
            statusLabel.setText("Status: " + status);
            if (isRunning) {
                statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                startButton.setDisable(true);
                stopButton.setDisable(false);
                restartButton.setDisable(false);
                portField.setDisable(true);
                webrootField.setDisable(true);
                browseButton.setDisable(true);
            } else {
                statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                startButton.setDisable(false);
                stopButton.setDisable(true);
                restartButton.setDisable(true);
                portField.setDisable(false);
                webrootField.setDisable(false);
                browseButton.setDisable(false);
            }
        });
    }

    public void appendConsole(String message) {
        Platform.runLater(() -> {
            consoleArea.appendText(message + "\n");
            consoleArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void addRequestLog(String method, String path, String status, String clientIp) {
        Platform.runLater(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            requestData.add(new RequestLog(timestamp, method, path, status, clientIp));
            
            // Keep only last 1000 entries
            if (requestData.size() > 1000) {
                requestData.remove(0);
            }
        });
    }

    public void updateConnectionCount(int count) {
        Platform.runLater(() -> connectionCountLabel.setText("Connections: " + count));
    }

    public void updateUniqueUsersCount(int count) {
        Platform.runLater(() -> uniqueUsersLabel.setText("Unique Users: " + count));
    }

    public void updateStatistics(int totalRequests, double requestsPerSecond, String uptime) {
        Platform.runLater(() -> {
            totalRequestsLabel.setText("Total Requests: " + totalRequests);
            requestsPerSecondLabel.setText("Requests/Second: " + String.format("%.2f", requestsPerSecond));
            uptimeLabel.setText("Uptime: " + uptime);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Inner class for request log entries
    public static class RequestLog {
        private String timestamp;
        private String method;
        private String path;
        private String status;
        private String clientIp;

        public RequestLog(String timestamp, String method, String path, String status, String clientIp) {
            this.timestamp = timestamp;
            this.method = method;
            this.path = path;
            this.status = status;
            this.clientIp = clientIp;
        }

        public String getTimestamp() { return timestamp; }
        public String getMethod() { return method; }
        public String getPath() { return path; }
        public String getStatus() { return status; }
        public String getClientIp() { return clientIp; }
    }
}
