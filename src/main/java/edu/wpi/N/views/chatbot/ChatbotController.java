package edu.wpi.N.views.chatbot;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.chatbot.Dialogflow;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import edu.wpi.N.views.mapDisplay.NewMapDisplayController;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatbotController implements Controller, Initializable {
  private StateSingleton state;
  private App mainApp;
  private NewMapDisplayController mapController;

  // Nodes
  public DbNode startNode;
  public DbNode endNode;

  @FXML private Pane mainPane;
  @FXML private JFXTextField textField;
  @FXML private VBox chatBox;
  @FXML private AnchorPane chatBotView;
  @FXML private AnchorPane buttonOnlyView;
  @FXML private ScrollPane scrollPane;
  @FXML private JFXButton btnSendMessage;

  // Inject state singleton
  public ChatbotController(StateSingleton state, App mainApp) {
    this.state = state;
    this.mainApp = mainApp;
  }

  /** Opens up the Chat-bot window */
  @FXML
  private void onBtnAskMeClicked() {

    // If message history is empty
    if (state.chatBotState.getMessageHistory().isEmpty()) {
      try {
        state.chatBotState.initSession();
      } catch (Exception e) {
        e.printStackTrace();
        displayErrorMessage("Error initializing Client Session");
      }
    } else {
      // else, load existing message history
      loadMessageHistory();
    }

    buttonOnlyView.setVisible(false);
    chatBotView.setVisible(true);
    chatBotView.setMouseTransparent(false);
  }

  /** When user closes chat-dialog, close the Dialogflow Client Session and clear message history */
  @FXML
  private void onBtnCloseDialogClicked() throws IOException {
    state.chatBotState.closeSession();
    chatBotView.setVisible(false);
    buttonOnlyView.setVisible(true);
    chatBotView.setMouseTransparent(true);
    chatBox.getChildren().clear();
  }

  /**
   * Loads previous message history into chatBox (Vbox) Loads in order. Chat-bot messages appear on
   * the left side, User messages on the right side
   */
  private void loadMessageHistory() {
    for (VBox singleMessage : state.chatBotState.getMessageHistory()) {
      chatBox.getChildren().add(singleMessage);
    }
  }

  /**
   * Upon click, sends the user's message to chat-bot Displays user's message and chat-bot reply in
   * Chatbox
   */
  @FXML
  private void onBtnSendMessageClicked() {
    if (!textField.getText().equals(null) && !textField.getText().equals("")) {

      // Display user's message first (right side)
      Label userInput = new Label(textField.getText());
      LinkedList<Node> messageContainer = new LinkedList<Node>();
      messageContainer.add(userInput);
      displayAndSaveMessages(messageContainer, true);

      // Display chat-bots reply (left side)
      try {
        // get chatbot's reply, display it and save in message history
        displayAndSaveBotMessage(userInput.getText(), state.chatBotState.prevQueryResult);
      } catch (Exception e) {
        e.printStackTrace();
        displayErrorMessage("Error trying to load chat-bot's reply");
      }
    }
  }

  /**
   * Displays chat-bot's reply-message/messages and saves to chat-history
   *
   * @param userMessage: user's Input
   */
  private void displayAndSaveBotMessage(String userMessage, QueryResult prevQueryResult) {
    try {

      QueryResult queryResults;

      if (prevQueryResult != null) {
        queryResults = prevQueryResult;
      } else {
        queryResults = state.chatBotState.dialogflow.detectIntentTexts(userMessage, "en-US");
      }

      LinkedList<Node> singleMessageObject = new LinkedList<Node>();
      String intent = queryResults.getIntent().getDisplayName();

      if (intent.equals("question-billing-how-to-pay-online-option")) {

        // add elements to a 'Single Message'
        singleMessageObject.addAll(getAnswerToPayingOnlineOption());
      }
      // If intent matches with get-weather
      else if (intent.equals("get-weather")) {
        Label message = new Label(Dialogflow.getCurrentWeatherReply());
        singleMessageObject.add(message);
      } else if (intent.equals("kiosk-help-get-directions-from-to")
          || intent.equals("questions-kiosk-location-search-from-to")) {
        // Get Parameters
        String startLocation =
            queryResults.getParameters().getFieldsMap().get("hospital-location1").getStringValue();
        String goalLocation =
            queryResults.getParameters().getFieldsMap().get("hospital-location2").getStringValue();

        System.out.println(startLocation);
        System.out.println(goalLocation);

        // Check if current scene is Map
        if (!state.isMapDisplayActive) {
          state.chatBotState.prevQueryResult = queryResults;
          LinkedList<DbNode> nodes1 = FuzzySearchAlgorithm.suggestLocations(startLocation);
          LinkedList<DbNode> nodes2 = FuzzySearchAlgorithm.suggestLocations(goalLocation);
          if (nodes1.size() == 0) {
            singleMessageObject.add(
                new Label("Sorry! I wasn't able to find " + startLocation + "!"));
            return;
          }
          if (nodes2.size() == 0) {
            singleMessageObject.add(
                new Label("Sorry! I wasn't able to find " + goalLocation + "!"));
            return;
          }
          state.chatBotState.startNodePrevSession = nodes1.getFirst();
          state.chatBotState.endNodePrevSession = nodes2.getFirst();
          Label reply = new Label(queryResults.getFulfillmentText());
          singleMessageObject.add(reply);
          displayAndSaveMessages(singleMessageObject, false);
          mainApp.switchScene("views/mapDisplay/newMapDisplay.fxml", state);
          return;
        }

        // Enter respective location in Map Editor Controller.MapLocationSearchController
        // Select the 'best' suggested location from drop-down
        mapController.resetMap();
        mapController.setToLocationSearch();
        mapController.locationSearchController.searchStartOrEndLocationForBot(startLocation, true);
        mapController.locationSearchController.searchStartOrEndLocationForBot(goalLocation, false);

        startNode = mapController.locationSearchController.getDBNodes()[0];
        endNode = mapController.locationSearchController.getDBNodes()[1];

        // Display the fulfilment text
        Label reply = new Label(queryResults.getFulfillmentText());
        singleMessageObject.add(reply);

      } else if (intent.equals("questions-kiosk-location-search-where-is")) {
        String location =
            queryResults.getParameters().getFieldsMap().get("hospital-location1").getStringValue();
        LinkedList<DbNode> locations = FuzzySearchAlgorithm.suggestLocations(location);
        if (locations.size() == 0) {
          Label reply = new Label("Sorry! I wasn't able to find " + location + "!");
          singleMessageObject.add(reply);
          return;
        }

        // Check if current scene is Map
        if (!state.isMapDisplayActive) {
          state.chatBotState.prevQueryResult = queryResults;
          LinkedList<DbNode> nodes1 = FuzzySearchAlgorithm.suggestLocations(location);
          if (nodes1.size() == 0) {
            singleMessageObject.add(new Label("Sorry! I wasn't able to find " + location + "!"));
            return;
          }

          state.chatBotState.whereIsNode = nodes1.getFirst();
          Label reply = new Label(queryResults.getFulfillmentText());
          singleMessageObject.add(reply);
          displayAndSaveMessages(singleMessageObject, false);
          mainApp.switchScene("views/mapDisplay/newMapDisplay.fxml", state);
          return;
        }

        mapController.resetMap();
        DbNode node = locations.getFirst();
        mapController.nodeFromDirectory(node);
        Label reply = new Label(queryResults.getFulfillmentText());
        singleMessageObject.add(reply);
      } else if (intent.equals("kiosk-help-get-directions-from-to-wrong-locations")
          || intent.equals("questions-kiosk-location-search-from-to - no")
          || intent.equals("questions-kiosk-location-search-where-is-goal-location - no")) {

        // Reset everything
        if (mapController != null) {
          mapController.resetMap(); // if it's null, than we're in a different scene
        }

        // Display the fulfilment text
        Label reply = new Label(queryResults.getFulfillmentText());
        singleMessageObject.add(reply);

        // Display animation how to use 'way-finding feature'
        mapController.displayGuideForSearchLocation();

      } else {
        // else, use Dialogflow text
        Label message = new Label(queryResults.getFulfillmentText());
        singleMessageObject.add(message);
      }

      state.chatBotState.prevQueryResult = null; // Set to null

      displayAndSaveMessages(singleMessageObject, false);

    } catch (Exception ex) {
      ex.printStackTrace();
      displayErrorMessage("Ooops... Something went wong when loading chat-bot message");
    }
  }

  /**
   * Create a single message consisting of 'Nodes' in the order, which is needed
   *
   * @return list of Nodes, which represent a message. Every node is a separate chat-bot's message
   */
  private LinkedList<Node> getAnswerToPayingOnlineOption() {
    LinkedList<Node> ans = new LinkedList<Node>();

    Label messagePartA =
        new Label(
            "Great! It is the most convenient, efficient way to pay your bill. If you have a Partners Patient Gateway account, you can see the current status of all open patient balances; payments are immediately posted to your account.\n"
                + "\n"
                + "You can opt to go paperless and receive your bills online. In addition, you can now set up monthly payment plans.\n"
                + "\n");
    Hyperlink loginLink = new Hyperlink("Log in to Partners Patient Gateway");
    loginLink.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            try {
              loadWebView("https://mychart.partners.org/mychart-prd/Authentication/Login?");
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });

    Hyperlink guestLink = new Hyperlink("Or you can quickly pay as a guest");
    guestLink.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            try {
              loadWebView("https://mychart.partners.org/mychart-prd/billing/guestpay/payasguest");
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });

    ans.add(messagePartA);
    ans.add(loginLink);
    ans.add(guestLink);

    return ans;
  }

  /**
   * Displays bot's reply(s) from a LinkedList of objects such as Labels/Hyperlinks
   *
   * @param messages: list of objects to display Output from
   * @param isUserMessage: boolean indicating whether the given message is user's message, true -
   *     yes
   */
  private void displayAndSaveMessages(LinkedList<Node> messages, boolean isUserMessage) {
    // Common settings for both User's and Chatbot's messages
    VBox singleMessage = new VBox(5);
    singleMessage.setPadding(new Insets(5));
    singleMessage.setFillWidth(false);

    for (Node n : messages) {
      singleMessage.getChildren().add(n);
    }

    if (isUserMessage) {
      singleMessage.getStylesheets().add(Main.class.getResource("css/UserMessage.css").toString());
      singleMessage.setAlignment(Pos.CENTER_RIGHT);
      textField.clear();
    } else {
      singleMessage.getStylesheets().add(Main.class.getResource("css/BotReply.css").toString());
    }

    // Update the chatBox (VBOX)
    chatBox.getChildren().add(singleMessage);

    // Save changes in Message history
    this.state.chatBotState.addMessageToHistory(singleMessage);
  }

  /**
   * Creates a new stage and Loads given link in a web-view
   *
   * @param url
   */
  private void loadWebView(String url) throws IOException {
    Stage browserStage = new Stage();
    browserStage.setTitle("Browser Window");

    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("browserWindow.fxml"));
    loader.setControllerFactory(
        type -> {
          try {
            return new BrowserController(url);
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
          }
        });

    Pane pane = loader.load();
    Scene scene = new Scene(pane);

    browserStage.setScene(scene);
    browserStage.setFullScreen(true);
    browserStage.show();
  }

  /**
   * Displays allert message
   *
   * @param str: message of the alert
   */
  private void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Something went wong...");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  /** Checks if previous action was not finished because of switching to a new screen */
  public void performPrevioslyPlannedAction() {
    // Check if previously planned action needs to be finished
    if (state.chatBotState.prevQueryResult != null) {
      displayAndSaveBotMessage(null, state.chatBotState.prevQueryResult);
    }
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void setMapController(NewMapDisplayController mapController) {
    this.mapController = mapController;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // on enter pressed, send message automatically
    textField.addEventHandler(
        KeyEvent.KEY_RELEASED,
        keyEvent -> {
          if (keyEvent.getCode() == KeyCode.ENTER) {
            onBtnSendMessageClicked(); // send message
            keyEvent.consume();
          }
        });

    chatBotView.setMouseTransparent(true);

    // Do such that scroll pane auto-scrolls down
    scrollPane.vvalueProperty().bind(chatBox.heightProperty());

    // If message history still persists, open the dialog window right away
    if (!state.chatBotState.getMessageHistory().isEmpty()) onBtnAskMeClicked();
  }

  public LinkedList<DbNode> getNodes() {
    if (startNode != null && endNode != null) {
      LinkedList<DbNode> nodes = new LinkedList<DbNode>();
      nodes.add(startNode);
      nodes.add(endNode);
      return nodes;
    } else {
      return new LinkedList<DbNode>();
    }
  }
}
