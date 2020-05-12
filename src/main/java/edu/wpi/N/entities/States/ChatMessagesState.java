package edu.wpi.N.entities.States;

import com.google.cloud.dialogflow.v2.QueryResult;
import edu.wpi.N.chatbot.Dialogflow;
import edu.wpi.N.entities.DbNode;
import java.io.IOException;
import java.util.LinkedList;
import javafx.scene.layout.VBox;

public class ChatMessagesState {
  // each HBox contains a label with a message
  private LinkedList<VBox> messageHistory;
  public Dialogflow dialogflow;
  public QueryResult prevQueryResult;
  public DbNode startNodePrevSession;
  public DbNode endNodePrevSession;

  public ChatMessagesState() {
    messageHistory = new LinkedList<VBox>();
    prevQueryResult = null;
    try {
      dialogflow = new Dialogflow();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public LinkedList<VBox> getMessageHistory() {
    return messageHistory;
  }

  public void setMessageHistory(LinkedList<VBox> messageHistory) {
    this.messageHistory = messageHistory;
  }

  public void addMessageToHistory(VBox message) {
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
