package edu.wpi.N.entities.States;

import edu.wpi.N.chatbot.Dialogflow;
import java.io.IOException;
import javafx.scene.layout.VBox;

public class ChatMessagesState {
  private VBox messageHistory;
  public Dialogflow dialogflow;

  public ChatMessagesState() {
    messageHistory = null;
    try {
      dialogflow = new Dialogflow();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public VBox getMessageHistory() {
    return messageHistory;
  }

  public void setMessageHistory(VBox messageHistory) {
    this.messageHistory = messageHistory;
  }

  /** Delete previous chat history. Called when a User session is closed */
  public void eraseChatHistory() {
    messageHistory = null;
  }

  /**
   * Initializes Session with the end user
   *
   * @throws IOException
   */
  public void initSession() throws IOException {
    dialogflow.initializeSession();
  }

  /**
   * Closes the session with the end usr
   *
   * @throws IOException
   */
  public void closeSession() throws IOException {
    dialogflow.closeSession();
    // Reset message history
    messageHistory = null;
  }
}
