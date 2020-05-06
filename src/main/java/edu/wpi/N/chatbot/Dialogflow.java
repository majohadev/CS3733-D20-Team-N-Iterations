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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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
    // String path = this.getClass().getResource("privatekey.txt").getPath();

    // String path = Main.class.getResource("credentials/privatekey.txt").getPath();

    // String privKey = Files.readString(Paths.get(path));

    StringBuilder pkcs8Lines = new StringBuilder();
    BufferedReader rdr =
        new BufferedReader(
            new StringReader(
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCGPL0El3GiYe+3\n"
                    + "Ge7fueUQuI+3KfWu5AeVx0E6HN0X/ntU0U/jQLSwn+ODkJACVKgmgZawS8wLCkf4\n"
                    + "o6i7oa3uA6/ajshfIXiW9seCRg7tEpV8zZWsP7IbsalTBhxXE2CNJBE+2Ox+rRG7\n"
                    + "960Cmv/RYQF0J0NJw5RLH9gyktPnsv/zxuqIGAKJaL7Hbu145m13IsGhey1tkGRo\n"
                    + "+d2Bf/BIjWmJSwBz3bpnNSXfSsP0t6xXcoWRb8tH6UffFaQGhP7Yj2sDh3+9ysAc\n"
                    + "ToJSS6JSz5jJEuk/B6ejZUj3OytYYgMAddl6BSufNBfBkMI19+n6Cugjw2Ni+YV+\n"
                    + "ExtgCRVzAgMBAAECggEAFHl3KxgeUPI3KepesOkdsyszYHCZ9I77STEXFdIJNvut\n"
                    + "vAYg9TyNUtNQVGTcUj6vVxA097bX7GcpCxeLSPMkIEWXsPJORZRJXF1PmOMuq6JG\n"
                    + "Ar8osRkWBXtXpD4uXGmsNFrn9noHuFqVdeIVFWym0DzHh/vlG38lQ0AcLbqPb6t5\n"
                    + "KctiSdIla9aZBtnWfK8iHRE5E20FXJbE1T1sdHXyRvVBagQDbj7b/pXe4Oy9Usmd\n"
                    + "NgHhxHAdCbrkej7aHf7o4/bWkaLWdiVRuI5f+it3drVnavmgOylxEXN6f55+ojnl\n"
                    + "+9VBwCM76DhylDTUE1uIkXUurpFAsCO54zMIybK3IQKBgQC56ek2+2MWq66i7uDB\n"
                    + "HrzGNtSQ5Yu5Ji2PXkR3fx4rR6BGQ+nbT19Thz4F8Vij16/rh/PfvFKo780AXIl3\n"
                    + "Uur2s0pyRfuhTlD3kYzf5VQ2ErRFM4BznFnSahKECRkLN4+TPeyi1XyrpmXESKpd\n"
                    + "jwjR6EUaSSA8yU4oFYDvi3R+pQKBgQC416fq8Hkdf8nClS2ju9mTRSMcOrQe5396\n"
                    + "sExAKQsIy9+1XtnIlJukgeByoVRpRLPAaRO0hknc8cYR53gWPsrsuGGxU7BV2z/O\n"
                    + "INR4TvctF1Ahr5C9BEMvmu+BV59ALHyV7RGFlsfo4qtsp++A0Y82Dyq0dIFdRKH9\n"
                    + "p1f8LTRgNwKBgCmTZdQl48LuId/OC/UkKpMpL+A3dUeygf3N1wHiK50CJ5WPGn0x\n"
                    + "AuBrHjO2BVyen3jMrn0aYGHnPrEWAKfuox0Ie12SyPMJ0JGMuzRW1L5C8I2JNvCj\n"
                    + "xnKHxhxA7JjVlAYZkxHXLEo2rAt5NKA3nIFUsB9wuwTc/9128Z6gUS0BAoGAQLki\n"
                    + "zx1NC4tHdp8N0Bti4DKBTM0xbPDarnc0+/JjLr1UpAmGcLm4li30Da94AzRPTLx+\n"
                    + "bePn0TnHhVVrsz5hh+o4KwOG50lSWjccrtPXsS5MnL8BC26GqBC0dLHdzXbGWSkd\n"
                    + "zY0yitVz+SQEtTKr1t4QsgdplOMHZe5G3MJnaJkCgYBni56LfOx7Trs6H1ggZPeF\n"
                    + "pPdghhvK5tO3SI6l4X4vyjn+E4KG2aePKQc2rAhjmKzK01PHxgsjKHFIm4Fvjs/4\n"
                    + "yPDXFDdFhLH/8qem9o11GaUZpB1MbOxLq/+7sj484QsRYarsQZ0l5l0sKgCYJ+nI\n"
                    + "Ak/tP14RKUcFuYIeVfCn5A=="));
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
