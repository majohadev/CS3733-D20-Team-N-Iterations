package edu.wpi.N.views.chatbot;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChatbotController implements Controller, Initializable {
  private StateSingleton state;
  private App mainApp;

  @FXML private Pane mainPane;
  @FXML private JFXTextField textField;
  @FXML private VBox chatBox;
  @FXML private AnchorPane chatBotView;
  @FXML private AnchorPane buttonOnlyView;
  @FXML private ScrollPane scrollPane;
  @FXML private JFXButton btnSendMessage;

  // Inject state singleton
  public ChatbotController(StateSingleton state) {
    this.state = state;
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
  }

  /** When user closes chat-dialog, close the Dialogflow Client Session and clear message history */
  @FXML
  private void onBtnCloseDialogClicked() throws IOException {
    state.chatBotState.closeSession();
    chatBotView.setVisible(false);
    buttonOnlyView.setVisible(true);
    chatBox.getChildren().clear();
  }

  /**
   * Loads previous message history into chatBox (Vbox) Loads in order. Chat-bot messages appear on
   * the left side, User messages on the right side
   */
  private void loadMessageHistory() {
    for (HBox singleMessage : state.chatBotState.getMessageHistory()) {
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
      String userInput = textField.getText();
      displayAndSaveMessage(userInput, true);

      // Display chat-bots reply (left side)
      try {
        // get chatbot's reply
        String chatBotReply = state.chatBotState.dialogflow.replyToUserInput(userInput);
        displayAndSaveMessage(chatBotReply, false);
      } catch (Exception e) {
        e.printStackTrace();
        displayErrorMessage("Error trying to load chat-bot's reply");
      }
    }
  }

  /**
   * Displays User's message and saves it to chat history
   *
   * @param message: Message from either chat-bot or user
   * @param isUserMessage: true if given message from User, false - from chat-bot
   */
  private void displayAndSaveMessage(String message, boolean isUserMessage) {
    // Common settings for both User's and Chatbot's messages
    Label messageAsLabel = new Label(message);
    messageAsLabel.setMaxWidth(300);
    messageAsLabel.setWrapText(true);

    HBox singleMessage = new HBox(messageAsLabel);
    singleMessage.setPadding(new Insets(5));

    if (isUserMessage) {
      messageAsLabel
          .getStylesheets()
          .add(Main.class.getResource("css/labelUserMessage.css").toString());
      singleMessage.setAlignment(Pos.CENTER_RIGHT);
    } else {
      messageAsLabel
          .getStylesheets()
          .add(Main.class.getResource("css/labelBotReply.css").toString());
      singleMessage.setAlignment(Pos.CENTER_LEFT);
    }

    // Update the chatBox (VBOX)
    chatBox.getChildren().add(singleMessage);
    // Save changes in Message history
    this.state.chatBotState.addMessageToHistory(singleMessage);

    // Clear text field input
    textField.clear();
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

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
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

    // Do such that scroll pane auto-scrolls down
    scrollPane.vvalueProperty().bind(chatBox.heightProperty());

    // If message history still persists, open the dialog window right away
    if (!state.chatBotState.getMessageHistory().isEmpty()) onBtnAskMeClicked();
  }
}
