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

  // For displaying path
  public DbNode startNode;
  public DbNode endNode;

  // For showing just the given node
  public DbNode whereIsNode;

  // If need to display guide for doctor's search
  public boolean showDoctorSearchGuide = false;

  // if need to display one of the separate requests upon loading services page
  public boolean showTranslator = false;
  public boolean showWheelChair = false;
  public boolean showSecurity = false;
  public boolean showSanitation = false;
  public boolean showLaundry = false;
  public boolean showITService = false;
  public boolean showInternalTransport = false;
  public boolean showFlower = false;
  public boolean showEmotional = false;

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

  /** Resets all previously planned actions. String and Structs to Null Boolean values to false */
  public void resetPreviouslyPlannedActions() {
    prevQueryResult = null;
    startNode = null;
    endNode = null;
    whereIsNode = null;
    showDoctorSearchGuide = false;
    showTranslator = false;
    showWheelChair = false;
    showSecurity = false;
    showSanitation = false;
    showLaundry = false;
    showITService = false;
    showInternalTransport = false;
    showFlower = false;
    showEmotional = false;
  }
}
