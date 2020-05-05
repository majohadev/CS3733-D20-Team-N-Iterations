package edu.wpi.N.chatbot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.swing.*;
import okhttp3.*;

public class Dialogflow {
  private String projectId;
  private String privateKeyId;
  private PrivateKey privateKey;
  private String clientEmail;
  private String clientId;
  private String tokenServerURI;

  private SessionsSettings sessionsSettings;

  private SessionsClient sessionClient;

  private SessionName session;

  public Dialogflow() throws Exception {
    // data from json ApiKey (Not the best practice...)
    this.projectId = "software-engineering-275406";
    this.privateKeyId = "78de75058289a64596766982e5d2478baae7e39f";
    this.clientEmail = "dialogflow-lmtgnw@software-engineering-275406.iam.gserviceaccount.com";
    this.clientId = "105648504415043709863";
    this.tokenServerURI = "https://oauth2.googleapis.com/token";

    extractPrivateKey();

    // get credintials object
    Credentials myCredentials =
        ServiceAccountCredentials.newBuilder()
            .setProjectId(projectId)
            .setPrivateKeyId(privateKeyId)
            .setPrivateKey(privateKey)
            .setClientEmail(clientEmail)
            .setClientId(clientId)
            .setTokenServerUri(URI.create(tokenServerURI))
            .build();

    // Create session settings
    sessionsSettings =
        SessionsSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
            .build();
  }

  /**
   * Extracts private key from a txt file
   *
   * @throws Exception
   */
  private void extractPrivateKey() throws Exception {
    String path = getClass().getResource("../credentials/privatekey.txt").getPath();

    String privKey = Files.readString(Paths.get(path));

    StringBuilder pkcs8Lines = new StringBuilder();
    BufferedReader rdr = new BufferedReader(new StringReader(privKey));
    String line;
    while ((line = rdr.readLine()) != null) {
      pkcs8Lines.append(line);
    }

    // Remove the "BEGIN" and "END" lines, as well as any whitespace

    String pkcs8Pem = pkcs8Lines.toString();
    pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
    pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
    pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

    // Base64 decode the result

    byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);

    // extract the private key

    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
    KeyFactory kf;
    try {
      kf = KeyFactory.getInstance("RSA");
      try {
        privateKey = kf.generatePrivate(keySpec);
      } catch (InvalidKeySpecException e) {
        throw new Exception("Something went wrong when setting key value");
      }
    } catch (NoSuchAlgorithmException e) {
      throw new Exception("Something went wrong when setting key value\"");
    }
  }

  /**
   * Function initializes session with a user. Think opening a dialog window with the chatbot
   *
   * @throws IOException
   */
  public void initializeSession() throws IOException {
    SessionsClient sessionClient = SessionsClient.create(sessionsSettings);
    // set the attribute for persistent storage
    this.sessionClient = sessionClient;
    this.session = SessionName.of(projectId, "123456789");
    System.out.println("Session Path: " + session.toString());
  }

  /**
   * Function closes session with a user. Think closing a chat-bot dialog window
   *
   * @throws IOException
   */
  public void closeSession() throws IOException {
    System.out.println("Session" + session.toString() + " has been closed.");
    sessionClient.close();
  }

  /**
   * Returns the result of detect intent with text as input.
   *
   * <p>Using the same `session_id` between requests allows continuation of the conversation.
   *
   * @param text The text intent to be detected based on what a user says.
   * @param languageCode Language code of the query.
   * @return The QueryResult for input text.
   */
  public QueryResult detectIntentTexts(String text, String languageCode) throws Exception {

    // Set the text (hello) and language code (en-US) for the query
    TextInput.Builder textInput =
        TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

    // Build the query with the TextInput
    QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

    // Performs the detect intent request
    DetectIntentResponse response = this.sessionClient.detectIntent(session, queryInput);

    // Display the query result
    QueryResult queryResult = response.getQueryResult();

    System.out.println("====================");
    System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
    System.out.format(
        "Detected Intent: %s (confidence: %f)\n",
        queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
    System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());

    return queryResult;
  }

  /**
   * Call the function to get chatbot's reply to user's input
   *
   * @param userText: user input
   * @return: chatbot's reply for given user's text
   */
  public String replyToUserInput(String userText) throws Exception {
    try {
      QueryResult queryResults = detectIntentTexts(userText, "en-US");
      String message;

      // If intent matches with get-weather
      if (queryResults.getIntent().getDisplayName().equals("get-weather")) {
        message = getCurrentWeatherReply();
        System.out.println(message);
      } else {
        // else, use Dialogflow text
        message = queryResults.getFulfillmentText();
      }

      return message;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Gets the message containing current weather in Boston
   *
   * @return
   */
  public String getCurrentWeatherReply() {

    // initialize and send request
    String url =
        "http://api.openweathermap.org/data/2.5/weather?appid=495b2d2af36253b0fd2e15dacdab5067&lat=42.361145&lon=-71.057083";
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();

    ObjectMapper objectMapper = new ObjectMapper();

    // Ignore fields we don't need
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try (ResponseBody response = client.newCall(request).execute().body()) {

      // Convert the string-json response into object with our custom fields
      JSONResponseWeather jsResponse =
          objectMapper.readValue(response.string(), JSONResponseWeather.class);

      // get the necessary fields
      Double tempK = jsResponse.getMain().get("temp");
      int tempF = (int) Math.round((tempK - 273.15) * 9 / 5 + 32);

      String description = jsResponse.getWeather().get(0).getDescription();

      return "Current temperature outside is " + tempF + " F, " + description;
      // response.body();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
