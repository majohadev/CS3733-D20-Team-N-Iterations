package edu.wpi.N.entities.States;

import edu.wpi.N.chatbot.Dialogflow;
import java.io.IOException;
import java.util.LinkedList;
import javafx.scene.layout.HBox;

public class ChatMessagesState {
  // each HBox contains a label with a message
  private LinkedList<HBox> messageHistory;
  public Dialogflow dialogflow;

  public ChatMessagesState() {
    messageHistory = new LinkedList<HBox>();
    try {
      dialogflow = new Dialogflow();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public LinkedList<HBox> getMessageHistory() {
    return messageHistory;
  }

  public void setMessageHistory(LinkedList<HBox> messageHistory) {
    this.messageHistory = messageHistory;
  }

  public void addMessageToHistory(HBox message) {
    this.messageHistory.add(message);
  }

  /** Delete previous chat history. Called when a User session is closed */
  public void eraseChatHistory() {
    messageHistory.clear();
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
    eraseChatHistory();
  }
}
