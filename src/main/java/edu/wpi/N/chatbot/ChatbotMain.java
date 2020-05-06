package edu.wpi.N.chatbot;

public class ChatbotMain {

  public static void main(String[] args) throws Exception {

    Dialogflow dialogflow = new Dialogflow();
    dialogflow.initializeSession();

    // Test detecting intent
    String userText = "Look up weather forecast";
    dialogflow.detectIntentTexts(userText, "en-US");

    // Test weather
    System.out.println(dialogflow.getCurrentWeatherReply());

    dialogflow.closeSession();
  }
}
