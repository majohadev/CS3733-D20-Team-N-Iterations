package edu.wpi.N.chatbot;

import java.util.LinkedList;

public class ChatbotMain {

  public static void main(String[] args) throws Exception {

    Dialogflow dialogflow = new Dialogflow();
    dialogflow.initializeSession();

    // Test detecting intent
    LinkedList<String> userText = new LinkedList<String>();
    userText.add("Hello!");
    dialogflow.detectIntentTexts(userText, "en-US");

    dialogflow.closeSession();
  }
}
