package com.angelbroking.smartapi.gui;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.algos.Backtester;
import com.angelbroking.smartapi.algos.Strategy;
import com.angelbroking.smartapi.algos.strategies.MACrossoverStrategy;
import com.angelbroking.smartapi.algos.strategies.RsiDeviationStrategy;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.BacktestReport;
import com.angelbroking.smartapi.models.Candle;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.TradeLog;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.utils.Constants;
import com.google.gson.Gson;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SmartApiGui extends Application {

    private SmartConnect smartConnect;
    private Stage primaryStage;

    private final String lightModeCss = getClass().getResource("/styles/light-mode.css").toExternalForm();
    private static final Logger log = LoggerFactory.getLogger(SmartApiGui.class);

    private final String darkModeCss = getClass().getResource("/styles/dark-mode.css").toExternalForm();
    private WebEngine chartWebEngine;
    private boolean isLiveChartActive = false;
    private String currentLiveToken = null;
    private String currentLiveExchange = null;

    // Removed hardcoded credentials
    // private static final String API_KEY = "YOUR_API_KEY"; // Example: "HFnOUXPi";
    // private static final String USERNAME = "YOUR_CLIENT_ID"; // Example: "AAAL432762";
    // private static final String PASSWORD = "YOUR_PASSWORD_OR_PIN"; // Example: "4321";

    private CheckBox candleDataTabLiveFeedCheckBox;
    private TextField candleDataTabSymbolTokenField;
    private ComboBox<String> candleDataTabExchangeComboBox;
    private DatePicker candleDataTabFromDatePicker;
    private TextField candleDataTabFromTimeField;
    private DatePicker candleDataTabToDatePicker;
    private TextField candleDataTabToTimeField;
    private Button candleDataTabFetchCandleDataButton;

    private WebEngine historicalDataChartEngine;
    private WebView historicalDataChartWebView;
    private Stage detachedHistoricalStage;
    private WebEngine detachedHistoricalWebEngine;
    private boolean isHistoricalChartDetached = false;
    private Label historicalChartPlaceholder; // Added declaration
    private StackPane historicalChartContainer; // Added declaration

    private Stage detachedBacktestChartStage;
    private WebEngine detachedBacktestChartEngine;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.smartConnect = new SmartConnect();
        // API key will be set from the login form
        // this.smartConnect.setApiKey(API_KEY);

        primaryStage.setTitle("SmartAPI Login");
        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(40, 50, 40, 50));
        loginLayout.getStyleClass().add("login-pane");

        Text scenetitle = new Text("SmartAPI Login");
        scenetitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        scenetitle.getStyleClass().add("login-title");

        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(15);

        Label apiKeyLabel = new Label("API Key:");
        formGrid.add(apiKeyLabel, 0, 0);
        TextField apiKeyTextField = new TextField();
        apiKeyTextField.setPromptText("Enter your API Key");
        apiKeyTextField.setPrefWidth(250);
        formGrid.add(apiKeyTextField, 1, 0);

        Label userNameLabel = new Label("Username (Client ID):");
        formGrid.add(userNameLabel, 0, 1);
        TextField userNameTextField = new TextField();
        userNameTextField.setPromptText("Enter your Client ID");
        userNameTextField.setPrefWidth(250);
        formGrid.add(userNameTextField, 1, 1);

        Label pwLabel = new Label("Password:");
        formGrid.add(pwLabel, 0, 2);
        PasswordField pwBox = new PasswordField();
        // pwBox.setText(PASSWORD); // Removed pre-filled password
        pwBox.setPromptText("Enter your Password/PIN");
        pwBox.setPrefWidth(250);
        formGrid.add(pwBox, 1, 2);

        Label totpLabel = new Label("TOTP:");
        formGrid.add(totpLabel, 0, 3);
        TextField totpTextField = new TextField();
        totpTextField.setPromptText("Enter current TOTP");
        totpTextField.setPrefWidth(250);
        formGrid.add(totpTextField, 1, 3);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(100);
        loginButton.getStyleClass().add("login-button");
        loginButton.setDefaultButton(true);
        formGrid.add(loginButton, 1, 4);

        final Text actiontarget = new Text();
        actiontarget.setWrappingWidth(250);
        actiontarget.getStyleClass().add("login-action-target");

        totpTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });

        loginLayout.getChildren().addAll(scenetitle, formGrid, actiontarget);

        loginButton.setOnAction(e -> {
            String apiKey = apiKeyTextField.getText();
            String clientCode = userNameTextField.getText();
            String password = pwBox.getText();
            String totp = totpTextField.getText();

            if (totp == null || totp.trim().isEmpty()) {
                updateActionTargetStyle(actiontarget, "TOTP cannot be empty.", false);
                actiontarget.setText("TOTP cannot be empty.");
                return;
            }

            smartConnect.setApiKey(apiKey);

            updateActionTargetStyle(actiontarget, "Attempting login...", null);
            actiontarget.setText("Attempting login...");
            loginButton.setDisable(true);

            new Thread(() -> {
                try {
                    User user = smartConnect.generateSession(clientCode, password, totp);
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        if (user != null && user.getAccessToken() != null) {
                            updateActionTargetStyle(actiontarget, "Login successful!", true);
                            showDashboardScreen(user);
                        } else {
                            String errorMessage = "Login failed. ";
                            updateActionTargetStyle(actiontarget, errorMessage + "Please check credentials and TOTP.", false);
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        updateActionTargetStyle(actiontarget, "Login error: " + ex.getMessage(), false);
                        ex.printStackTrace();
                    });
                }
            }).start();
        });

        Scene scene = new Scene(loginLayout, 450, 450);
        primaryStage.setScene(scene);
        applyStylesheet(scene, lightModeCss);
        primaryStage.show();
    }

    private void updateActionTargetStyle(Text actionTarget, String message, Boolean success) {
        actionTarget.setText(message);
        actionTarget.getStyleClass().removeAll("success", "error");
        if (success == null) {
        } else if (success) {
            actionTarget.getStyleClass().add("success");
        } else {
            actionTarget.getStyleClass().add("error");
        }
    }

    private void applyStylesheet(Scene scene, String cssPath) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(cssPath);
    }

    private TableView<Order> orderTable;
    private StackPane contentArea;
    private TableView<JSONObject> holdingsTable;
    private TableView<JSONObject> positionsTable;

    private void showDashboardScreen(User user) {
        this.orderTable = new TableView<>();
        primaryStage.setTitle("SmartAPI Dashboard");

        BorderPane rootLayout = new BorderPane();

        VBox topControls = new VBox(10);
        topControls.setAlignment(Pos.CENTER_LEFT);
        topControls.setPadding(new Insets(10));

        GridPane topGrid = new GridPane();
        topGrid.setHgap(20);
        topGrid.setAlignment(Pos.CENTER_LEFT);

        Text welcomeText = new Text("Welcome, " + (user.getUserName() != null ? user.getUserName() : user.getUserId()) + "!");
        welcomeText.getStyleClass().add("welcome-text");
        welcomeText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        topGrid.add(welcomeText, 0, 0);

        ToggleButton themeToggle = new ToggleButton("Dark Mode");
        themeToggle.setSelected(primaryStage.getScene().getStylesheets().contains(darkModeCss));
        themeToggle.setOnAction(event -> {
            String cssToApply;
            String themeNameForJs;
            if (themeToggle.isSelected()) {
                cssToApply = darkModeCss;
                themeToggle.setText("Light Mode");
                themeNameForJs = "dark";
            } else {
                cssToApply = lightModeCss;
                themeToggle.setText("Dark Mode");
                themeNameForJs = "light";
            }
            applyStylesheet(primaryStage.getScene(), cssToApply);

            // Update embedded historical chart engine
            if (historicalDataChartEngine != null) {
                historicalDataChartEngine.executeScript("applyTheme('" + themeNameForJs + "');");
            }
            // Update detached historical chart engine
            if (detachedHistoricalWebEngine != null && detachedHistoricalStage != null && detachedHistoricalStage.isShowing()) {
                detachedHistoricalWebEngine.executeScript("applyTheme('" + themeNameForJs + "');");
                applyStylesheet(detachedHistoricalStage.getScene(), cssToApply);
            }
            // Update detached algo chart engine
            if (detachedBacktestChartEngine != null && detachedBacktestChartStage != null && detachedBacktestChartStage.isShowing()) {
                detachedBacktestChartEngine.executeScript("applyTheme('" + themeNameForJs + "');");
                applyStylesheet(detachedBacktestChartStage.getScene(), cssToApply);
            }
        });
        topGrid.add(themeToggle, 1, 0);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            logoutButton.setDisable(true);
            new Thread(() -> {
                try {
                    if (smartConnect.getAccessToken() != null) {
                        JSONObject logoutResponse = smartConnect.logout();
                        Platform.runLater(() -> {
                            if (logoutResponse != null && logoutResponse.optBoolean("status", false)) {
                                System.out.println("Logout successful via API.");
                            } else {
                                System.err.println("Logout API call failed or status false.");
                            }
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Error during logout: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    Platform.runLater(() -> {
                        logoutButton.setDisable(false);
                        primaryStage.setTitle("SmartAPI Login");
                        showLoginScreen();
                    });
                }
            }).start();
        });
        VBox logoutBox = new VBox(logoutButton);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(logoutBox, javafx.scene.layout.Priority.ALWAYS);
        topGrid.add(logoutBox, 2, 0);

        topControls.getChildren().add(topGrid);
        rootLayout.setTop(topControls);

        VBox navPane = new VBox(10);
        navPane.setPadding(new Insets(10));
        navPane.getStyleClass().add("nav-pane");
        navPane.setPrefWidth(180);

        contentArea = new StackPane();
        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(10));

        GridPane profileDetailsGrid = new GridPane();
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(8);
        detailsGrid.setPadding(new Insets(10));

        Label userIdLabelKey = new Label("User ID:");
        Text userIdText = new Text("Loading...");
        userIdText.getStyleClass().add("profile-value-text");

        Label userNameLabelKey = new Label("User Name:");
        Text userNameText = new Text("Loading...");
        userNameText.getStyleClass().add("profile-value-text");

        Label emailLabelKey = new Label("Email:");
        Text emailText = new Text("Loading...");
        emailText.getStyleClass().add("profile-value-text");

        Label mobileLabelKey = new Label("Mobile No:");
        Text mobileText = new Text("Loading...");
        mobileText.getStyleClass().add("profile-value-text");

        Label exchangesLabelKey = new Label("Exchanges:");
        Text exchangesText = new Text("Loading...");
        exchangesText.getStyleClass().add("profile-value-text");

        Label productsLabelKey = new Label("Products:");
        Text productsText = new Text("Loading...");
        productsText.getStyleClass().add("profile-value-text");

        Label orderTypesLabelKey = new Label("Order Types:");
        Text orderTypesText = new Text("Loading...");
        orderTypesText.getStyleClass().add("profile-value-text");

        profileDetailsGrid.add(userIdLabelKey, 0, 0);
        profileDetailsGrid.add(userIdText, 1, 0);
        profileDetailsGrid.add(userNameLabelKey, 0, 1);
        profileDetailsGrid.add(userNameText, 1, 1);
        profileDetailsGrid.add(emailLabelKey, 0, 2);
        profileDetailsGrid.add(emailText, 1, 2);
        profileDetailsGrid.add(mobileLabelKey, 0, 3);
        profileDetailsGrid.add(mobileText, 1, 3);

        profileDetailsGrid.add(exchangesLabelKey, 0, 4);
        profileDetailsGrid.add(exchangesText, 1, 4);
        GridPane.setValignment(exchangesLabelKey, javafx.geometry.VPos.TOP);
        exchangesText.setWrappingWidth(350);

        profileDetailsGrid.add(productsLabelKey, 0, 5);
        profileDetailsGrid.add(productsText, 1, 5);
        GridPane.setValignment(productsLabelKey, javafx.geometry.VPos.TOP);
        productsText.setWrappingWidth(350);

        profileDetailsGrid.add(orderTypesLabelKey, 0, 6);
        profileDetailsGrid.add(orderTypesText, 1, 6);
        GridPane.setValignment(orderTypesLabelKey, javafx.geometry.VPos.TOP);
        orderTypesText.setWrappingWidth(350);

        new Thread(() -> {
            try {
                User fullProfile = smartConnect.getProfile();
                Platform.runLater(() -> {
                    if (fullProfile != null) {
                        userIdText.setText(fullProfile.getUserId() != null ? fullProfile.getUserId() : "N/A");
                        userNameText.setText(fullProfile.getUserName() != null ? fullProfile.getUserName() : "N/A");
                        emailText.setText(fullProfile.getEmail() != null && !fullProfile.getEmail().isEmpty() ? fullProfile.getEmail() : "N/A");
                        mobileText.setText(fullProfile.getMobileNo() != null && !fullProfile.getMobileNo().isEmpty() ? fullProfile.getMobileNo() : "N/A");
                        exchangesText.setText(fullProfile.getExchanges() != null ? String.join(", ", fullProfile.getExchanges()) : "N/A");
                        productsText.setText(fullProfile.getProducts() != null ? String.join(", ", fullProfile.getProducts()) : "N/A");
                        orderTypesText.setText(fullProfile.getOrderTypes() != null && fullProfile.getOrderTypes().isEmpty() ? String.join(", ", fullProfile.getOrderTypes()) : "N/A");
                    } else {
                        userIdText.setText("Failed to load");
                        userNameText.setText("Failed to load");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    userIdText.setText("Error loading profile");
                    ex.printStackTrace();
                });
            }
        }).start();

        BorderPane ordersContentLayout = new BorderPane();

        VBox ordersControls = new VBox(10);
        ordersControls.setPadding(new Insets(0, 0, 10, 0));
        Button refreshOrderBookButton = new Button("Refresh Order Book");
        Button placeNewOrderButton = new Button("Place New Order");
        Button modifyOrderButton = new Button("Modify Order");
        modifyOrderButton.setOnAction(e -> showPlaceOrderDialog(orderTable.getSelectionModel().getSelectedItem()));
        modifyOrderButton.setDisable(true);
        modifyOrderButton.setOnAction(e -> showPlaceOrderDialog(orderTable.getSelectionModel().getSelectedItem()));
        modifyOrderButton.setOnAction(e -> showPlaceOrderDialog(this.orderTable.getSelectionModel().getSelectedItem()));

        placeNewOrderButton.setOnAction(e -> showPlaceOrderDialog());
        modifyOrderButton.setOnAction(e -> showPlaceOrderDialog(orderTable.getSelectionModel().getSelectedItem()));
        modifyOrderButton.setDisable(true);

        ordersControls.getChildren().addAll(refreshOrderBookButton, placeNewOrderButton, modifyOrderButton);
        ordersContentLayout.setTop(ordersControls);

        TableView<Order> orderTable = new TableView<>();
        setupOrderTableColumns(orderTable);
        ordersContentLayout.setCenter(orderTable);

        refreshOrderBookButton.setOnAction(e -> fetchOrderBook(orderTable));

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            modifyOrderButton.setDisable(newSelection == null);
        });

        VBox holdingsPositionsContent = new VBox(10);
        holdingsPositionsContent.setPadding(new Insets(15));
        holdingsPositionsContent.setAlignment(Pos.CENTER);
        holdingsPositionsContent.getChildren().add(new Label("Holdings and Positions will be displayed here."));

        BorderPane holdingsPositionsContentLayout = new BorderPane();
        holdingsPositionsContentLayout.setPadding(new Insets(15));

        VBox holdingsControls = new VBox(10);
        holdingsControls.setPadding(new Insets(0, 0, 10, 0));
        Button refreshHoldingsButton = new Button("Refresh Holdings");
        Button refreshPositionsButton = new Button("Refresh Positions");
        holdingsControls.getChildren().addAll(refreshHoldingsButton, refreshPositionsButton);
        holdingsPositionsContentLayout.setTop(holdingsControls);

        Label holdingsHeaderLabel = new Label("Holdings");
        holdingsHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        GridPane totalHoldingsGrid = new GridPane();
        totalHoldingsGrid.setHgap(10);
        totalHoldingsGrid.setVgap(5);
        totalHoldingsGrid.setPadding(new Insets(0, 0, 10, 0));
        Label totalHoldingValueLabel = new Label("Total Holding Value: N/A");
        Label totalInvestmentValueLabel = new Label("Total Investment: N/A");
        Label totalPnlLabel = new Label("Total P&L: N/A");
        totalHoldingsGrid.add(totalHoldingValueLabel, 0, 0);
        totalHoldingsGrid.add(totalInvestmentValueLabel, 1, 0);
        totalHoldingsGrid.add(totalPnlLabel, 2, 0);

        this.holdingsTable = new TableView<>();
        setupHoldingsTableColumns(holdingsTable);

        VBox holdingsSection = new VBox(10, holdingsHeaderLabel, totalHoldingsGrid, holdingsTable);

        Label positionsHeaderLabel = new Label("Positions");
        positionsHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        this.positionsTable = new TableView<>();
        setupPositionsTableColumns(positionsTable);

        VBox positionsSection = new VBox(10, positionsHeaderLabel, positionsTable);

        VBox centerContent = new VBox(20, holdingsSection, positionsSection);
        centerContent.setPadding(new Insets(10, 0, 0, 0));
        holdingsPositionsContentLayout.setCenter(centerContent);

        refreshHoldingsButton.setOnAction(e -> fetchHoldingsData(totalHoldingValueLabel, totalInvestmentValueLabel, totalPnlLabel));
        refreshPositionsButton.setOnAction(e -> fetchPositionsData());

        Button profileNavButton = new Button("Profile");
        profileNavButton.setMaxWidth(Double.MAX_VALUE);
        profileNavButton.getStyleClass().add("nav-button");

        Button ordersNavButton = new Button("Orders");
        ordersNavButton.setMaxWidth(Double.MAX_VALUE);
        ordersNavButton.getStyleClass().add("nav-button");

        Button holdingsNavButton = new Button("Holdings & Positions");
        holdingsNavButton.setMaxWidth(Double.MAX_VALUE);
        holdingsNavButton.getStyleClass().add("nav-button");

        Button historicalDataNavButton = new Button("Historical Data");
        historicalDataNavButton.setMaxWidth(Double.MAX_VALUE);
        historicalDataNavButton.getStyleClass().add("nav-button");
        Button backtestingNavButton = new Button("Backtesting");
        backtestingNavButton.setMaxWidth(Double.MAX_VALUE);
        backtestingNavButton.getStyleClass().add("nav-button");

        List<Button> navButtons = Arrays.asList(profileNavButton, ordersNavButton, holdingsNavButton, historicalDataNavButton, backtestingNavButton);

        profileNavButton.setOnAction(e -> {
            ScrollPane scrollPane = new ScrollPane(profileDetailsGrid);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            contentArea.getChildren().setAll(scrollPane);
            updateNavButtonSelection(navButtons, profileNavButton);
        });

        ordersNavButton.setOnAction(e -> {
            ScrollPane scrollPane = new ScrollPane(ordersContentLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            contentArea.getChildren().setAll(scrollPane);
            updateNavButtonSelection(navButtons, ordersNavButton);
            if (orderTable.getItems().isEmpty()) {
                fetchOrderBook(orderTable);
            }
        });

        holdingsNavButton.setOnAction(e -> {
            ScrollPane scrollPane = new ScrollPane(holdingsPositionsContentLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            contentArea.getChildren().setAll(scrollPane);
            updateNavButtonSelection(navButtons, holdingsNavButton);
            if (holdingsTable.getItems().isEmpty()) {
                fetchHoldingsData(totalHoldingValueLabel, totalInvestmentValueLabel, totalPnlLabel);
                fetchPositionsData();
            }
        });
        
        historicalDataNavButton.setOnAction(e -> {
            Node historicalDataNode = createCandleDataTab();
            ScrollPane scrollPane = new ScrollPane(historicalDataNode);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            contentArea.getChildren().setAll(scrollPane);
            updateNavButtonSelection(navButtons, historicalDataNavButton);
        });

        backtestingNavButton.setOnAction(e -> {
            updateNavButtonSelection(navButtons, backtestingNavButton);
            Node backtestingNode = createBacktestingTab();
            ScrollPane scrollPane = new ScrollPane(backtestingNode);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            contentArea.getChildren().setAll(scrollPane);
        });

        navPane.getChildren().addAll(profileNavButton, ordersNavButton, holdingsNavButton, historicalDataNavButton, backtestingNavButton);

        rootLayout.setLeft(navPane);
        rootLayout.setCenter(contentArea);

        ScrollPane initialScrollPane = new ScrollPane(profileDetailsGrid);
        initialScrollPane.setFitToWidth(true);
        initialScrollPane.setFitToHeight(true);
        contentArea.getChildren().add(initialScrollPane);
        updateNavButtonSelection(navButtons, profileNavButton);

        Scene scene = new Scene(rootLayout, 800, 600);
        applyStylesheet(scene, primaryStage.getScene().getStylesheets().isEmpty() ? lightModeCss : primaryStage.getScene().getStylesheets().get(0));
        primaryStage.setScene(scene);
    }

    private void setupOrderTableColumns(TableView<Order> orderTable) {
        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("tradingSymbol"));

        TableColumn<Order, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        TableColumn<Order, String> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Order, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productType"));

        TableColumn<Order, String> exchangeCol = new TableColumn<>("Exchange");
        exchangeCol.setCellValueFactory(new PropertyValueFactory<>("exchange"));

        orderTable.getColumns().addAll(orderIdCol, symbolCol, typeCol, qtyCol, priceCol, statusCol, productCol, exchangeCol);
        orderTable.setPlaceholder(new Label("No orders to display. Click 'Refresh Order Book'."));
    }

    private void fetchOrderBook(TableView<Order> orderTable) {
        Label placeholder = (Label) orderTable.getPlaceholder();
        if (placeholder != null) {
            placeholder.setText("Fetching order book...");
        }

        new Thread(() -> {
            try {
                JSONObject orderBookJson = smartConnect.getOrderHistory(smartConnect.getUserId());
                List<Order> orders = new ArrayList<>();
                if (orderBookJson != null && orderBookJson.optBoolean("status", false)) {
                    JSONArray dataArray = orderBookJson.optJSONArray("data");
                    if (dataArray != null) {
                        Gson gson = new Gson();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject orderJson = dataArray.getJSONObject(i);
                            Order order = gson.fromJson(orderJson.toString(), Order.class);
                            orders.add(order);
                        }
                    }
                }

                Platform.runLater(() -> {
                    orderTable.getItems().setAll(orders);
                    if (placeholder != null) {
                        placeholder.setText(orders.isEmpty() ? "No orders found." : "No orders to display. Click 'Refresh Order Book'.");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (placeholder != null) {
                        placeholder.setText("Error fetching order book: " + e.getMessage());
                    }
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Order Book Error");
                    alert.setHeaderText("Failed to fetch order book");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }

    private void updateNavButtonSelection(List<Button> allNavButtons, Button selectedButton) {
        for (Button btn : allNavButtons) {
            btn.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), false);
        }
        selectedButton.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), true);
    }

    private void setupPositionsTableColumns(TableView<JSONObject> table) {
        TableColumn<JSONObject, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().optString("tradingsymbol")));

        TableColumn<JSONObject, String> exchangeCol = new TableColumn<>("Exchange");
        exchangeCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().optString("exchange")));

        TableColumn<JSONObject, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().optString("producttype")));

        TableColumn<JSONObject, Integer> netQtyCol = new TableColumn<>("Net Qty");
        netQtyCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optInt("netqty")));

        TableColumn<JSONObject, Integer> buyQtyCol = new TableColumn<>("Buy Qty");
        buyQtyCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optInt("buyqty")));

        TableColumn<JSONObject, Integer> sellQtyCol = new TableColumn<>("Sell Qty");
        sellQtyCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optInt("sellqty")));

        TableColumn<JSONObject, Double> buyAvgCol = new TableColumn<>("Buy Avg");
        buyAvgCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optDouble("buyavgprice")));

        TableColumn<JSONObject, Double> sellAvgCol = new TableColumn<>("Sell Avg");
        sellAvgCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optDouble("sellavgprice")));

        TableColumn<JSONObject, Double> ltpCol = new TableColumn<>("LTP");
        ltpCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optDouble("ltp")));

        TableColumn<JSONObject, Double> pnlCol = new TableColumn<>("P&L");
        pnlCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().optDouble("realisedprofitandloss")));

        table.getColumns().addAll(symbolCol, exchangeCol, productCol, netQtyCol, buyQtyCol, sellQtyCol, buyAvgCol, sellAvgCol, ltpCol, pnlCol);
        table.setPlaceholder(new Label("No positions to display. Click 'Refresh Positions'."));
    }

    private void fetchPositionsData() {
        if (positionsTable.getPlaceholder() instanceof Label) {
            ((Label) positionsTable.getPlaceholder()).setText("Fetching positions...");
        }
        new Thread(() -> {
            try {
                JSONObject positionsJson = smartConnect.getPosition();
                List<JSONObject> positionItems = new ArrayList<>();
                if (positionsJson != null && positionsJson.optBoolean("status", false)) {
                    JSONArray dataArray = positionsJson.optJSONArray("data");
                    if (dataArray != null) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            positionItems.add(dataArray.getJSONObject(i));
                        }
                    }
                }
                Platform.runLater(() -> {
                    positionsTable.getItems().setAll(positionItems);
                    if (positionsTable.getPlaceholder() instanceof Label) {
                        ((Label) positionsTable.getPlaceholder()).setText(positionItems.isEmpty() ? "No positions found." : "No positions to display. Click 'Refresh Positions'.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (positionsTable.getPlaceholder() instanceof Label) {
                        ((Label) positionsTable.getPlaceholder()).setText("Error fetching positions: " + e.getMessage());
                    }
                    showErrorAlert("Positions Error", "Failed to fetch positions: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void showPlaceOrderDialog() {
        showPlaceOrderDialog(null);
    }

    private void showPlaceOrderDialog(Order existingOrder) {
        Dialog<OrderParams> dialog = new Dialog<>();
        if (existingOrder != null) {
            dialog.setTitle("Modify Order");
            dialog.setHeaderText("Modify Order Details (ID: " + existingOrder.orderId + ")");
        } else {
            dialog.setTitle("Place New Order");
            dialog.setHeaderText("Enter Order Details");
        }

        ButtonType placeOrderButtonType = new ButtonType("Place Order", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(placeOrderButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField searchKeywordField = new TextField();
        searchKeywordField.setPromptText("Scrip keyword (e.g., RELIANCE)");
        Button searchScripButton = new Button("Search Scrip");
        ListView<JSONObject> searchResultsListView = new ListView<>();
        searchResultsListView.setPrefHeight(100);
        searchResultsListView.setPlaceholder(new Label("Select exchange and enter keyword to search."));

        TextField tradingSymbolField = new TextField();
        tradingSymbolField.setPromptText("e.g., SBIN-EQ");
        tradingSymbolField.setEditable(existingOrder == null);

        TextField symbolTokenField = new TextField();
        symbolTokenField.setPromptText("e.g., 3045");
        symbolTokenField.setEditable(existingOrder == null);

        tradingSymbolField.setEditable(false);
        symbolTokenField.setEditable(false);

        ComboBox<String> exchangeComboBox = new ComboBox<>();
        exchangeComboBox.getItems().addAll(Constants.EXCHANGE_NSE, Constants.EXCHANGE_BSE, Constants.EXCHANGE_NFO, Constants.EXCHANGE_MCX, Constants.EXCHANGE_CDS);
        exchangeComboBox.getSelectionModel().selectFirst();
        exchangeComboBox.setDisable(existingOrder != null);

        ComboBox<String> transactionTypeComboBox = new ComboBox<>();
        transactionTypeComboBox.getItems().addAll(Constants.TRANSACTION_TYPE_BUY, Constants.TRANSACTION_TYPE_SELL);
        transactionTypeComboBox.getSelectionModel().selectFirst();
        transactionTypeComboBox.setDisable(existingOrder != null);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        ComboBox<String> productTypeComboBox = new ComboBox<>();
        productTypeComboBox.getItems().addAll(Constants.PRODUCT_DELIVERY, Constants.PRODUCT_INTRADAY, Constants.PRODUCT_MARGIN, Constants.PRODUCT_CARRYFORWARD, Constants.PRODUCT_BO);
        productTypeComboBox.getSelectionModel().selectFirst();
        productTypeComboBox.setDisable(existingOrder != null);

        ComboBox<String> orderTypeComboBox = new ComboBox<>();
        orderTypeComboBox.getItems().addAll(Constants.ORDER_TYPE_MARKET, Constants.ORDER_TYPE_LIMIT, Constants.ORDER_TYPE_STOPLOSS_LIMIT, Constants.ORDER_TYPE_STOPLOSS_MARKET);
        orderTypeComboBox.getSelectionModel().selectFirst();

        TextField priceField = new TextField("0");
        priceField.setPromptText("Price (0 for MARKET)");
        TextField triggerPriceField = new TextField("0");
        triggerPriceField.setPromptText("Trigger Price (for SL orders)");

        ComboBox<String> varietyComboBox = new ComboBox<>();
        varietyComboBox.getItems().addAll(Constants.VARIETY_NORMAL, Constants.VARIETY_STOPLOSS, Constants.VARIETY_AMO, Constants.VARIETY_ROBO);
        varietyComboBox.getSelectionModel().selectFirst();
        varietyComboBox.setDisable(existingOrder != null);

        ComboBox<String> durationComboBox = new ComboBox<>();
        durationComboBox.getItems().addAll(Constants.DURATION_DAY, Constants.DURATION_IOC);
        durationComboBox.getSelectionModel().selectFirst();

        grid.add(new Label("Search Exchange:"), 0, 0);
        grid.add(exchangeComboBox, 1, 0);
        grid.add(new Label("Search Keyword:"), 0, 1);
        grid.add(searchKeywordField, 1, 1);
        grid.add(searchScripButton, 2, 1);
        grid.add(new Label("Search Results:"), 0, 2);
        grid.add(searchResultsListView, 1, 2, 2, 1);

        int currentRow = 3;
        grid.add(new Label("Selected Symbol:"), 0, currentRow);
        grid.add(tradingSymbolField, 1, currentRow++);
        grid.add(new Label("Selected Token:"), 0, currentRow);
        grid.add(symbolTokenField, 1, currentRow++);
        grid.add(new Label("Transaction Type:"), 0, currentRow);
        grid.add(transactionTypeComboBox, 1, currentRow++);
        grid.add(new Label("Quantity:"), 0, currentRow);
        grid.add(quantityField, 1, currentRow++);
        grid.add(new Label("Product Type:"), 0, currentRow);
        grid.add(productTypeComboBox, 1, currentRow++);
        grid.add(new Label("Order Type:"), 0, currentRow);
        grid.add(orderTypeComboBox, 1, currentRow++);
        grid.add(new Label("Price:"), 0, currentRow);
        grid.add(priceField, 1, currentRow++);
        grid.add(new Label("Trigger Price:"), 0, currentRow);
        grid.add(triggerPriceField, 1, currentRow++);
        grid.add(new Label("Variety:"), 0, currentRow);
        grid.add(varietyComboBox, 1, currentRow++);
        grid.add(new Label("Duration:"), 0, currentRow);
        grid.add(durationComboBox, 1, currentRow++);

        if (existingOrder != null) {
            tradingSymbolField.setText(existingOrder.tradingSymbol);
            symbolTokenField.setText(existingOrder.symbolToken);
            exchangeComboBox.setValue(existingOrder.exchange);
            searchKeywordField.setDisable(true);
            searchScripButton.setDisable(true);
            searchResultsListView.setDisable(true);

            transactionTypeComboBox.setValue(existingOrder.transactionType);
            quantityField.setText(existingOrder.quantity);
            productTypeComboBox.setValue(existingOrder.productType);
            orderTypeComboBox.setValue(existingOrder.orderType);
            priceField.setText(existingOrder.price);
            triggerPriceField.setText(existingOrder.triggerPrice != null ? existingOrder.triggerPrice : "0");
            varietyComboBox.setValue(existingOrder.variety);
            durationComboBox.setValue(existingOrder.duration);
        } else {
            tradingSymbolField.clear();
            symbolTokenField.clear();
        }

        dialog.getDialogPane().setContent(grid);

        Node placeOrderNode = dialog.getDialogPane().lookupButton(placeOrderButtonType);
        placeOrderNode.setDisable(existingOrder == null);

        searchScripButton.setOnAction(event -> {
            String keyword = searchKeywordField.getText();
            String exchange = exchangeComboBox.getValue();
            if (keyword == null || keyword.trim().isEmpty() || exchange == null) {
                showErrorAlert("Search Scrip", "Please enter a keyword and select an exchange.");
                return;
            }
            searchResultsListView.setPlaceholder(new Label("Searching..."));
            searchResultsListView.getItems().clear();

            new Thread(() -> {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("exchange", exchange);
                    payload.put("searchscrip", keyword);
                    String responseStr = smartConnect.getSearchScrip(payload);
                    JSONArray resultsArray = parseHumanReadableScripSearch(responseStr);

                    final JSONArray finalResultsArray = resultsArray;
                    Platform.runLater(() -> {
                        if (finalResultsArray == null || finalResultsArray.length() == 0) {
                            searchResultsListView.setPlaceholder(new Label("No scrips found."));
                        } else {
                            for (int i = 0; i < finalResultsArray.length(); i++) {
                                searchResultsListView.getItems().add(finalResultsArray.getJSONObject(i));
                            }
                            searchResultsListView.setPlaceholder(new Label("Select exchange and enter keyword to search."));
                        }
                    });
                } catch (SmartAPIException | IOException | org.json.JSONException e) {
                    Platform.runLater(() -> {
                        searchResultsListView.setPlaceholder(new Label("Error searching scrips."));
                        showErrorAlert("Search Scrip Error", "Failed to search scrips: " + e.getMessage());
                        e.printStackTrace();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        searchResultsListView.setPlaceholder(new Label("Unexpected error during search."));
                        showErrorAlert("Search Scrip Error", "An unexpected error occurred: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
            }).start();
        });

        searchResultsListView.setCellFactory(lv -> new ListCell<JSONObject>() {
            @Override
            protected void updateItem(JSONObject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.optString("tradingsymbol", "N/A") + " (" + item.optString("name", "N/A") + ") - " + item.optString("exchange"));
            }
        });

        searchResultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedJson) -> {
            if (selectedJson != null) {
                tradingSymbolField.setText(selectedJson.optString("tradingsymbol"));
                symbolTokenField.setText(selectedJson.optString("symboltoken"));
            }
        });

        Runnable validateFields = () -> {
            boolean disable = (tradingSymbolField.getText().trim().isEmpty() || symbolTokenField.getText().trim().isEmpty()) ||
                              quantityField.getText().trim().isEmpty();
            placeOrderNode.setDisable(disable);
        };
        tradingSymbolField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        symbolTokenField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == placeOrderButtonType) {
                OrderParams params = new OrderParams();
                params.tradingsymbol = tradingSymbolField.getText();
                params.symboltoken = symbolTokenField.getText();
                params.exchange = exchangeComboBox.getValue();
                params.transactiontype = transactionTypeComboBox.getValue();
                try {
                    params.quantity = Integer.parseInt(quantityField.getText());
                    params.price = Double.parseDouble(priceField.getText());
                    params.triggerprice = (triggerPriceField.getText() == null || triggerPriceField.getText().trim().isEmpty() || triggerPriceField.getText().equals("0")) ? "0" : triggerPriceField.getText();
                } catch (NumberFormatException ex) {
                    showErrorAlert("Invalid Input", "Quantity and Price must be numbers.");
                    return null;
                }
                params.producttype = productTypeComboBox.getValue();
                params.ordertype = orderTypeComboBox.getValue();
                params.variety = varietyComboBox.getValue();
                params.duration = durationComboBox.getValue();
                return params;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(orderParams -> {
            if (orderParams != null) {
                if (existingOrder != null) {
                    modifyOrderInBackground(existingOrder.orderId, orderParams, existingOrder.variety);
                } else {
                    placeOrderInBackground(orderParams);
                }
            }
        });
    }

    private void placeOrderInBackground(OrderParams orderParams) {
        System.out.println("Placing order: " + orderParams.tradingsymbol);
        new Thread(() -> {
            try {
                Order orderResponse = smartConnect.placeOrder(orderParams, orderParams.variety);
                Platform.runLater(() -> {
                    if (orderResponse != null && orderResponse.orderId != null) {
                        showInfoAlert("Order Placed", "Order ID: " + orderResponse.orderId + "\nUnique ID: " + orderResponse.uniqueOrderId);
                        findOrderTableAndRefresh();
                    } else {
                        showErrorAlert("Order Placement Failed", "Could not place order. Check logs for details.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Order Placement Error", "An unexpected error occurred: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    private void modifyOrderInBackground(String orderId, OrderParams orderParams, String variety) {
        System.out.println("Modifying order: " + orderId);
        new Thread(() -> {
            try {
                Order orderResponse = smartConnect.modifyOrder(orderId, orderParams, variety);
                Platform.runLater(() -> {
                    if (orderResponse != null && orderResponse.orderId != null) {
                        showInfoAlert("Order Modified", "Order ID: " + orderResponse.orderId + " successfully modified.");
                        findOrderTableAndRefresh();
                    } else {
                        showErrorAlert("Order Modification Failed", "Could not modify order. Check logs for details.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Order Modification Error", "An unexpected error occurred: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void findOrderTableAndRefresh() {
        Node currentContent = contentArea.getChildren().isEmpty() ? null : contentArea.getChildren().get(0);
        if (currentContent instanceof BorderPane) {
            Node centerNode = ((BorderPane) currentContent).getCenter();
            if (centerNode instanceof TableView) {
                fetchOrderBook((TableView<Order>) centerNode);
            }
        }
    }

    private JSONArray parseHumanReadableScripSearch(String responseStr) {
        JSONArray scrips = new JSONArray();
        // Regex updated to optionally capture a 'name' field, assuming it's the last part or followed by specific delimiters.
        Pattern pattern = Pattern.compile("\\d+\\.\\s*exchange:\\s*([^,]+),\\s*tradingsymbol:\\s*([^,]+),\\s*symboltoken:\\s*([^,]+)(?:,\\s*name:\\s*([^\\r\\n]+))?");
        String[] lines = responseStr.split("\\r?\\n");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                try {
                    JSONObject scrip = new JSONObject();
                    scrip.put("exchange", matcher.group(1).trim());
                    scrip.put("tradingsymbol", matcher.group(2).trim());
                    scrip.put("symboltoken", matcher.group(3).trim());
                    String name = (matcher.groupCount() >= 4 && matcher.group(4) != null) ? matcher.group(4).trim() : "N/A";
                    scrip.put("name", name);
                    scrips.put(scrip);
                } catch (JSONException e) {
                    System.err.println("Error parsing line in custom scrip parser: " + line + " - " + e.getMessage());
                }
            }
        }
        return scrips;
    }

    private void setupHoldingsTableColumns(TableView<JSONObject> table) {
        TableColumn<JSONObject, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(cellData -> {
            JSONObject rowValue = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(rowValue.optString("tradingsymbol"));
        });

        TableColumn<JSONObject, String> exchangeCol = new TableColumn<>("Exchange");
        exchangeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().optString("exchange")));

        TableColumn<JSONObject, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().optInt("quantity")));

        TableColumn<JSONObject, Double> avgPriceCol = new TableColumn<>("Avg. Price");
        avgPriceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().optDouble("averageprice")));

        TableColumn<JSONObject, Double> ltpCol = new TableColumn<>("LTP");
        ltpCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().optDouble("ltp")));

        TableColumn<JSONObject, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().optString("product")));

        TableColumn<JSONObject, Double> pnlCol = new TableColumn<>("P&L");
        pnlCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().optDouble("profitandloss")));

        TableColumn<JSONObject, String> pnlPercentCol = new TableColumn<>("P&L %");
        pnlPercentCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().optString("pnlpercentage") + "%"));

        table.getColumns().addAll(symbolCol, exchangeCol, quantityCol, avgPriceCol, ltpCol, productCol, pnlCol, pnlPercentCol);
        table.setPlaceholder(new Label("No holdings to display. Click 'Refresh Holdings'."));
    }

    private void fetchHoldingsData(Label totalHoldingValueLabel, Label totalInvestmentValueLabel, Label totalPnlLabel) {
        if (holdingsTable.getPlaceholder() instanceof Label) {
            ((Label) holdingsTable.getPlaceholder()).setText("Fetching holdings...");
        }
        totalHoldingValueLabel.setText("Total Holding Value: Loading...");
        totalInvestmentValueLabel.setText("Total Investment: Loading...");
        totalPnlLabel.setText("Total P&L: Loading...");

        new Thread(() -> {
            try {
                JSONObject holdingsJson = smartConnect.getAllHolding();
                List<JSONObject> holdingItems = new ArrayList<>();
                JSONObject totalHoldingData = null;

                if (holdingsJson != null && holdingsJson.optBoolean("status", false)) {
                    JSONObject data = holdingsJson.optJSONObject("data");
                    if (data != null) {
                        totalHoldingData = data.optJSONObject("totalholding");
                        JSONArray holdingsArray = data.optJSONArray("holdings");
                        if (holdingsArray != null) {
                            for (int i = 0; i < holdingsArray.length(); i++) {
                                holdingItems.add(holdingsArray.getJSONObject(i));
                            }
                        }
                    }
                }
                final JSONObject finalTotalHoldingData = totalHoldingData;
                Platform.runLater(() -> {
                    holdingsTable.getItems().setAll(holdingItems);
                    if (holdingsTable.getPlaceholder() instanceof Label) {
                        ((Label) holdingsTable.getPlaceholder()).setText(holdingItems.isEmpty() ? "No holdings found." : "No holdings to display. Click 'Refresh Holdings'.");
                    }
                    if (finalTotalHoldingData != null) {
                        totalHoldingValueLabel.setText("Total Holding Value: " + finalTotalHoldingData.optString("totalholdingvalue", "N/A"));
                        totalInvestmentValueLabel.setText("Total Investment: " + finalTotalHoldingData.optString("totalinvvalue", "N/A"));
                        totalPnlLabel.setText("Total P&L: " + finalTotalHoldingData.optString("totalprofitandloss", "N/A") + " (" + finalTotalHoldingData.optString("totalpnlpercentage", "N/A") + "%)");
                    }
                });
            } catch (SmartAPIException | IOException | JSONException e) {
                Platform.runLater(() -> {
                    if (holdingsTable.getPlaceholder() instanceof Label) {
                        ((Label) holdingsTable.getPlaceholder()).setText("Error fetching holdings: " + e.getMessage());
                    }
                    showErrorAlert("Holdings Error", "Failed to fetch holdings: " + e.getMessage());
                    e.printStackTrace();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (holdingsTable.getPlaceholder() instanceof Label) {
                        ((Label) holdingsTable.getPlaceholder()).setText("Unexpected error fetching holdings: " + e.getMessage());
                    }
                    showErrorAlert("Holdings Error", "An unexpected error occurred: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private Node createCandleDataTab() {
        BorderPane candleDataLayout = new BorderPane();
        candleDataLayout.setPadding(new Insets(15));

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(8);

        this.candleDataTabExchangeComboBox = new ComboBox<>();
        this.candleDataTabExchangeComboBox.getItems().addAll(Constants.EXCHANGE_NSE, Constants.EXCHANGE_BSE, Constants.EXCHANGE_NFO, Constants.EXCHANGE_MCX, Constants.EXCHANGE_CDS);
        this.candleDataTabExchangeComboBox.setValue(Constants.EXCHANGE_NSE);

        TextField searchKeywordField = new TextField();
        searchKeywordField.setPromptText("Scrip keyword (e.g., SBIN)");
        Button searchScripButton = new Button("Search");
        ListView<JSONObject> searchResultsListView = new ListView<>();
        searchResultsListView.setPrefHeight(80);
        searchResultsListView.setPlaceholder(new Label("Enter keyword and search."));

        this.candleDataTabSymbolTokenField = new TextField();
        this.candleDataTabSymbolTokenField.setPromptText("Symbol Token (e.g., 3045)");
        this.candleDataTabSymbolTokenField.setEditable(false);

        ComboBox<String> intervalComboBox = new ComboBox<>();
        intervalComboBox.getItems().addAll("ONE_MINUTE", "THREE_MINUTE", "FIVE_MINUTE", "TEN_MINUTE", "FIFTEEN_MINUTE", "THIRTY_MINUTE", "ONE_HOUR", "ONE_DAY");
        intervalComboBox.setValue("ONE_MINUTE");

        this.candleDataTabFromDatePicker = new DatePicker(LocalDate.now().minusDays(1));
        this.candleDataTabFromTimeField = new TextField("09:15");
        this.candleDataTabFromTimeField.setPromptText("HH:MM");
        this.candleDataTabFromTimeField.setPrefColumnCount(5);

        this.candleDataTabToDatePicker = new DatePicker(LocalDate.now());
        this.candleDataTabToTimeField = new TextField("15:30");
        this.candleDataTabToTimeField.setPromptText("HH:MM");
        this.candleDataTabToTimeField.setPrefColumnCount(5);

        this.candleDataTabFetchCandleDataButton = new Button("Fetch Candle Data");
        this.candleDataTabLiveFeedCheckBox = new CheckBox("Enable Live Feed");
        Button detachHistoricalChartButton = new Button("Detach Chart");
        historicalDataChartWebView = new WebView();
        // Initialize historicalChartPlaceholder before use
        this.historicalChartPlaceholder = new Label("Chart is detached. Close the detached window to re-embed.");
        historicalDataChartEngine = historicalDataChartWebView.getEngine();
        historicalChartPlaceholder.setStyle("-fx-alignment: center; -fx-font-size: 14px;");
        historicalChartContainer = new StackPane(historicalDataChartWebView);

        try {
            String chartHtmlUrl = getClass().getResource("/plotly_chart.html").toExternalForm();
            historicalDataChartEngine.load(chartHtmlUrl);
            historicalDataChartEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    applyThemeToEngine(historicalDataChartEngine);
                }
            });
        } catch (NullPointerException npe) {
            log.error("Could not find plotly_chart.html.", npe);
            historicalDataChartEngine.loadContent("<html><body><h1>Error: plotly_chart.html not found</h1></body></html>");
        }

        int rowIndex = 0;
        inputGrid.add(new Label("Search Exchange:"), 0, rowIndex);
        inputGrid.add(this.candleDataTabExchangeComboBox, 1, rowIndex++);
        inputGrid.add(new Label("Search Keyword:"), 0, rowIndex);
        inputGrid.add(searchKeywordField, 1, rowIndex);
        inputGrid.add(searchScripButton, 2, rowIndex++);
        inputGrid.add(new Label("Search Results:"), 0, rowIndex);
        inputGrid.add(searchResultsListView, 1, rowIndex++, 2, 1);

        inputGrid.add(new Label("Selected Token:"), 0, rowIndex);
        inputGrid.add(this.candleDataTabSymbolTokenField, 1, rowIndex++);
        inputGrid.add(new Label("Interval:"), 0, rowIndex);
        inputGrid.add(intervalComboBox, 1, rowIndex++);
        inputGrid.add(new Label("From:"), 0, rowIndex);
        HBox fromDateTimeBox = new HBox(5, this.candleDataTabFromDatePicker, new Label("Time:"), this.candleDataTabFromTimeField);
        inputGrid.add(fromDateTimeBox, 1, rowIndex++);
        inputGrid.add(new Label("To:"), 0, rowIndex);
        HBox toDateTimeBox = new HBox(5, this.candleDataTabToDatePicker, new Label("Time:"), this.candleDataTabToTimeField);
        inputGrid.add(toDateTimeBox, 1, rowIndex++);
        inputGrid.add(detachHistoricalChartButton, 2, rowIndex);
        inputGrid.add(this.candleDataTabLiveFeedCheckBox, 0, rowIndex);
        inputGrid.add(this.candleDataTabFetchCandleDataButton, 1, rowIndex);

        candleDataLayout.setTop(inputGrid);
        candleDataLayout.setCenter(historicalChartContainer);

        detachHistoricalChartButton.setOnAction(event -> detachShowHistoricalChart());

        searchScripButton.setOnAction(event -> {
            String keyword = searchKeywordField.getText();
            String exch = this.candleDataTabExchangeComboBox.getValue();
            if (keyword == null || keyword.trim().isEmpty() || exch == null) {
                showErrorAlert("Search Scrip", "Please enter a keyword and select an exchange.");
                return;
            }
            searchResultsListView.setPlaceholder(new Label("Searching..."));
            searchResultsListView.getItems().clear();
            this.candleDataTabSymbolTokenField.clear();

            new Thread(() -> {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("exchange", exch);
                    payload.put("searchscrip", keyword);
                    String responseStr = smartConnect.getSearchScrip(payload);
                    JSONArray resultsArray = parseHumanReadableScripSearch(responseStr);

                    final JSONArray finalResultsArray = resultsArray;
                    Platform.runLater(() -> {
                        if (finalResultsArray == null || finalResultsArray.length() == 0) {
                            searchResultsListView.setPlaceholder(new Label("No scrips found."));
                        } else {
                            for (int i = 0; i < finalResultsArray.length(); i++) {
                                searchResultsListView.getItems().add(finalResultsArray.getJSONObject(i));
                            }
                            searchResultsListView.setPlaceholder(new Label("Enter keyword and search."));
                        }
                    });
                } catch (SmartAPIException | IOException | org.json.JSONException e) {
                    Platform.runLater(() -> {
                        searchResultsListView.setPlaceholder(new Label("Error searching scrips."));
                        showErrorAlert("Search Scrip Error", "Failed to search scrips: " + e.getMessage());
                        e.printStackTrace();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        searchResultsListView.setPlaceholder(new Label("Unexpected error during search."));
                        showErrorAlert("Search Scrip Error", "An unexpected error occurred: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
            }).start();
        });

        searchResultsListView.setCellFactory(lv -> new ListCell<JSONObject>() {
            @Override
            protected void updateItem(JSONObject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.optString("tradingsymbol", "N/A") + " (" + item.optString("name", "N/A") + ") - " + item.optString("exchange"));
            }
        });

        searchResultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedJson) -> {
            if (selectedJson != null) {
                this.candleDataTabSymbolTokenField.setText(selectedJson.optString("symboltoken"));
            }
        });

        this.candleDataTabLiveFeedCheckBox.setOnAction(e -> {
            boolean liveFeedEnabled = this.candleDataTabLiveFeedCheckBox.isSelected();
            this.candleDataTabFromDatePicker.setDisable(liveFeedEnabled);
            this.candleDataTabFromTimeField.setDisable(liveFeedEnabled);
            this.candleDataTabToDatePicker.setDisable(liveFeedEnabled);
            this.candleDataTabToTimeField.setDisable(liveFeedEnabled);
            this.candleDataTabFetchCandleDataButton.setDisable(liveFeedEnabled);

            WebEngine currentEngine = getActiveHistoricalChartEngine();
            if (currentEngine != null) {
                if (liveFeedEnabled) {
                    String symbolToken = this.candleDataTabSymbolTokenField.getText();
                    String exchange = this.candleDataTabExchangeComboBox.getValue();

                    if (symbolToken == null || symbolToken.trim().isEmpty() || exchange == null) {
                        showErrorAlert("Live Feed Error", "Please select a symbol first");
                        this.candleDataTabLiveFeedCheckBox.setSelected(false);
                        this.candleDataTabFromDatePicker.setDisable(false);
                        this.candleDataTabFromTimeField.setDisable(false);
                        this.candleDataTabToDatePicker.setDisable(false);
                        this.candleDataTabToTimeField.setDisable(false);
                        this.candleDataTabFetchCandleDataButton.setDisable(false);
                        return;
                    }

                    currentLiveToken = symbolToken;
                    currentLiveExchange = exchange;

                    Platform.runLater(() -> currentEngine.executeScript("setLiveFeedMode('Connecting to live feed...');"));

                    startLiveFeed();
                } else {
                    stopLiveFeed();
                    Platform.runLater(() -> currentEngine.executeScript("setLiveFeedMode('Historical Data');"));
                }
            }
        });

        this.candleDataTabFetchCandleDataButton.setOnAction(e -> {
            String exchange = this.candleDataTabExchangeComboBox.getValue();
            String symbolToken = this.candleDataTabSymbolTokenField.getText();
            String interval = intervalComboBox.getValue();
            LocalDate fromDate = this.candleDataTabFromDatePicker.getValue();
            String fromTime = this.candleDataTabFromTimeField.getText();
            LocalDate toDate = this.candleDataTabToDatePicker.getValue();
            String toTime = this.candleDataTabToTimeField.getText();

            if (this.candleDataTabLiveFeedCheckBox.isSelected()) {
                showInfoAlert("Live Feed Active", "Live feed is enabled. Disable it to fetch historical data.");
                return;
            }

            if (this.candleDataTabFromDatePicker.getValue() == null || this.candleDataTabToDatePicker.getValue() == null || this.candleDataTabFromTimeField.getText().trim().isEmpty() || this.candleDataTabToTimeField.getText().trim().isEmpty()) {
                showErrorAlert("Invalid Input", "Please select valid dates and enter times.");
                return;
            }

            String fromDateTimeStr = fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + fromTime;
            String toDateTimeStr = toDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + toTime;

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime.parse(fromDateTimeStr, formatter);
                LocalDateTime.parse(toDateTimeStr, formatter);

                if (exchange == null || symbolToken == null || symbolToken.trim().isEmpty()) {
                    showErrorAlert("Invalid Input", "Please fill in all fields.");
                    return;
                }

                new Thread(() -> {
                    WebEngine currentEngine = getActiveHistoricalChartEngine();
                    if (currentEngine != null) {
                        Platform.runLater(() -> {
                            currentEngine.executeScript("setLiveFeedMode('Fetching Data...');");
                        });
                    }
                    try {
                        JSONObject params = new JSONObject();
                        params.put("exchange", exchange);
                        params.put("symboltoken", symbolToken);
                        params.put("interval", interval);
                        params.put("fromdate", fromDateTimeStr);
                        params.put("todate", toDateTimeStr);

                        JSONArray candleDataArray = smartConnect.candleData(params);
                        JSONArray chartData = new JSONArray();
                        List<com.angelbroking.smartapi.models.Candle> parsedCandles = new ArrayList<>();

                        JSONObject plotlyData = new JSONObject();
                        JSONArray times = new JSONArray();
                        JSONArray opens = new JSONArray();
                        JSONArray highs = new JSONArray();
                        JSONArray lows = new JSONArray();
                        JSONArray closes = new JSONArray();

                        if (candleDataArray != null && candleDataArray.length() > 0) {
                            SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

                            for (int i = 0; i < candleDataArray.length(); i++) {
                                JSONArray candle = candleDataArray.getJSONArray(i);
                                try {
                                    com.angelbroking.smartapi.models.Candle apiCandle = new com.angelbroking.smartapi.models.Candle(candle);
                                    parsedCandles.add(apiCandle);

                                    String timestampStr = candle.getString(0);
                                    times.put(timestampStr);
                                    opens.put(apiCandle.getOpen());
                                    highs.put(apiCandle.getHigh());
                                    lows.put(apiCandle.getLow());
                                    closes.put(apiCandle.getClose());
                                } catch (Exception parseEx) {
                                    log.error("Error parsing candle: {}", candle.toString(), parseEx);
                                }
                            }
                        }

                        if (currentEngine != null) {
                            plotlyData.put("x", times);
                            plotlyData.put("open", opens);
                            plotlyData.put("high", highs);
                            plotlyData.put("low", lows);
                            plotlyData.put("close", closes);
                            final String dataForJs = plotlyData.toString();
                            Platform.runLater(() -> currentEngine.executeScript("setHistoricalData(" + dataForJs + ", '" + symbolToken + "', '" + interval + "');"));
                        }

                    } catch (SmartAPIException | IOException | JSONException apiEx) {
                        handleChartError("API Error: " + apiEx.getMessage(), apiEx);
                    } catch (Exception ex) {
                        handleChartError("Unexpected Error: " + ex.getMessage(), ex);
                    }
                }).start();
            } catch (Exception ex) {
                showErrorAlert("Invalid Date Format", "Please check date format");
                return;
            }
        });

        return candleDataLayout;
    }

    private void handleChartError(String message, Throwable ex) {
        WebEngine currentEngine = getActiveHistoricalChartEngine();
        if (currentEngine != null) {
            Platform.runLater(() -> currentEngine.executeScript("setLiveFeedMode('" + message.replace("'", "\\'") + "');"));
        }
        Platform.runLater(() -> {
            showErrorAlert("Historical Data Error", message);
            if (ex != null) ex.printStackTrace();
        });
    }

    private WebEngine getActiveHistoricalChartEngine() {
        if (isHistoricalChartDetached && detachedHistoricalStage != null && detachedHistoricalStage.isShowing() && detachedHistoricalWebEngine != null) {
            return detachedHistoricalWebEngine;
        }
        return historicalDataChartEngine;
    }

    private void detachShowHistoricalChart() { // This method is for historical data tab only
        if (isHistoricalChartDetached && detachedHistoricalStage != null && detachedHistoricalStage.isShowing()) {
            detachedHistoricalStage.toFront();
            return;
        }

        isHistoricalChartDetached = true;
        historicalChartContainer.getChildren().setAll(historicalChartPlaceholder);

        detachedHistoricalStage = new Stage();
        detachedHistoricalStage.setTitle("Detached Historical Chart");
        WebView newWebView = new WebView();
        detachedHistoricalWebEngine = newWebView.getEngine();
        Scene scene = new Scene(newWebView, 800, 600);
        applyCurrentThemeToScene(scene);
        detachedHistoricalStage.setScene(scene);

        detachedHistoricalWebEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                applyThemeToEngine(detachedHistoricalWebEngine);
                detachedHistoricalWebEngine.executeScript("setLiveFeedMode('Chart detached. Fetch data or enable live feed.');");
            }
        });
        detachedHistoricalWebEngine.load(getClass().getResource("/plotly_chart.html").toExternalForm());

        detachedHistoricalStage.setOnCloseRequest(event -> {
            isHistoricalChartDetached = false;
            historicalChartContainer.getChildren().setAll(historicalDataChartWebView);
            detachedHistoricalWebEngine = null;
            detachedHistoricalStage = null;
            if (candleDataTabLiveFeedCheckBox.isSelected()) {
                stopLiveFeed();
                Platform.runLater(() -> historicalDataChartEngine.executeScript("setLiveFeedMode('Chart re-embedded. Toggle live feed if needed.');"));
            }
        });
        detachedHistoricalStage.show();
    }

    private void stopLiveFeed() {
        isLiveChartActive = false;
        // If your smartConnect library has an explicit method to disconnect or unsubscribe from the stream,
        // you would call it here. For example:
        // if (smartConnect != null) {
        //     smartConnect.disconnectStream(); // Or smartConnect.unsubscribeStream(...);
        // }

        log.info("Live feed stopped for token: {} on exchange: {}", currentLiveToken, currentLiveExchange);

        currentLiveToken = null;
        currentLiveExchange = null;

        WebEngine activeEngine = getActiveHistoricalChartEngine();
        if (activeEngine != null) {
            Platform.runLater(() -> {
                activeEngine.executeScript("setLiveFeedMode('Live feed stopped. Historical Data mode.');");
            });
        }
    }

    private void startLiveFeed() {
        WebEngine targetEngine = getActiveHistoricalChartEngine();
        if (targetEngine == null) {
            log.warn("No active chart engine for live feed.");
            disableLiveFeed();
            return;
        }

        try {
            isLiveChartActive = true;
            smartConnect.connectStream(new SmartConnect.OnStreamListener() {
                @Override
                public void onConnected() {
                    Platform.runLater(() -> {
                        if (targetEngine != null) targetEngine.executeScript("setLiveFeedMode('Connected. Subscribing to " + currentLiveToken + "...');");
                    });
                    List<String> tokens = Arrays.asList(currentLiveToken);
                    smartConnect.subscribeStream("LTP", tokens);
                }
                @Override
                public void onDisconnected(int statusCode, String reason) {
                    Platform.runLater(() -> {
                        if (isLiveChartActive && targetEngine != null) {
                            targetEngine.executeScript("setLiveFeedMode('Disconnected: " + reason + "');");
                        }
                    });
                    isLiveChartActive = false;
                }
                @Override
                public void onError(Throwable error) {
                    Platform.runLater(() -> {
                        String message = error != null ? error.getMessage() : "Unknown error";
                        if (targetEngine != null) targetEngine.executeScript("setLiveFeedMode('Error: " + message.replace("'", "\\'") + "');");
                        showErrorAlert("Live Feed Error", message);
                        disableLiveFeed();
                    });
                    isLiveChartActive = false;
                }

                @Override
                public void onData(ByteBuffer data) {
                    if (!isLiveChartActive) return;
                    WebEngine currentDataEngine = getActiveHistoricalChartEngine();
                    if (currentDataEngine == null) return;
                    try {
                        double price = 100.0 + (Math.random() * 10 - 5);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        String time = sdf.format(new Date());
                    } catch (Exception e) {
                        log.error("Error processing live data for chart", e);
                    }
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                if (targetEngine != null) targetEngine.executeScript("setLiveFeedMode('Connection error: " + e.getMessage().replace("'", "\\'") + "');");
                showErrorAlert("Live Feed Error", "Failed to connect: " + e.getMessage());
                disableLiveFeed();
            });
            isLiveChartActive = false;
        }
    }

    private void disableLiveFeed() {
        if (this.candleDataTabLiveFeedCheckBox != null) this.candleDataTabLiveFeedCheckBox.setSelected(false);
        if (this.candleDataTabFromDatePicker != null) this.candleDataTabFromDatePicker.setDisable(false);
        if (this.candleDataTabFromTimeField != null) this.candleDataTabFromTimeField.setDisable(false);
        if (this.candleDataTabToDatePicker != null) this.candleDataTabToDatePicker.setDisable(false);
        if (this.candleDataTabToTimeField != null) this.candleDataTabToTimeField.setDisable(false);
        if (this.candleDataTabFetchCandleDataButton != null) this.candleDataTabFetchCandleDataButton.setDisable(false);
        stopLiveFeed();
    }

    private Node createBacktestingTab() {
        BorderPane backtestingLayout = new BorderPane();
        backtestingLayout.setPadding(new Insets(15));

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        Label strategyLabel = new Label("Strategy:");
        ComboBox<String> strategyComboBox = new ComboBox<>();
        strategyComboBox.getItems().addAll("MA Crossover", "RSI Deviation", "Custom Strategy");
        strategyComboBox.setValue("MA Crossover");

        Label exchangeLabel = new Label("Exchange:");
        ComboBox<String> exchangeComboBox = new ComboBox<>();
        exchangeComboBox.getItems().addAll(Constants.EXCHANGE_NSE, Constants.EXCHANGE_BSE, Constants.EXCHANGE_NFO, Constants.EXCHANGE_MCX);
        exchangeComboBox.setValue(Constants.EXCHANGE_NSE);

        Label symbolTokenLabel = new Label("Symbol Token:");
        TextField symbolTokenField = new TextField();
        symbolTokenField.setPromptText("Enter Symbol Token (e.g., 3045)");

        Label tradingSymbolLabel = new Label("Trading Symbol:");
        TextField tradingSymbolField = new TextField();
        tradingSymbolField.setPromptText("e.g., SBIN-EQ (for report)");

        Label fromDateLabel = new Label("From Date:");
        DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusMonths(3));
        Label toDateLabel = new Label("To Date:");
        DatePicker toDatePicker = new DatePicker(LocalDate.now().minusDays(1));

        Label intervalLabel = new Label("Interval:");
        ComboBox<String> intervalComboBox = new ComboBox<>();
        intervalComboBox.getItems().addAll("ONE_MINUTE", "THREE_MINUTE", "FIVE_MINUTE", "TEN_MINUTE", "FIFTEEN_MINUTE", "THIRTY_MINUTE", "ONE_HOUR", "ONE_DAY");
        intervalComboBox.setValue("FIFTEEN_MINUTE");
        
        Label productTypeLabel = new Label("Product Type:");
        ComboBox<String> productTypeComboBox = new ComboBox<>();
        productTypeComboBox.getItems().addAll(Constants.PRODUCT_INTRADAY, Constants.PRODUCT_DELIVERY, Constants.PRODUCT_MARGIN, Constants.PRODUCT_CARRYFORWARD, Constants.PRODUCT_BO, Constants.PRODUCT_CO, "FNO"); // Added FNO as a common type
        productTypeComboBox.setValue(Constants.PRODUCT_INTRADAY);

        Label shortPeriodLabel = new Label("Short MA Period:");
        TextField shortPeriodField = new TextField("10");
        Label longPeriodLabel = new Label("Long MA Period:");
        TextField longPeriodField = new TextField("30");

        Label rsiPeriodLabel = new Label("RSI Period:"); // This label is fine as it's specific to RSI strategy params
        TextField rsiPeriodField = new TextField("14");
        Label rsiOversoldLabel = new Label("RSI Oversold (<):");
        TextField rsiOversoldField = new TextField("30");
        Label rsiOverboughtLabel = new Label("RSI Overbought (>):");
        TextField rsiOverboughtField = new TextField("70");
        Label quantityLabel = new Label("Quantity per Trade:");
        TextField quantityField = new TextField("1");

        Label initialCapitalLabel = new Label("Initial Capital:");
        TextField initialCapitalField = new TextField("100000");
        Label commissionLabel = new Label("Commission/Trade:");
        TextField commissionField = new TextField("20");

        // Custom Strategy Class Name
        Label customStrategyClassNameLabel = new Label("Custom Strategy Class:");
        TextField customStrategyClassNameField = new TextField();
        customStrategyClassNameField.setPromptText("e.g., MyCustomStrategy (in default package)");
        HBox customStrategyBox = new HBox(10, customStrategyClassNameLabel, customStrategyClassNameField);

        Button runBacktestButton = new Button("Run Backtest");
        Button detachBacktestChartButton = new Button("Detach Chart");
        TextArea resultsTextArea = new TextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.setWrapText(true);
        int row = 0;
        inputGrid.add(strategyLabel, 0, row);
        inputGrid.add(strategyComboBox, 1, row++);
        inputGrid.add(customStrategyBox, 0, row++, 2, 1); // Add custom strategy input here
        inputGrid.add(exchangeComboBox, 1, row++);
        inputGrid.add(symbolTokenLabel, 0, row);
        inputGrid.add(symbolTokenField, 1, row++);
        inputGrid.add(tradingSymbolLabel, 0, row);
        inputGrid.add(tradingSymbolField, 1, row++);
        inputGrid.add(fromDateLabel, 0, row);
        inputGrid.add(fromDatePicker, 1, row++);
        inputGrid.add(toDateLabel, 0, row);
        inputGrid.add(toDatePicker, 1, row++);
        inputGrid.add(intervalLabel, 0, row);
        inputGrid.add(intervalComboBox, 1, row++);
        inputGrid.add(productTypeLabel, 0, row); // Add Product Type input
        inputGrid.add(productTypeComboBox, 1, row++); // Add Product Type input
        inputGrid.add(new Separator(), 0, row++, 2, 1);
        
        // MA Crossover Params
        inputGrid.add(shortPeriodLabel, 0, row);
        inputGrid.add(shortPeriodField, 1, row++);
        inputGrid.add(longPeriodLabel, 0, row);
        inputGrid.add(longPeriodField, 1, row++);

        // RSI Params (initially hidden)
        inputGrid.add(rsiPeriodLabel, 0, row);
        inputGrid.add(rsiPeriodField, 1, row++);
        inputGrid.add(rsiOversoldLabel, 0, row);
        inputGrid.add(rsiOversoldField, 1, row++);
        inputGrid.add(rsiOverboughtLabel, 0, row);
        inputGrid.add(rsiOverboughtField, 1, row++);
        
        inputGrid.add(quantityLabel, 0, row);
        inputGrid.add(quantityField, 1, row++);
        inputGrid.add(new Separator(), 0, row++, 2, 1);
        inputGrid.add(initialCapitalLabel, 0, row);
        inputGrid.add(initialCapitalField, 1, row++);
        inputGrid.add(commissionLabel, 0, row);
        inputGrid.add(commissionField, 1, row++);

        // --- Strategy Parameter Visibility ---
        Runnable toggleStrategyParams = () -> {
            String selectedStrategy = strategyComboBox.getValue();
            boolean isMaStrategy = "MA Crossover".equals(selectedStrategy); // Assuming "MA Crossover" is the exact string
            boolean isRsiStrategy = "RSI Deviation".equals(selectedStrategy);

            shortPeriodLabel.setVisible(isMaStrategy); shortPeriodField.setVisible(isMaStrategy);
            longPeriodLabel.setVisible(isMaStrategy); longPeriodField.setVisible(isMaStrategy);
            shortPeriodLabel.setManaged(isMaStrategy); shortPeriodField.setManaged(isMaStrategy);
            longPeriodLabel.setManaged(isMaStrategy); longPeriodField.setManaged(isMaStrategy);

            rsiPeriodLabel.setVisible(isRsiStrategy); rsiPeriodField.setVisible(isRsiStrategy);
            rsiOversoldLabel.setVisible(isRsiStrategy); rsiOversoldField.setVisible(isRsiStrategy);
            rsiOverboughtLabel.setVisible(isRsiStrategy); rsiOverboughtField.setVisible(isRsiStrategy);
            rsiPeriodLabel.setManaged(isRsiStrategy); rsiPeriodField.setManaged(isRsiStrategy);
            rsiOversoldLabel.setManaged(isRsiStrategy); rsiOversoldField.setManaged(isRsiStrategy);
            rsiOverboughtLabel.setManaged(isRsiStrategy); rsiOverboughtField.setManaged(isRsiStrategy);

            boolean isCustomStrategy = "Custom Strategy".equals(selectedStrategy);
            customStrategyBox.setVisible(isCustomStrategy);
            customStrategyBox.setManaged(isCustomStrategy);
            // Hide MA/RSI params if custom is selected
            if (isCustomStrategy) {
                shortPeriodLabel.setVisible(false); shortPeriodField.setVisible(false); longPeriodLabel.setVisible(false); longPeriodField.setVisible(false);
                rsiPeriodLabel.setVisible(false); rsiPeriodField.setVisible(false); rsiOversoldLabel.setVisible(false); rsiOversoldField.setVisible(false); rsiOverboughtLabel.setVisible(false); rsiOverboughtField.setVisible(false);
            }
        };
        strategyComboBox.setOnAction(event -> toggleStrategyParams.run());
        toggleStrategyParams.run(); // Initial setup

        HBox buttonBox = new HBox(10, runBacktestButton); // Removed detach button
        VBox controlsAndButton = new VBox(15, inputGrid, buttonBox);

        backtestingLayout.setTop(controlsAndButton); // Controls at the top
        backtestingLayout.setCenter(resultsTextArea); // Results text area in the center

        runBacktestButton.setOnAction(e -> {
            resultsTextArea.setText("Running backtest...\n");
            new Thread(() -> {
                try {
                    JSONObject candleParams = new JSONObject();
                    candleParams.put("exchange", exchangeComboBox.getValue());
                    candleParams.put("symboltoken", symbolTokenField.getText());
                    candleParams.put("interval", intervalComboBox.getValue());
                    candleParams.put("fromdate", fromDatePicker.getValue().toString() + " 00:00");
                    candleParams.put("todate", toDatePicker.getValue().toString() + " 23:59");

                    JSONArray candleDataArray = smartConnect.candleData(candleParams);
                    List<Candle> historicalCandles = new ArrayList<>();
                    if (candleDataArray != null) {
                        for (int i = 0; i < candleDataArray.length(); i++) {
                            historicalCandles.add(new Candle(candleDataArray.getJSONArray(i)));
                        }
                    }

                    if (historicalCandles.isEmpty()) {
                        Platform.runLater(() -> resultsTextArea.appendText("No historical data found for the selected parameters.\n"));
                        return;
                    }

                    Strategy strategyToRun;
                    JSONObject strategyRunParams = new JSONObject();
                    strategyRunParams.put("symbolToken", symbolTokenField.getText());
                    strategyRunParams.put("tradingSymbol", tradingSymbolField.getText());
                    strategyRunParams.put("exchange", exchangeComboBox.getValue());
                    strategyRunParams.put("quantity", Integer.parseInt(quantityField.getText()));
                    strategyRunParams.put("productType", productTypeComboBox.getValue()); // Pass selected product type
                    strategyRunParams.put("exchange", exchangeComboBox.getValue()); // Pass selected exchange
                    strategyRunParams.put("interval", intervalComboBox.getValue()); // Add interval to params

                        final String DEFAULT_STRATEGY_PACKAGE = "com.angelbroking.smartapi.algos.strategies.";
                    String selectedStrategyName = strategyComboBox.getValue();
                    if ("MA Crossover".equals(selectedStrategyName)) {
                        strategyToRun = new MACrossoverStrategy();
                        strategyRunParams.put("shortPeriod", Integer.parseInt(shortPeriodField.getText()));
                        strategyRunParams.put("longPeriod", Integer.parseInt(longPeriodField.getText()));
                        strategyRunParams.put("strategyName", "MA Crossover"); // Added for chart plotting
                    } else if ("RSI Deviation".equals(selectedStrategyName) || "Rsipricedeviation".equals(selectedStrategyName)) { // Allow both names
                        strategyToRun = new RsiDeviationStrategy();
                        strategyRunParams.put("rsiPeriod", Integer.parseInt(rsiPeriodField.getText()));
                        strategyRunParams.put("oversoldThreshold", Double.parseDouble(rsiOversoldField.getText()));
                        strategyRunParams.put("overboughtThreshold", Double.parseDouble(rsiOverboughtField.getText()));
                        strategyRunParams.put("strategyName", "RSI Deviation"); // Added for chart plotting
                    } else if ("Custom Strategy".equals(selectedStrategyName)) {
                        String className = customStrategyClassNameField.getText();
                        if (className == null || className.trim().isEmpty()) {
                            Platform.runLater(() -> resultsTextArea.appendText("Custom strategy class name cannot be empty.\n")); // Ensure this is Rsipricedeviation
                            return;
                        }
                        final String fullClassName; // Declare as final
                        // Prepend default package if no package is specified
                        if (!className.contains(".")) {
                            fullClassName = DEFAULT_STRATEGY_PACKAGE + className;
                        } else {
                            fullClassName = className; // Assign once
                        }
                        try {
                            Class<?> clazz = Class.forName(fullClassName);
                            if (Strategy.class.isAssignableFrom(clazz)) {
                                strategyToRun = (Strategy) clazz.getDeclaredConstructor().newInstance();
                                // For custom strategies, we pass the common params.
                                // If they need specific params, they must parse them from the JSONObject
                                // or the GUI needs to be extended to collect arbitrary key-value pairs.
                            } else {
                                throw new IllegalArgumentException("Class " + className + " does not implement com.angelbroking.smartapi.algos.Strategy");
                            }
                        } catch (Exception ex) {
                            Platform.runLater(() -> resultsTextArea.appendText("Error loading custom strategy '" + fullClassName + "': " + ex.getMessage() + "\n"));
                            log.error("Error loading custom strategy", ex);
                            return;
                        }
                    } else {
                        throw new IllegalArgumentException("Unknown strategy: " + selectedStrategyName);
                    }

                    JSONObject backtesterParams = new JSONObject();
                    backtesterParams.put("initialCapital", Double.parseDouble(initialCapitalField.getText()));
                    backtesterParams.put("commissionPerTrade", Double.parseDouble(commissionField.getText()));
                    backtesterParams.put("sharesPerTrade", Integer.parseInt(quantityField.getText())); // sharesPerTrade is still used by backtester

                    Backtester backtester = new Backtester(strategyToRun, historicalCandles, backtesterParams);
                    BacktestReport report = backtester.run(strategyRunParams);
                    report.setHistoricalCandles(historicalCandles); // Ensure candles are in the report for plotting

                    Platform.runLater(() -> {
                        displayBacktestReportText(resultsTextArea, report);
                        // Only attempt to plot indicators if it's not a custom strategy,
                        // or if the custom strategy somehow provides indicator data in a known format.
                        // For now, custom strategy indicators are not automatically plotted.
                        JSONObject paramsForChartIndicators = null;
                        if (!"Custom Strategy".equals(selectedStrategyName)) {
                            showBacktestChartInNewWindow(report, strategyRunParams); // Always show in new window
                        }
                    });

                } catch (SmartAPIException | IOException | JSONException ex) {
                    Platform.runLater(() -> resultsTextArea.appendText("Error during backtest (API/IO/JSON): " + ex.getMessage() + "\n"));
                    log.error("Backtest API/IO/JSON error", ex);
                } catch (Exception ex) {
                    Platform.runLater(() -> resultsTextArea.appendText("Error during backtest (General): " + ex.getMessage() + "\n"));
                    log.error("General backtest error", ex);
                }
            }).start();
        }); // End of runBacktestButton.setOnAction
        return backtestingLayout;
    }

    // Method to show the backtest chart in a new window
    private void showBacktestChartInNewWindow(BacktestReport reportToPlot, JSONObject strategyParams) {
        // If a detached window already exists and is showing, bring it to front and update it
        if (detachedBacktestChartStage != null && detachedBacktestChartStage.isShowing()) {
            detachedBacktestChartStage.toFront();
            if (reportToPlot != null && detachedBacktestChartEngine != null) {
                sendBacktestDataToChart(reportToPlot, detachedBacktestChartEngine, strategyParams);
            } // else: If no report is provided, just bring the existing window to front
            return;
        }

        // Create a new stage and scene for the detached window
        detachedBacktestChartStage = new Stage();
        detachedBacktestChartStage.setTitle("Backtest Chart"); // Set a clear title
        WebView newWebView = new WebView(); // Create a new WebView for the new window
        detachedBacktestChartEngine = newWebView.getEngine(); // Get the engine for the new WebView
        // Set a larger initial size for the new window
        Scene scene = new Scene(newWebView, 1000, 700); // Increased size
        applyCurrentThemeToScene(scene);
        detachedBacktestChartStage.setScene(scene);

        detachedBacktestChartEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                applyThemeToEngine(detachedBacktestChartEngine);
                if (reportToPlot != null) {
                    // Send the backtest data to the newly loaded chart
                    sendBacktestDataToChart(reportToPlot, detachedBacktestChartEngine, strategyParams);
                }
            }
        });
        detachedBacktestChartEngine.load(getClass().getResource("/plotly_chart.html").toExternalForm());
        // Clean up references when the detached window is closed
        detachedBacktestChartStage.setOnCloseRequest(event -> {
            detachedBacktestChartEngine = null; // Clear the engine reference
            detachedBacktestChartStage = null; // Clear the stage reference
        });
        detachedBacktestChartStage.show();
    }

    private void displayBacktestReportText(TextArea resultsTextArea, BacktestReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Backtest Report:\n");
        sb.append("------------------------------------\n");
        sb.append(String.format("Initial Capital: %.2f\n", report.getInitialCapital()));
        sb.append(String.format("Final Capital:   %.2f\n", report.getFinalCapital()));
        sb.append(String.format("Net Profit:      %.2f\n", report.getNetProfit()));
        sb.append(String.format("Profit %%:        %.2f%%\n", report.getProfitPercentage()));
        sb.append(String.format("Total Trades:    %d\n", report.getTotalTrades()));
        sb.append("------------------------------------\n");
        sb.append("Trade Log:\n");
        if (report.getTrades() == null || report.getTrades().isEmpty()) {
            sb.append("No trades executed.\n");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Use the format from TradeLog toString
            for (TradeLog trade : report.getTrades()) {
                sb.append(String.format("%s - %s - Qty: %d @ Price: %.2f\n",
                        sdf.format(trade.getTimestamp()), trade.getAction(), trade.getQuantity(), trade.getPrice()));
            }
        }
        sb.append("------------------------------------\n");
        resultsTextArea.setText(sb.toString());
    }

    private void sendBacktestDataToChart(BacktestReport report, WebEngine targetWebEngine, JSONObject strategyParams) {
        if (targetWebEngine == null) {
            log.warn("Target WebEngine is null in sendBacktestDataToChart. Cannot send data to chart.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String equityTimestampsJson = report.getEquityCurveTimestamps().stream()
                .map(sdf::format)
                .collect(Collectors.joining("\",\"", "[\"", "\"]"));
        String equityValuesJson = report.getEquityCurveValues().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // 1. Prepare Candlestick Data
        JSONObject candleDataJson = new JSONObject();
        JSONArray candleTimes = new JSONArray();
        JSONArray candleOpens = new JSONArray();
        JSONArray candleHighs = new JSONArray();
        JSONArray candleLows = new JSONArray();
        JSONArray candleCloses = new JSONArray();

        List<Candle> historicalCandles = report.getHistoricalCandles();
        if (historicalCandles != null) {
            for (Candle c : historicalCandles) {
                candleTimes.put(sdf.format(c.getTimestamp()));
                candleOpens.put(c.getOpen());
                candleHighs.put(c.getHigh());
                candleLows.put(c.getLow());
                candleCloses.put(c.getClose());
            }
        }
        candleDataJson.put("x", candleTimes);
        candleDataJson.put("open", candleOpens);
        candleDataJson.put("high", candleHighs);
        candleDataJson.put("low", candleLows);
        candleDataJson.put("close", candleCloses);

        // 2. Prepare Indicator Data
        JSONArray indicatorDataJsonArray = new JSONArray();
        if (historicalCandles != null && !historicalCandles.isEmpty() && strategyParams != null) {
            if (strategyParams.has("shortPeriod") && strategyParams.has("longPeriod") && strategyParams.optString("strategyName", "").equals("MA Crossover")) { // MA Crossover
                int shortP = strategyParams.getInt("shortPeriod");
                int longP = strategyParams.getInt("longPeriod");
                if (shortP > 0 && shortP < historicalCandles.size()) {
                    indicatorDataJsonArray.put(calculateSmaJson(historicalCandles, shortP, "SMA " + shortP, "#007bff", sdf));
                }
                if (longP > 0 && longP < historicalCandles.size() && longP != shortP) {
                    indicatorDataJsonArray.put(calculateSmaJson(historicalCandles, longP, "SMA " + longP, "#ffa500", sdf));
                }
            } else if (strategyParams.has("rsiPeriod") && strategyParams.optString("strategyName", "").equals("RSI Deviation")) { // RSI Deviation
                int rsiP = strategyParams.getInt("rsiPeriod");
                if (rsiP > 0 && rsiP < historicalCandles.size()) {
                    indicatorDataJsonArray.put(calculateRsiJson(historicalCandles, rsiP, "RSI " + rsiP, "#9C27B0", sdf));

                    // Add RSI Threshold lines
                    if (strategyParams.has("oversoldThreshold") && strategyParams.has("overboughtThreshold") && !historicalCandles.isEmpty()) {
                        double oversold = strategyParams.getDouble("oversoldThreshold");
                        double overbought = strategyParams.getDouble("overboughtThreshold");
                        // Use the full range of candle timestamps for the threshold lines
                        String firstCandleTimestamp = sdf.format(historicalCandles.get(0).getTimestamp());
                        String lastCandleTimestamp = sdf.format(historicalCandles.get(historicalCandles.size() - 1).getTimestamp());

                        // Oversold Line
                        JSONObject oversoldLine = new JSONObject();
                        oversoldLine.put("name", "RSI Oversold (" + String.format("%.1f", oversold) + ")");
                        JSONArray xOversold = new JSONArray().put(firstCandleTimestamp).put(lastCandleTimestamp);
                        oversoldLine.put("x", xOversold);
                        JSONArray yOversold = new JSONArray().put(oversold).put(oversold);
                        oversoldLine.put("y", yOversold);
                        oversoldLine.put("color", "gray");
                        oversoldLine.put("dash", "dash");
                        oversoldLine.put("yaxis", "y3");
                        indicatorDataJsonArray.put(oversoldLine);

                        // Overbought Line
                        JSONObject overboughtLine = new JSONObject();
                        overboughtLine.put("name", "RSI Overbought (" + String.format("%.1f", overbought) + ")");
                        JSONArray xOverbought = new JSONArray().put(firstCandleTimestamp).put(lastCandleTimestamp);
                        overboughtLine.put("x", xOverbought);
                        JSONArray yOverbought = new JSONArray().put(overbought).put(overbought);
                        overboughtLine.put("y", yOverbought);
                        overboughtLine.put("color", "gray");
                        overboughtLine.put("dash", "dash");
                        overboughtLine.put("yaxis", "y3");
                        indicatorDataJsonArray.put(overboughtLine);
                    }
                }
            }
        }

        // 3. Prepare Trade Data
        JSONArray tradesJsonArray = new JSONArray();
        List<TradeLog> trades = report.getTrades();
        if (trades != null) {
            for (TradeLog trade : trades) {
                JSONObject tradeJson = new JSONObject();
                tradeJson.put("time", sdf.format(trade.getTimestamp()));
                tradeJson.put("action", trade.getAction()); // "Buy" or "Sell"
                log.debug("Adding trade to chart data: Time: {}, Action: {}, Price: {}", sdf.format(trade.getTimestamp()), trade.getAction(), trade.getPrice()); // Added log
                tradeJson.put("price", trade.getPrice());
                tradesJsonArray.put(tradeJson);
            }
        }

        // 4. Prepare Equity Curve Data
        JSONObject equityDataJson = new JSONObject();
        JSONArray equityTimes = new JSONArray();
        JSONArray equityValues = new JSONArray();
        if (report.getEquityCurveTimestamps() != null && report.getEquityCurveValues() != null) {
            for (Date timestamp : report.getEquityCurveTimestamps()) {
                equityTimes.put(sdf.format(timestamp));
            }
            for (Double value : report.getEquityCurveValues()) {
                equityValues.put(value);
            }
        }
        equityDataJson.put("x", equityTimes);
        equityDataJson.put("y", equityValues);

        // 5. Call JavaScript
        try {
            String script = String.format("plotBacktestData(%s, %s, %s, %s);",
                    candleDataJson.toString(),
                    indicatorDataJsonArray.toString(),
                    tradesJsonArray.toString(),
                    equityDataJson.toString());
            log.debug("Executing JS for backtest plot: {}", script);
            targetWebEngine.executeScript(script);

        } catch (Exception e) {
            log.error("Error sending comprehensive backtest data to chart", e);
            showErrorAlert("Chart Error", "Could not display full backtest report on chart: " + e.getMessage());
        }
    }

    private JSONObject calculateSmaJson(List<Candle> candles, int period, String name, String color, SimpleDateFormat sdf) {
        JSONObject smaJson = new JSONObject();
        JSONArray smaTimes = new JSONArray();
        JSONArray smaValues = new JSONArray();

        if (candles.size() < period) {
            smaJson.put("name", name);
            smaJson.put("x", smaTimes);
            smaJson.put("y", smaValues);
            smaJson.put("color", color);
            return smaJson;
        }

        double[] closePrices = candles.stream().mapToDouble(Candle::getClose).toArray();
        double[] smaValuesArray = calculateSMA(closePrices, period);

        for (int i = period - 1; i < candles.size(); i++) {
            smaTimes.put(sdf.format(candles.get(i).getTimestamp()));
            smaValues.put(smaValuesArray[i - (period - 1)]);
        }

        smaJson.put("name", name);
        smaJson.put("x", smaTimes);
        smaJson.put("y", smaValues);
        smaJson.put("color", color); // Optional: pass color for the line
        smaJson.put("yaxis", "y"); // Assign to main price y-axis
        return smaJson;
    }

    private double[] calculateSMA(double[] closePrices, int period) {
        Core taLib = new Core();
        double[] out = new double[closePrices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        taLib.sma(0, closePrices.length - 1, closePrices, period, begin, length, out);

        // Trim the output to match the valid SMA values
        return Arrays.copyOfRange(out, begin.value, begin.value + length.value);
    }

    private JSONObject calculateRsiJson(List<Candle> candles, int period, String name, String color, SimpleDateFormat sdf) {
        JSONObject rsiJson = new JSONObject();
        JSONArray rsiTimes = new JSONArray();
        JSONArray rsiValuesJson = new JSONArray();

        if (candles.size() < period) {
            rsiJson.put("name", name);
            rsiJson.put("x", rsiTimes);
            rsiJson.put("y", rsiValuesJson);
            rsiJson.put("color", color);
            return rsiJson;
        }

        double[] closePrices = candles.stream().mapToDouble(Candle::getClose).toArray();
        Core taLib = new Core();
        double[] rsiOutput = new double[closePrices.length];
        MInteger outBegin = new MInteger();
        MInteger outLength = new MInteger();

        RetCode retCode = taLib.rsi(0, closePrices.length - 1, closePrices, period, outBegin, outLength, rsiOutput);

        if (retCode == RetCode.Success && outLength.value > 0) {
            for (int i = 0; i < outLength.value; i++) {
                rsiTimes.put(sdf.format(candles.get(outBegin.value + i).getTimestamp()));
                rsiValuesJson.put(rsiOutput[i]);
            }
        }
        rsiJson.put("name", name);
        rsiJson.put("x", rsiTimes);
        rsiJson.put("y", rsiValuesJson);
        rsiJson.put("color", color);
        rsiJson.put("yaxis", "y3"); // Assign to RSI specific y-axis
        return rsiJson;
    }

    private void applyThemeToEngine(WebEngine engine) {
        if (engine == null) return;
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                if (primaryStage.getScene().getStylesheets().contains(darkModeCss)) {
                    engine.executeScript("applyTheme('dark');");
                } else {
                    engine.executeScript("applyTheme('light');");
                }
            }
        });
        // If already loaded, apply immediately
        if (engine.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
            if (primaryStage.getScene().getStylesheets().contains(darkModeCss)) {
                engine.executeScript("applyTheme('dark');");
            } else {
                engine.executeScript("applyTheme('light');");
            }
        }
    }

    private void applyCurrentThemeToScene(Scene scene) {
        if (primaryStage.getScene().getStylesheets().contains(darkModeCss)) {
            applyStylesheet(scene, darkModeCss);
        } else {
            applyStylesheet(scene, lightModeCss);
        }
    }
}