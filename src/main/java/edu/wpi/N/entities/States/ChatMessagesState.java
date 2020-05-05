package edu.wpi.N.entities.States;

import edu.wpi.N.chatbot.Dialogflow;
import java.io.IOException;
import java.util.LinkedList;
import javafx.scene.control.Label;

public class ChatMessagesState {
  private LinkedList<Label> messageHistory;
  private boolean sessionActive;
  public Dialogflow dialogflow;

  public ChatMessagesState() {
    messageHistory = new LinkedList<Label>();
    try {
      dialogflow = new Dialogflow();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Gets the current status of dialogflow session
   *
   * @return
   */
  public boolean getSessionStatus() {
    return this.sessionActive;
  }

  public LinkedList<Label> getMessageHistory() {
    return messageHistory;
  }

  public void setMessageHistory(LinkedList<Label> messageHistory) {
    this.messageHistory = messageHistory;
  }

  /** Delete previous chat history. Called when a User session is closed */
  public void eraseChatHistory() {
    messageHistory = null;
  }

  /**
   * Adds a single message to message history
   *
   * @param message
   */
  public void addMessage(Label message) {
    messageHistory.add(message);
  }

  /**
   * Initializes Session with the end user
   *
   * @throws IOException
   */
  public void initSession() throws IOException {
    dialogflow.initializeSession();
    sessionActive = true;
  }

  /**
   * Closes the session with the end usr
   *
   * @throws IOException
   */
  public void closeSession() throws IOException {
    dialogflow.closeSession();
    sessionActive = false;
  }
}
