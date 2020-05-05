package edu.wpi.N.chatbot;

public class ChatbotMain {

  public static void main(String[] args) throws Exception {

    Dialogflow dialogflow = new Dialogflow();
    dialogflow.initializeSession();

    // Test detecting intent
    String userText = "Hello!";
    dialogflow.detectIntentTexts(userText, "en-US");

    // Test weather
    System.out.println(dialogflow.getCurrentWeatherReply());

    dialogflow.closeSession();
  }
}
