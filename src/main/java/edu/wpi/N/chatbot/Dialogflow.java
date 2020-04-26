package edu.wpi.N.chatbot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.auth.Credentials;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class Dialogflow {
    String projectId;
    String privateKeyId;
    String privateKey;
    String clientEmail;
    String clientId;
    String tokenServerURI;

    public Dialogflow() {
        //data from json ApiKey (Not the best practice...)
        this.projectId = "experimenting11135";
        this.privateKeyId = "";
        this.privateKey = "";
        this.clientEmail = "";
        this.clientId = "";
        this.tokenServerURI = "";
    }



}
