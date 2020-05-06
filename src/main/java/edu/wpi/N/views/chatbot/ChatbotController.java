package edu.wpi.N.views.chatbot;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatbotController implements Controller {
  private StateSingleton state;
  private App mainApp;

  @FXML private TextField textField;
  @FXML private VBox chatBox;
  @FXML private AnchorPane chatBotView;
  @FXML private AnchorPane buttonOnlyView;

  // Inject state singleton
  public ChatbotController(StateSingleton state) {
    this.state = state;
  }

  /** Opens up the Chat-bot window */
  @FXML
  private void onBtnAskMeClicked() {

    // If message history is empty
    if (state.chatBotState.getMessageHistory() == null) {
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
    this.chatBox = singleton.chatBotState.getMessageHistory();
  }

  // TODO: add styleshits to labels
  /**
   * Upon click, sends the user's message to chat-bot Displays user's message and chat-bot reply in
   * Chatbox
   */
  @FXML
  private void onBtnSendMessageClicked() {
    if (!textField.getText().equals(null) || !textField.getText().equals("")) {

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
    singleMessage.setPadding(new Insets(20));

    if (isUserMessage) {
      messageAsLabel.setStyle("-fx-background-color: grey;");
      singleMessage.setAlignment(Pos.CENTER_RIGHT);
    } else {
      messageAsLabel.setStyle("-fx-background-color: green;");
      singleMessage.setAlignment(Pos.CENTER_LEFT);
    }

    // Update the chatBox (VBOX)
    chatBox.getChildren().add(singleMessage);
    // Save changes in Message history
    this.state.chatBotState.setMessageHistory(chatBox);
  }

  // TODO: clear it up. Has redundant code with MapDisplayController
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
}
